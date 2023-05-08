package net.twlghtdrgn.minichat;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.twlghtdrgn.minichat.config.Config;
import net.twlghtdrgn.twilightlib.util.Format;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

public class ProxyMessaging implements PluginMessageListener {
    public static final String PROXY_CHANNEL = "BungeeCord";

    public static void sendMessage(@NotNull Player p,String message) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Forward");
        out.writeUTF("ALL");
        out.writeUTF("MiniChat");
        out.writeUTF(message);
        p.sendPluginMessage(MiniChat.getPlugin(), PROXY_CHANNEL, out.toByteArray());
    }

    @Override
    public void onPluginMessageReceived(@NotNull String channel, @NotNull Player player, byte @NotNull [] message) {
        if (!Config.isCrossServerEnabled()) return;
        if (!channel.equals(PROXY_CHANNEL)) return;
        ByteArrayDataInput in = ByteStreams.newDataInput(message);
        if (!in.readUTF().equals("MiniChat")) return;

        String msg = in.readUTF();
        Bukkit.broadcast(Format.parse(msg));
    }
}
