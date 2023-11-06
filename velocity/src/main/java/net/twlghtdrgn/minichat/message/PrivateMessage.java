package net.twlghtdrgn.minichat.message;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import net.kyori.adventure.text.Component;
import net.twlghtdrgn.minichat.ChatPermission;
import net.twlghtdrgn.minichat.MiniChat;
import net.twlghtdrgn.minichat.PlayerCache;
import net.twlghtdrgn.minichat.event.PrivateMessageEvent;
import net.twlghtdrgn.minichat.sql.Database;
import net.twlghtdrgn.minichat.util.EmojiUtil;
import net.twlghtdrgn.twilightlib.api.util.Format;
import net.twlghtdrgn.vanishbridge.VanishBridge;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class PrivateMessage {
    private static final ProxyServer server = MiniChat.getPlugin().getServer();
    private final Player sender;
    private final Set<UUID> recipients;
    private Player unknownPlayer;
    private String message;

    public PrivateMessage(Player sender, Set<UUID> recipients, String message) {
        this.sender = sender;
        this.recipients = recipients;
        this.message = message;
    }

    public void send() {
        if (recipients.size() > MiniChat.getPlugin().getConf().get().getMaxRecipients()) {
            sender.sendMessage(Format.parse(MiniChat.getPlugin().getLang().get().getDirectMessage().getRecipientLimit()
                    .replace("%current%", String.valueOf(recipients.size()))
                    .replace("%max%", String.valueOf(MiniChat.getPlugin().getConf().get().getMaxRecipients()))));
            return;
        }

        final Set<Player> players = getPlayers();

        if (players.isEmpty()) {
            String errorMessage = MiniChat.getPlugin().getLang().get().getDirectMessage().getPlayerOffline();
            errorMessage = unknownPlayer != null
                    ? errorMessage.replace("%player%", unknownPlayer.getUsername())
                    : MiniChat.getPlugin().getLang().get().getDirectMessage().getPlayerNotFound();

            sender.sendMessage(Format.parse(errorMessage));
            return;
        }

        for (Player p:players) {
            String nick = p.getUsername();
            UUID uuid = p.getUniqueId();
            if (Database.isBlocked(sender.getUniqueId(), nick)) {
                sender.sendMessage(Format.parse(MiniChat.getPlugin().getLang().get().getDirectMessage().getBlockedError()
                        .replace("%player%",nick)));
                return;
            }
            if (Database.isBlocked(uuid, sender.getUsername())) {
                sender.sendMessage(Format.parse(MiniChat.getPlugin().getLang().get().getDirectMessage().getBlocked()
                        .replace("%player%", nick)));
                return;
            }
        }

        String recipientNicknames = players.stream()
                .map(Player::getUsername)
                .collect(Collectors.joining(", "));

        if (MiniChat.getPlugin().getConf().get().getEmojis().isEmojiReplacerEnabled())
            message = EmojiUtil.replaceEmojis(message);

        Component formattedReceiverMessage = Format.parse(MiniChat.getPlugin().getLang().get().getDirectMessage().getReceiverFormat()
                .replace("%sender%", sender.getUsername())
                .replace("%recipients%", players.size() > 1 ? recipientNicknames : "You")
                .replace("%message%", message));

        Component formattedHoverableReceiverMessage = Format.parse(
                "<hover:show_text:'" + MiniChat.getPlugin().getLang().get().getClickToReply() + "'>"
                        + "<click:suggest_command:/r >"
                            + MiniChat.getPlugin().getLang().get().getDirectMessage().getReceiverFormat()
                                .replace("%sender%", sender.getUsername())
                                .replace("%recipients%", players.size() > 1 ? recipientNicknames : "You")
                                .replace("%message%", message));

        Component formattedSenderMessage = Format.parse(MiniChat.getPlugin().getLang().get().getDirectMessage().getSenderFormat()
                .replace("%recipients%", recipientNicknames)
                .replace("%message%", message));

        server.getEventManager().fire(new PrivateMessageEvent(sender, players, message)).thenAcceptAsync(event -> {
            if (!event.getResult().isAllowed()) return;

            players.forEach(p -> {
                if (PlayerCache.isChatModeEnabled(p.getUniqueId())) {
                    p.sendMessage(formattedReceiverMessage);
                } else p.sendMessage(formattedHoverableReceiverMessage);

                final Set<Player> from = new HashSet<>(players);
                from.removeIf(player -> player.getUniqueId().equals(p.getUniqueId()));
                from.add(sender);
                PlayerCache.setLastRecipients(p.getUniqueId(), from);
            });

            sender.sendMessage(formattedSenderMessage);
            PlayerCache.setLastRecipients(sender.getUniqueId(), players);

            new SpyMessage(false,
                    message,
                    sender.getUsername(),
                    players.stream().map(Player::getUsername).toList()).send();
        });
    }

    private @NotNull Set<Player> getPlayers() {
        final Set<Player> players = new HashSet<>();
        for (UUID uuid:recipients) {
            Optional<Player> recipient = server.getPlayer(uuid);
            if (recipient.isEmpty()) continue;
            Player player = recipient.get();
            if (player.equals(sender)) continue;
            if (checkOffline(player)) {
                unknownPlayer = player;
                continue;
            }
            if (MiniChat.getPlugin().isVanishBridgeInstalled()
                    && checkVanished(player)
                    && (!sender.hasPermission(ChatPermission.VANISHED))) {
                unknownPlayer = player;
                continue;
            }
            players.add(player);
        }
        return players;
    }

    private boolean checkOffline(@NotNull Player p) {
        return !p.isActive();
    }

    private boolean checkVanished(@NotNull Player p) {
        return VanishBridge.getPlugin().isVanished(p.getUsername());
    }
}
