package net.twlghtdrgn.minichat.message;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.twlghtdrgn.minichat.PlayerCache;
import net.twlghtdrgn.vanishbridge.VanishBridge;
import net.twlghtdrgn.minichat.MiniChatVelocity;
import net.twlghtdrgn.minichat.command.MiniChatCommand;
import net.twlghtdrgn.minichat.config.Lang;
import net.twlghtdrgn.minichat.config.MainConfig;
import net.twlghtdrgn.minichat.util.ReplaceUtil;

import java.util.*;

public class PrivateMessage {
    @Getter
    private static final Map<Player, String> recentRecipients = new HashMap<>();

    private final CommandSource sender;
    private final String recipients;
    private String message;

    public PrivateMessage(CommandSource sender, String recipients, String message) {
        this.sender = sender;
        this.recipients = recipients;
        this.message = message;
    }

    public void send() {
        String[] splitPlayers = recipients.split(",");
        if (splitPlayers.length > MainConfig.getConfig().getMaxRecipients()) {
            sender.sendMessage(Component.text(Lang.getConfig().getDirectMessageOverLimit()
                    .replace("%current%", String.valueOf(splitPlayers.length))
                    .replace("%max%", String.valueOf(MainConfig.getConfig().getMaxRecipients()))));
            return;
        }

        boolean unknownRecipient = false;
        List<Player> players = new ArrayList<>();
        for (String s:splitPlayers) {
            Optional<Player> recipient = MiniChatVelocity.getPlugin().getServer().getPlayer(s);
            if (recipient.isEmpty()) {
                sender.sendMessage(Component.text("You have no one to reply"));
                return;
            }
            if (!recipient.get().isActive() || VanishBridge.getPlugin().isVanished(recipient.get().getUsername())) {
                unknownRecipient = true;
                break;
            } else {
                if (sender instanceof Player p
                        && recipient.get().equals(p)) {
                    sender.sendMessage(Component.text("You cannot message to yourself!"));
                    return;
                }
                if (!players.contains(recipient.get())) {
                    players.add(recipient.get());
                }
            }
        }

        if (unknownRecipient) {
            sender.sendMessage(Component.text(Lang.getConfig().getDirectMessagePlayerNotFound()
                    .replace("%player%", splitPlayers[players.size()])));
            return;
        }


        if (MainConfig.getConfig().isDirectMessageLoggingEnabled())
            MiniChatVelocity.getPlugin().getLogger().info(Lang.getConfig().getDirectMessageConsoleLogFormat()
                    .replace("%sender%", sender instanceof Player p ? p.getUsername() : "Console")
                    .replace("%recipients%", recipients)
                    .replace("%message%", message));

        if (MainConfig.getConfig().isEmojiReplacer())
            message = ReplaceUtil.replaceEmojis(message);

        for (Player p:players) p.sendMessage(Component.text(Lang.getConfig().getDirectMessageInFormat()
                .replace("%sender%", sender instanceof Player pl ? pl.getUsername() : "Console")
                .replace("%recipients%", players.size() != 1 ? recipients : "You")
                .replace("%message%", message)));

        sender.sendMessage(Component.text(Lang.getConfig().getDirectMessageOutFormat()
                .replace("%recipients%", recipients)
                .replace("%message%", message)));

        if (sender instanceof Player p)
            PlayerCache.setLastRecipients(p.getUniqueId(), players);

        for (Player p:MiniChatCommand.getSocialSpyPlayers()) {
            if (p == sender) continue;
            p.sendMessage(Component.text(Lang.getConfig().getSocialSpyFormat()
                    .replace("%sender%", sender instanceof Player pl ? pl.getUsername() : "Console")
                    .replace("%recipients%", recipients)
                    .replace("%message%", message)
            ));
        }
    }
}
