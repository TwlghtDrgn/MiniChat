package net.twlghtdrgn.minichat.messaging;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.twlghtdrgn.minichat.MiniChat;
import net.twlghtdrgn.minichat.PlayerCache;
import net.twlghtdrgn.minichat.config.Configuration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

public class ProxySyncMessaging implements PluginMessageListener {
    public static void sendMessage(@NotNull Player p, @NotNull String syncType) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF(syncType);

        if (syncType.equals(SyncType.SPY)) {
            out.writeBoolean(PlayerCache.isLocalSpy(p.getUniqueId()));
        } else if (syncType.equals(SyncType.SETTINGS)) {
            out.writeBoolean(Configuration.getConfig().getGlobalChat().isEnabled());
            out.writeUTF(Configuration.getConfig().getGlobalChat().getPrefix());
            out.writeBoolean(Configuration.getConfig().getEnable().isProxyChatLoggingEnabled());
        } else return;

        p.sendPluginMessage(MiniChat.getPlugin(), MessageChannel.PROXY, out.toByteArray());
    }

    @Override
    public void onPluginMessageReceived(@NotNull String channel, @NotNull Player player, byte @NotNull [] bytes) {
        if (!channel.equals(MessageChannel.PROXY)) return;
        ByteArrayDataInput in = ByteStreams.newDataInput(bytes);
        String sub = in.readUTF();
        if (sub.equals(SyncType.SPY)) {
            if (in.readBoolean() != PlayerCache.isLocalSpy(player.getUniqueId())) PlayerCache.setLocalSpy(player.getUniqueId());
        } else if (sub.equals(SyncType.RESYNC)) {
            Bukkit.getAsyncScheduler().runDelayed(MiniChat.getPlugin(), task ->
                    sendMessage(player, SyncType.SETTINGS), 2, TimeUnit.SECONDS);
        }
    }
}
