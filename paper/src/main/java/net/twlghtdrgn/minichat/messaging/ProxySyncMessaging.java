package net.twlghtdrgn.minichat.messaging;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import lombok.Getter;
import net.twlghtdrgn.minichat.MiniChat;
import net.twlghtdrgn.minichat.PlayerCache;
import net.twlghtdrgn.minichat.config.Configuration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

public class ProxySyncMessaging implements PluginMessageListener {
    public static final String PROXY_SYNC_CHANNEL = "minichat:sync";

    public static void sendMessage(@NotNull Player p, @NotNull SyncID v) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF(v.getName());

        if (v == SyncID.SPY) {
            out.writeBoolean(PlayerCache.isLocalSpy(p.getUniqueId()));
        } else if (v == SyncID.SETTINGS) {
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
        if (sub.equals(SyncID.SPY.getName())) {
            if (out.readBoolean() != PlayerCache.isLocalSpy(player.getUniqueId())) PlayerCache.setLocalSpy(player.getUniqueId());
        } else if (sub.equals(SyncID.RESYNC.getName())) {
            Bukkit.getAsyncScheduler().runDelayed(MiniChat.getPlugin(), task ->
                    sendMessage(player, SyncID.SETTINGS), 2, TimeUnit.SECONDS);
        }
    }

    public enum SyncID {
        SETTINGS("settings"),
        RESYNC("resync"),
        SPY("spy");

        @Getter
        private final String name;
        SyncID(String name) {
            this.name = name;
        }
    }
}
