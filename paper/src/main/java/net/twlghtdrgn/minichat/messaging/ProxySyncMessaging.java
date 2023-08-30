package net.twlghtdrgn.minichat.messaging;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.twlghtdrgn.minichat.MiniChat;
import net.twlghtdrgn.minichat.PlayerCache;
import net.twlghtdrgn.minichat.config.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

public class ProxySyncMessaging implements PluginMessageListener {
    public static final String PROXY_SYNC_CHANNEL = "minichat:sync";

    public static void sendMessage(@NotNull Player p, @NotNull Values v) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();

        if (v == Values.SPY) {
            out.writeUTF("spy");
            out.writeBoolean(PlayerCache.isLocalSpy(p.getUniqueId()));
        } else if (v == Values.SETTINGS) {
            out.writeUTF("settings");
            out.writeBoolean(Configuration.getConfig().getGlobalChat().isEnabled());
            out.writeUTF(Configuration.getConfig().getGlobalChat().getPrefix());
            out.writeBoolean(Configuration.getConfig().getDisable().isProxyChatLoggingDisabled());
        } else return;

        p.sendPluginMessage(MiniChat.getPlugin(), PROXY_SYNC_CHANNEL, out.toByteArray());
    }

    @Override
    public void onPluginMessageReceived(@NotNull String channel, @NotNull Player player, byte @NotNull [] bytes) {
        if (!channel.equals(PROXY_SYNC_CHANNEL)) return;
        ByteArrayDataInput out = ByteStreams.newDataInput(bytes);
        String sub = out.readUTF();
        if (sub.equals("spy")) {
            boolean spy = out.readBoolean();
            if (spy != PlayerCache.isLocalSpy(player.getUniqueId())) PlayerCache.setLocalSpy(player.getUniqueId());
        }
    }

    public enum Values {
        SPY,
        SETTINGS
    }
}
