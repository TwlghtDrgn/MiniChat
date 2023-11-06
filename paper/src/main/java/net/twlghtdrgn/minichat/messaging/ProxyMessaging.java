package net.twlghtdrgn.minichat.messaging;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.twlghtdrgn.minichat.MiniChat;
import net.twlghtdrgn.minichat.PlayerCache;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

public class ProxyMessaging implements PluginMessageListener {
    public static void sendMessage(@NotNull Player p, @NotNull String syncType) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF(syncType);
        if (!syncType.equals(SyncType.SPY)) return;
        out.writeBoolean(PlayerCache.isLocalSpy(p.getUniqueId()));
        p.sendPluginMessage(MiniChat.getPlugin(), MessageChannel.PROXY, out.toByteArray());
    }

    @Override
    public void onPluginMessageReceived(@NotNull String channel, @NotNull Player player, byte @NotNull [] bytes) {
        if (!channel.equals(MessageChannel.PROXY)) return;
        ByteArrayDataInput in = ByteStreams.newDataInput(bytes);
        String sub = in.readUTF();
        if (!sub.equals(SyncType.SPY)) return;
        if (in.readBoolean() != PlayerCache.isLocalSpy(player.getUniqueId()))
            PlayerCache.setLocalSpy(player.getUniqueId());
    }
}
