package net.twlghtdrgn.minichat.messaging;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.twlghtdrgn.minichat.MiniChat;
import net.twlghtdrgn.minichat.config.Config;
import net.twlghtdrgn.twilightlib.api.redis.RedisConnector;
import net.twlghtdrgn.twilightlib.api.util.Format;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import redis.clients.jedis.BinaryJedisPubSub;
import redis.clients.jedis.Jedis;

import java.util.Arrays;
import java.util.UUID;

public class RedisMessaging {
    private final MiniChat plugin;
    private BinaryJedisPubSub crossserverListener;
    private BinaryJedisPubSub proxyListener;

    public RedisMessaging(MiniChat plugin) {
        this.plugin = plugin;
        if (!RedisConnector.isEnabled()) return;
        crossserverListener = new BinaryJedisPubSub() {
            @Override
            public void onMessage(byte[] channel, byte[] message) {
                final Config.CrossServer cfg = MiniChat.getPlugin().getConf().get().getCrossServer();
                if (!Arrays.equals(channel, MessageChannel.SERVER.getBytes())) return;
                if (!cfg.isCrossServerEnabled()) return;

                ByteArrayDataInput in = ByteStreams.newDataInput(message);
                if (!in.readUTF().equals(cfg.getServerId())) return;
                if (Bukkit.getOfflinePlayer(UUID.fromString(in.readUTF())).isOnline()) return;
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
                plugin.log().info("Received re-sync request from the proxy");
                sendSync();
            }
        };

        plugin.getServer().getAsyncScheduler().runNow(plugin, task -> {
            try (Jedis redis = RedisConnector.getResource()){
                redis.subscribe(crossserverListener, MessageChannel.SERVER.getBytes());
            } catch (Exception e) {
                plugin.log().error("Unable to use Redis (Cross-Server)", e);
            }
        });

        if (plugin.getConf().get().getProxySync().isEnabled()) {
            plugin.getServer().getAsyncScheduler().runNow(plugin, task -> {
                try (Jedis redis = RedisConnector.getResource()) {
                    redis.subscribe(proxyListener, MessageChannel.PROXY.getBytes());
                } catch (Exception e) {
                    plugin.log().error("Unable to use Redis (Proxy messaging)", e);
                }
            });
        }
    }

    public void sendSync() {
        final Config cfg = MiniChat.getPlugin().getConf().get();
        if (!cfg.getProxySync().isEnabled()) return;
        if (cfg.getProxySync().getServerName().isEmpty()) {
            throw new IllegalStateException("Server name is empty");
        }
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF(SyncType.SETTINGS);
        out.writeUTF(cfg.getProxySync().getServerName());
        out.writeBoolean(cfg.getGlobalChat().isEnabled());
        out.writeUTF(cfg.getGlobalChat().getPrefix());
        out.writeBoolean(cfg.getEnable().isProxyChatLoggingEnabled());

        if (RedisConnector.sendMessage(MessageChannel.PROXY, out.toByteArray())) plugin.log().info("Synchronization message sent successfully");
    }

    public void sendCrossServer(@NotNull Player player, @NotNull String message) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF(plugin.getConf().get().getCrossServer().getServerId());
        out.writeUTF(player.getUniqueId().toString());
        out.writeUTF(message);

        RedisConnector.sendMessage(MessageChannel.SERVER, out.toByteArray());
    }

    public void unsubscribe() {
        if (!RedisConnector.isEnabled()) return;
        crossserverListener.unsubscribe();
        proxyListener.unsubscribe();
    }
}
