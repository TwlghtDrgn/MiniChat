package net.twlghtdrgn.minichat.message;

import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;
import net.twlghtdrgn.minichat.MiniChat;
import net.twlghtdrgn.minichat.PlayerCache;
import net.twlghtdrgn.minichat.ServerCache;
import net.twlghtdrgn.minichat.event.NetworkMessageEvent;
import net.twlghtdrgn.twilightlib.api.util.Format;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class SpyMessage {
    private final boolean isNetwork;
    private String message;
    private final String sender;
    private List<String> nicknames = new ArrayList<>();
    private String serverName = "";
    private ServerCache.CachedServer cachedServer = new ServerCache.CachedServer(false, "!", false);

    public SpyMessage(boolean isNetwork, String message, String sender, Object value) {
        this.isNetwork = isNetwork;
        this.message = message;
        this.sender = sender;
        if (value instanceof String serverName) {
            this.serverName = serverName;
            if (ServerCache.isCached(serverName)) this.cachedServer = ServerCache.getCachedServer(serverName).get();
        } else if (value instanceof List<?> recipients) {
            nicknames = (List<String>) recipients;
        } else throw new IllegalArgumentException("Value should be a list of players, or server name");
    }

    public void send() {
        final List<Player> spies = getPlayers();

        String channelType = "";
        String recipients = "";
        if (isNetwork) {
            if (!cachedServer.isLoggingEnabled()) return;
            if (cachedServer.isChanneled()) {
                if (message.startsWith(cachedServer.getGlobalChatPrefix())) {
                    message = message.replaceFirst(cachedServer.getGlobalChatPrefix(), "");
                    channelType = MiniChat.getPlugin().getConf().get().getSpy().getGlobalChatSymbol();
                } else {
                    channelType = MiniChat.getPlugin().getConf().get().getSpy().getLocalChatSymbol();
                }
            }
        } else recipients = String.join(", ", nicknames);

        final Component spyMessage = Format.parse(getMessage()
                .replace("%sender%", sender)
                .replace("%recipients%", recipients)
                .replace("%message%", message)
                .replace("%server%", serverName)
                .replace("%channel_type%", channelType));

        spies.stream()
                .filter(player -> player.getCurrentServer().isPresent())
                .filter(player -> !player.getUsername().equalsIgnoreCase(sender))
                .filter(player -> !isNetwork || !player.getCurrentServer().get().getServerInfo().getName().equalsIgnoreCase(serverName))
                .filter(player -> !nicknames.contains(player.getUsername()))
                .forEach(player -> player.sendMessage(spyMessage));

        String consoleChannelType = "";
        if (isNetwork && cachedServer.isChanneled()) {
            if (channelType.equals(MiniChat.getPlugin().getConf().get().getSpy().getGlobalChatSymbol())) {
                consoleChannelType = "G";
            } else {
                consoleChannelType = "L";
            }
        }

        String consoleSpyMessage = getConsoleMessage()
                .replace("%sender%", sender)
                .replace("%recipients%", recipients)
                .replace("%message%", message)
                .replace("%server%", serverName)
                .replace("%channel_type%", consoleChannelType);

        if (isNetwork) {
            MiniChat.getPlugin().getServer().getEventManager().fire(new NetworkMessageEvent(sender, message, serverName, consoleChannelType));
            if (MiniChat.getPlugin().getConf().get().getLogging().isNetworkLoggingEnabled())
                MiniChat.getPlugin().getLogger().info(consoleSpyMessage);
        } else {
            if (MiniChat.getPlugin().getConf().get().getLogging().isDirectMessageLoggingEnabled())
                MiniChat.getPlugin().getLogger().info(consoleSpyMessage);
        }
    }

    private @NotNull List<Player> getPlayers() {
        if (isNetwork) return PlayerCache.getNetworkSpies();
        else return PlayerCache.getSocialSpies();
    }

    private @NotNull String getMessage() {
        if (isNetwork) return MiniChat.getPlugin().getLang().get().getSpy().getNetworkSpyFormat();
        else return MiniChat.getPlugin().getLang().get().getSpy().getSocialSpyFormat();
    }

    private @NotNull String getConsoleMessage() {
        if (isNetwork) return MiniChat.getPlugin().getLang().get().getSpy().getNetworkSpyConsoleFormat();
        else return MiniChat.getPlugin().getLang().get().getSpy().getSocialSpyConsoleFormat();
    }
}
