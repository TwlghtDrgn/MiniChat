package net.twlghtdrgn.minichat.listener;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;
import net.twlghtdrgn.minichat.PlayerCache;
import net.twlghtdrgn.minichat.config.Language;
import net.twlghtdrgn.minichat.message.PrivateMessage;
import net.twlghtdrgn.minichat.message.SpyMessage;
import net.twlghtdrgn.minichat.message.StaffMessage;
import net.twlghtdrgn.twilightlib.api.util.Format;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public class ChatListener {
    @Subscribe
    public void onPlayerChatEvent(@NotNull PlayerChatEvent e) {
        String message = e.getMessage();
        Player player = e.getPlayer();

        if (PlayerCache.isStaffChatEnabled(player.getUniqueId())) {
            e.setResult(PlayerChatEvent.ChatResult.denied());

            new StaffMessage(player, message).send();
            return;
        }

        if (PlayerCache.isChatModeEnabled(player.getUniqueId())) {
            e.setResult(PlayerChatEvent.ChatResult.denied());

            Optional<Set<UUID>> lastRecipients = PlayerCache.getLastRecipients(player.getUniqueId());
            if (lastRecipients.isEmpty()) {
                player.sendMessage(Format.parse(Language.getConfig().getDirectMessage().getPlayerNotFound()));
                return;
            }

            new PrivateMessage(player,
                    lastRecipients.get(),
                    message).send();
            return;
        }

//        if (Configuration.getConfig().getEmojis().isEmojiReplacerEnabled())
//            message = EmojiUtil.replaceEmojis(message);
//
//        e.setResult(PlayerChatEvent.ChatResult.message(message));

        Optional<ServerConnection> serverConnection = player.getCurrentServer();
        serverConnection.ifPresent(connection -> new SpyMessage(true,
                message,
                player.getUsername(),
                connection.getServerInfo().getName()).send());
    }
}
