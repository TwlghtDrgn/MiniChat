package net.twlghtdrgn.minichat.messaging;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.twlghtdrgn.minichat.config.Configuration;
import net.twlghtdrgn.twilightlib.api.redis.RedisConnector;
import net.twlghtdrgn.twilightlib.api.util.Format;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import redis.clients.jedis.BinaryJedisPubSub;
import redis.clients.jedis.Jedis;

import java.util.Arrays;

public class RedisMessaging {
    private BinaryJedisPubSub crossserverListener;
    private BinaryJedisPubSub proxyListener;
    public RedisMessaging() {
        try (Jedis redis = RedisConnector.getResource()) {
            if (redis == null) return;
            crossserverListener = new BinaryJedisPubSub() {
                @Override
                public void onMessage(byte[] channel, byte[] message) {
                    final Configuration.Config.CrossServer cfg = Configuration.getConfig().getCrossServer();
                    if (!Arrays.equals(channel, MessageChannel.SERVER.getBytes())) return;
                    if (!cfg.isCrossServerEnabled()) return;

                    ByteArrayDataInput in = ByteStreams.newDataInput(message);
                    if (!in.readUTF().equals(cfg.getServerId())) return;
                    String msg = in.readUTF();
                    Bukkit.broadcast(Format.parse(msg));
                }
            };

            proxyListener = new BinaryJedisPubSub() {
                @Override
                public void onMessage(byte[] channel, byte[] message) {
                    if (!Arrays.equals(channel, MessageChannel.PROXY.getBytes())) return;

                    ByteArrayDataInput out = ByteStreams.newDataInput(message);
                    final String syncType = out.readUTF();
                    if (!syncType.equals(SyncType.RESYNC)) return;
                    sendSync();
                }
            };

            redis.subscribe(crossserverListener);
            redis.subscribe(proxyListener);
        }
    }

    private void sendSync() {
        final Configuration.Config cfg = Configuration.getConfig();
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeBoolean(cfg.getGlobalChat().isEnabled());
        out.writeUTF(cfg.getGlobalChat().getPrefix());
        out.writeBoolean(cfg.getEnable().isProxyChatLoggingEnabled());

        RedisConnector.sendMessage(MessageChannel.PROXY, out.toByteArray());
    }

    public void sendCrossServer(@NotNull String message) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF(Configuration.getConfig().getCrossServer().getServerId());
        out.writeUTF(message);

        RedisConnector.sendMessage(MessageChannel.SERVER, out.toByteArray());
    }

    public void unsubscribe() {
        if (!RedisConnector.isEnabled()) return;
        crossserverListener.unsubscribe();
        proxyListener.unsubscribe();
    }
}
