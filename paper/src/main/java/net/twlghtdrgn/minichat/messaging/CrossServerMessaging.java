package net.twlghtdrgn.minichat.messaging;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.twlghtdrgn.minichat.MiniChat;
import net.twlghtdrgn.twilightlib.api.util.Format;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

public class CrossServerMessaging implements PluginMessageListener {
    public static void sendMessage(@NotNull Player p, String message) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Forward");
        out.writeUTF("ALL");
        out.writeUTF(MiniChat.getPlugin().getConf().get().getCrossServer().getServerId());
        out.writeUTF(message);
        p.sendPluginMessage(MiniChat.getPlugin(), MessageChannel.SERVER, out.toByteArray());
    }

    @Override
    public void onPluginMessageReceived(@NotNull String channel, @NotNull Player player, byte @NotNull [] message) {
        if (!MiniChat.getPlugin().getConf().get().getCrossServer().isCrossServerEnabled()) return;
        if (!channel.equals(MessageChannel.SERVER)) return;
        ByteArrayDataInput in = ByteStreams.newDataInput(message);
        if (!in.readUTF().equals(MiniChat.getPlugin().getConf().get().getCrossServer().getServerId())) return;
        String msg = in.readUTF();
        Bukkit.broadcast(Format.parse(msg));
    }
}
