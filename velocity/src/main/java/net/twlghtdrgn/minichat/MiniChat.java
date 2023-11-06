package net.twlghtdrgn.minichat;

import com.google.inject.Inject;
import com.velocitypowered.api.command.Command;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Dependency;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import lombok.Getter;
import net.twlghtdrgn.minichat.command.*;
import net.twlghtdrgn.minichat.config.Config;
import net.twlghtdrgn.minichat.config.Language;
import net.twlghtdrgn.minichat.listener.ChatListener;
import net.twlghtdrgn.minichat.messaging.ProxyMessaging;
import net.twlghtdrgn.minichat.messaging.RedisMessaging;
import net.twlghtdrgn.minichat.sql.Database;
import net.twlghtdrgn.twilightlib.api.ILibrary;
import net.twlghtdrgn.twilightlib.api.config.ConfigLoader;
import net.twlghtdrgn.twilightlib.api.config.Configuration;
import net.twlghtdrgn.twilightlib.api.util.PluginInfoProvider;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.spongepowered.configurate.ConfigurateException;

import java.nio.file.Path;

@Plugin(
        id = "minichat",
        name = "MiniChat",
        version = PluginInfo.VERSION,
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
    private RedisMessaging redisMessaging;
    private Configuration<Config> conf;
    private Configuration<Language> lang;
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

            conf = new Configuration<>(this, Config.class);
            lang = new Configuration<>(this, Language.class);

            if (!reload()) throw new IllegalStateException("Unable to load one or more configuration files");
            Database.load();

            redisMessaging = new RedisMessaging(this);

            server.getEventManager().register(this, new ChatListener());
            new ProxyMessaging(this);
            loadCommands();
            redisMessaging.sendSyncRequest();
        } catch (Exception e) {
            logger.error("Unable to start a plugin. Additional info can be found below");
            e.printStackTrace();
        }
    }

    @Subscribe
    public void onProxyShutdown(ProxyShutdownEvent event) {
        if (redisMessaging != null) redisMessaging.unsubscribe();
    }

    private void loadCommands() {
        loadCommand("minichat-velocity", new MiniChatCommand());
        loadCommand("message", MiniChat.getPlugin().getConf().get().getAliases().getMessageAliases(), new MessageCommand());
        loadCommand("reply", ArrayUtils.addAll(new String[]{"chat"}, MiniChat.getPlugin().getConf().get().getAliases().getReplyAliases()), new ReplyCommand());
        loadCommand("staffchat", MiniChat.getPlugin().getConf().get().getAliases().getStaffChatAliases(), new StaffChatCommand());
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
        try {
            conf.reload();
            lang.reload();
            if (redisMessaging != null) redisMessaging.sendSyncRequest();
            return true;
        } catch (ConfigurateException e) {
            log().error("Unable to reload configuration files", e);
            return false;
        }
    }
}
