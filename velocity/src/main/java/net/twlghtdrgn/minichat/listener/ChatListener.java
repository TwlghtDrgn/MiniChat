package net.twlghtdrgn.minichat.listener;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.twlghtdrgn.minichat.MiniChatVelocity;
import net.twlghtdrgn.minichat.PlayerCache;
import net.twlghtdrgn.minichat.command.MiniChatCommand;
import net.twlghtdrgn.minichat.config.Lang;
import net.twlghtdrgn.minichat.config.MainConfig;
import net.twlghtdrgn.minichat.message.PrivateMessage;
import net.twlghtdrgn.minichat.util.ReplaceUtil;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class ChatListener {
    @Getter
    private static final List<Player> directMessageChatMode = new ArrayList<>();

    @Subscribe
    public void onPlayerChatEvent(@NotNull PlayerChatEvent e) {
        String message = e.getMessage();
        Player player = e.getPlayer();

        if (directMessageChatMode.contains(player)) {
            e.setResult(PlayerChatEvent.ChatResult.denied());

            Optional<HashSet<UUID>> lastRecipients = PlayerCache.getLastRecipients(player.getUniqueId());
            if (lastRecipients.isEmpty()) {
                player.sendMessage(Component.text(Lang.getConfig().getDirectMessagePlayerNotFound()));
                return;
            }

            StringBuilder recipientList = new StringBuilder();
            for (UUID last:lastRecipients.get()) {
                MiniChatVelocity.getPlugin().getServer().getPlayer(last).ifPresent(r -> recipientList.append(r.getUsername()).append(","));
            }

            new PrivateMessage(player,
                    recipientList.toString(),
                    message);
            return;
        }

        if (MainConfig.getConfig().isEmojiReplacer())
            message = ReplaceUtil.replaceEmojis(message);

        e.setResult(PlayerChatEvent.ChatResult.message(message));

        Optional<ServerConnection> serverConnection = e.getPlayer().getCurrentServer();
        String server = serverConnection.isPresent() ? serverConnection.get().getServerInfo().getName() : "unknown";
        boolean channeled = Arrays.asList(MainConfig.getConfig().getChanneled()).contains(server);

        String channel = message.startsWith(MainConfig.getConfig().getGlobalChatPrefix()) ? "G" : "L";

        if (message.startsWith("!"))
            message = message.replaceFirst("!","");

        if (MainConfig.getConfig().isNetworkLoggingEnabled()) {
            MiniChatVelocity.getPlugin().getLogger().info(Lang.getConfig().getNetworkMessageConsoleLogFormat()
                    .replace("%server%", server)
                    .replace("%channel_type%", channeled ? channel : "" )
                    .replace("%sender%", player.getUsername())
                    .replace("%message%", message));
        }

        channel = channel
                .replace("G", MainConfig.getConfig().getGlobalChatSymbol())
                .replace("L", MainConfig.getConfig().getLocalChatSymbol());

        for (Player p:MiniChatCommand.getNetworkSpyPlayers())
            if (p.getCurrentServer().isPresent()
                    && !p.getCurrentServer().get().getServerInfo().getName().equals(server)
                    && p != player)
                p.sendMessage(Component.text(Lang.getConfig().getNetworkSpyFormat()
                        .replace("%server%", server)
                        .replace("%channel_type%", channeled ? "" : channel )
                        .replace("%sender%", player.getUsername())
                        .replace("%message%", message)));
    }
}
