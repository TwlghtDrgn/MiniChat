package net.twlghtdrgn.minichat;

import com.google.inject.Inject;
import com.velocitypowered.api.command.Command;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Dependency;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import lombok.Getter;
import net.twlghtdrgn.minichat.command.*;
import net.twlghtdrgn.minichat.config.Configuration;
import net.twlghtdrgn.minichat.config.Language;
import net.twlghtdrgn.minichat.listener.ChatListener;
import net.twlghtdrgn.minichat.messaging.ProxyMessaging;
import net.twlghtdrgn.minichat.sql.Database;
import net.twlghtdrgn.twilightlib.api.ILibrary;
import net.twlghtdrgn.twilightlib.api.config.ConfigLoader;
import net.twlghtdrgn.twilightlib.api.util.PluginInfoProvider;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.nio.file.Path;

@Plugin(
        id = "minichat",
        name = "MiniChat",
        version = "0.2",
        authors = {"TwlghtDrgn"},
        dependencies = {
                @Dependency(id = "twilightlib"),
                @Dependency(id = "vanishbridge", optional = true),
        },
        url = "https://github.com/TwlghtDrgn/MiniChat"
)
@Getter
public class MiniChat implements ILibrary {
    @Getter
    private static MiniChat plugin;
    private final Logger logger;
    private final ProxyServer server;
    private final ConfigLoader configLoader;
    private final Path path;
    private final PluginInfoProvider pluginInfo;
    private boolean vanishBridgeInstalled = false;

    private static void setPlugin(MiniChat p) {
        plugin = p;
    }

    @Inject
    public MiniChat(Logger logger, @NotNull ProxyServer server, @DataDirectory Path dataDirectory) {
        this.logger = logger;
        this.server = server;
        this.path = dataDirectory;
        setPlugin(this);
        configLoader = new ConfigLoader(this);
        pluginInfo = new PluginInfoProvider(net.twlghtdrgn.minichat.PluginInfo.NAME,
                net.twlghtdrgn.minichat.PluginInfo.VERSION,
                server.getVersion().getName() + " " + server.getVersion().getVersion(),
                net.twlghtdrgn.minichat.PluginInfo.URL);
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        try {
            logger.info(pluginInfo.getStartupMessage());

            if (server.getPluginManager().isLoaded("vanishbridge")) vanishBridgeInstalled = true;

            getConfigLoader().addConfig(new Configuration("config.yml"));
            getConfigLoader().addConfig(new Language("lang.yml"));

            if (!getConfigLoader().reload()) throw new IllegalStateException("Unable to load one or more configuration files");
            Database.load();

            server.getEventManager().register(this, new ChatListener());
            new ProxyMessaging(this);
            loadCommands();
        } catch (Exception e) {
            logger.error("Unable to start a plugin. Additional info can be found below");
            e.printStackTrace();
        }
    }

    private void loadCommands() {
        loadCommand("minichat-velocity", new MiniChatCommand());
        loadCommand("message", Configuration.getConfig().getAliases().getMessageAliases(), new MessageCommand());
        loadCommand("reply", ArrayUtils.addAll(new String[]{"chat"}, Configuration.getConfig().getAliases().getReplyAliases()), new ReplyCommand());
        loadCommand("staffchat", Configuration.getConfig().getAliases().getStaffChatAliases(), new StaffChatCommand());
        loadCommand("alert", new String[]{"broadcast", "bc"}, new AlertCommand());
        loadCommand("block", new String[]{"unblock"}, new BlockCommand());
    }

    private void loadCommand(String commandName, Command command) {
        final CommandManager manager = server.getCommandManager();
        CommandMeta meta = manager.metaBuilder(commandName)
                .plugin(this)
                .build();
        manager.register(meta, command);
    }

    private void loadCommand(String commandName, String[] aliases, Command command) {
        final CommandManager manager = server.getCommandManager();
        CommandMeta meta = manager.metaBuilder(commandName)
                .aliases(aliases)
                .plugin(this)
                .build();
        manager.register(meta, command);
    }

    @Override
    public Logger log() {
        return logger;
    }

    @Override
    public boolean reload() {
        return false;
    }
}
