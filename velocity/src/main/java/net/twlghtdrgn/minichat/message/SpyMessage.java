package net.twlghtdrgn.minichat.message;

import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;
import net.twlghtdrgn.minichat.MiniChat;
import net.twlghtdrgn.minichat.PlayerCache;
import net.twlghtdrgn.minichat.ServerCache;
import net.twlghtdrgn.minichat.config.Configuration;
import net.twlghtdrgn.minichat.config.Language;
import net.twlghtdrgn.minichat.event.NetworkMessageEvent;
import net.twlghtdrgn.twilightlib.api.util.Format;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class SpyMessage {
    private final boolean isNetwork;
    private String message;
    private final String sender;
    private final String value;
    private final ServerCache.CachedServer cachedServer;

    public SpyMessage(boolean isNetwork, String message, String sender, String value) {
        this.isNetwork = isNetwork;
        this.message = message;
        this.sender = sender;
        this.value = value;
        if (isNetwork && ServerCache.isCached(value))
            this.cachedServer = ServerCache.getCachedServer(value).get();
        else
            this.cachedServer = new ServerCache.CachedServer(false,"!",false);
    }

    public void send() {
        final List<Player> spies = getPlayers();

        String channelType = "";
        if (isNetwork) {
            if (cachedServer.isIgnore()) return;
            if (message.startsWith(cachedServer.getGlobalChatPrefix())) {
                message = message.replaceFirst(cachedServer.getGlobalChatPrefix(), "");
                channelType = Configuration.getConfig().getSpy().getGlobalChatSymbol();
            } else channelType = Configuration.getConfig().getSpy().getLocalChatSymbol();
        }

        final Component spyMessage = Format.parse(getMessage()
                .replace("%sender%", sender)
                .replace("%recipients%", value)
                .replace("%message%", message)
                .replace("%server%", value)
                .replace("%channel_type%", channelType));

        spies.stream()
                .filter(player -> player.getCurrentServer().isPresent())
                .filter(player -> (!isNetwork && Arrays.stream(value.split(",")).noneMatch(s -> s.equalsIgnoreCase(player.getUsername())))
                        || !player.getCurrentServer().get().getServerInfo().getName().equalsIgnoreCase(value))
                .filter(player -> !player.getUsername().equalsIgnoreCase(sender))
                .forEach(player -> player.sendMessage(spyMessage));

        String consoleSpyMessage = getConsoleMessage()
                .replace("%sender%", sender)
                .replace("%recipients%", value)
                .replace("%message%", message)
                .replace("%server%", value)
                .replace("%channel_type%", channelType.equals(Configuration.getConfig().getSpy().getLocalChatSymbol())
                        ? Language.getConfig().getSpy().getLocalChatSymbol() : Language.getConfig().getSpy().getGlobalChatSymbol());

        if (isNetwork) {
            MiniChat.getPlugin().getServer().getEventManager().fire(new NetworkMessageEvent(sender, message, value, channelType));
            if (Configuration.getConfig().getLogging().isNetworkLoggingEnabled())
                MiniChat.getPlugin().getLogger().info(consoleSpyMessage);
        } else {
            if (Configuration.getConfig().getLogging().isDirectMessageLoggingEnabled())
                MiniChat.getPlugin().getLogger().info(consoleSpyMessage);
        }
    }

    private @NotNull List<Player> getPlayers() {
        if (isNetwork) return PlayerCache.getNetworkSpies();
        else return PlayerCache.getSocialSpies();
    }

    private @NotNull String getMessage() {
        if (isNetwork) return Language.getConfig().getSpy().getNetworkSpyFormat();
        else return Language.getConfig().getSpy().getSocialSpyFormat();
    }

    private @NotNull String getConsoleMessage() {
        if (isNetwork) return Language.getConfig().getSpy().getNetworkSpyConsoleFormat();
        else return Language.getConfig().getSpy().getSocialSpyConsoleFormat();
    }
}
