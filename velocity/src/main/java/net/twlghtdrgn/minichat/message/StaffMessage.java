package net.twlghtdrgn.minichat.message;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.ServerConnection;
import net.kyori.adventure.text.Component;
import net.twlghtdrgn.minichat.MiniChat;
import net.twlghtdrgn.minichat.config.Configuration;
import net.twlghtdrgn.minichat.config.Language;
import net.twlghtdrgn.minichat.util.EmojiUtil;
import net.twlghtdrgn.twilightlib.api.util.Format;

import java.util.Optional;

public class StaffMessage {
    private static final ProxyServer server = MiniChat.getPlugin().getServer();
    private final Player sender;
    private String message;
    public StaffMessage(Player sender, String message) {
        this.sender = sender;
        this.message = message;
    }

    public void send() {
        if (Configuration.getConfig().getEmojis().isEmojiReplacerEnabled())
            message = EmojiUtil.replaceEmojis(message);

        Optional<ServerConnection> srv = sender.getCurrentServer();
        String serverName = srv.isPresent() ? srv.get().getServerInfo().getName() : "n/a";

        Component staffMessage = Format.parse(Language.getConfig().getStaffChatFormat()
                .replace("%server%", serverName)
                .replace("%sender%", sender.getUsername())
                .replace("%message%", message));

        server.getAllPlayers().stream()
                .filter(player -> player.hasPermission("minichat.command.staff"))
                .forEach(player -> player.sendMessage(staffMessage));
    }
}
