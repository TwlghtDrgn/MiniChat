package net.twlghtdrgn.minichat;

import com.google.inject.Inject;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.plugin.Dependency;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import lombok.Getter;
import lombok.Setter;
import net.twlghtdrgn.minichat.command.MessageCommand;
import net.twlghtdrgn.minichat.command.MiniChatCommand;
import net.twlghtdrgn.minichat.config.Lang;
import net.twlghtdrgn.minichat.config.MainConfig;
import net.twlghtdrgn.minichat.discord.DiscordWebhook;
import net.twlghtdrgn.minichat.listener.ChatListener;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;

import java.nio.file.Path;

@Plugin(
        id = "minichat",
        name = "MiniChat",
        version = "0.0.1",
        dependencies = {
                @Dependency(id = "vanishbridge", optional = true),
                @Dependency(id = "skinsrestorer", optional = true)
        }
)
public class MiniChatVelocity {
    @Getter
    private static MiniChatVelocity plugin;

    @Getter
    private final Logger logger;
    @Getter
    private final ProxyServer server;
    @Getter
    private final Path dataDirectory;

    @Getter
    private boolean vanishBridgeInstalled = false;
    @Getter
    @Setter
    private boolean skinsRestorerInstalled = false;

    @Inject
    public MiniChatVelocity(Logger logger, ProxyServer server, @DataDirectory Path dataDirectory) {
        this.logger = logger;
        this.server = server;
        this.dataDirectory = dataDirectory;
        plugin = this;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        try {
            logger.info("""
                    
                    || MiniChat
                    ||
                    || Version: 0.0.1
                    || Proxy version: %proxy_ver%
                    || GitHub: https://github.com/TwlghtDrgn/MiniChat"""
                    .replace("%proxy_ver%",server.getVersion().getName() + " " + server.getVersion().getVersion()));

            if (server.getPluginManager().isLoaded("vanishbridge")) vanishBridgeInstalled = true;
            if (server.getPluginManager().isLoaded("skinsrestorer")) skinsRestorerInstalled = true;
            MainConfig.load();
            if (MainConfig.getConfig().isDiscordWebhook()) DiscordWebhook.load();
            Lang.load();

            server.getEventManager().register(this, new ChatListener());

            loadCommands();
        } catch (Exception e) {
            logger.error("Unable to start a plugin. Additional info can be found below");
            e.printStackTrace();
        }
    }

    private void loadCommands() {
        CommandManager manager = server.getCommandManager();

        CommandMeta minichatCommandMeta = manager.metaBuilder("minichat-velocity")
                .plugin(this)
                .build();

        manager.register(minichatCommandMeta, new MiniChatCommand());

        String[] internalPrefixes = {"chat"};
        String[] configPrefixes = ArrayUtils.addAll(MainConfig.getConfig().getMessageAliases(),MainConfig.getConfig().getReplyAliases());

        CommandMeta messageCommandMeta = manager.metaBuilder("message")
                .aliases(ArrayUtils.addAll(internalPrefixes, configPrefixes))
                .plugin(this)
                .build();

        manager.register(messageCommandMeta, new MessageCommand());
    }
}
