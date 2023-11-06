package net.twlghtdrgn.minichat.messaging;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.twlghtdrgn.minichat.MiniChat;
import net.twlghtdrgn.minichat.ServerCache;
import net.twlghtdrgn.twilightlib.api.redis.RedisConnector;
import redis.clients.jedis.BinaryJedisPubSub;
import redis.clients.jedis.Jedis;

import java.util.Arrays;

public class RedisMessaging {
    private final MiniChat plugin;
    private BinaryJedisPubSub proxyListener;
    public RedisMessaging(MiniChat plugin) {
        this.plugin = plugin;
        if (!RedisConnector.isEnabled()) return;
        proxyListener = new BinaryJedisPubSub() {
            @Override
            public void onMessage(byte[] channel, byte[] message) {
                if (!Arrays.equals(channel, MessageChannel.PROXY_CHANNEL.getBytes())) return;
                ByteArrayDataInput in = ByteStreams.newDataInput(message);
                String sub = in.readUTF();
                if (!sub.equals(SyncType.SETTINGS)) return;
                final String serverName = in.readUTF();
                final boolean isGlobalChatEnabled = in.readBoolean();
                final String globalChatPrefix = in.readUTF();
                final boolean proxyChatLoggingEnabled = in.readBoolean();

                ServerCache.CachedServer cachedServer = new ServerCache.CachedServer(isGlobalChatEnabled, globalChatPrefix, proxyChatLoggingEnabled);
                ServerCache.addCachedServer(serverName, cachedServer);
            }
        };

        plugin.getServer().getScheduler().buildTask(plugin, task -> {
            try (Jedis jedis = RedisConnector.getResource()) {
                jedis.subscribe(proxyListener, MessageChannel.PROXY_CHANNEL.getBytes());
            } catch (Exception e) {
                plugin.log().error("Unable to use Redis (Proxy messaging)", e);
            }
        }).schedule();
    }

    public void sendSyncRequest() {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF(SyncType.RESYNC);
        if (RedisConnector.sendMessage(MessageChannel.PROXY_CHANNEL, out.toByteArray())) plugin.log().info("Sync request has sent successfully");
    }

    public void unsubscribe() {
        if (!RedisConnector.isEnabled()) return;
        proxyListener.unsubscribe();
    }
}
