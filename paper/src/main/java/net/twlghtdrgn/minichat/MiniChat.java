package net.twlghtdrgn.minichat;

import lombok.Getter;
import net.twlghtdrgn.minichat.command.MiniChatCommand;
import net.twlghtdrgn.minichat.config.Config;
import net.twlghtdrgn.minichat.listener.ChatListener;
import net.twlghtdrgn.minichat.listener.PlayerJoinLeaveListener;
import net.twlghtdrgn.minichat.messaging.CrossServerMessaging;
import net.twlghtdrgn.minichat.messaging.MessageChannel;
import net.twlghtdrgn.minichat.messaging.ProxyMessaging;
import net.twlghtdrgn.minichat.messaging.RedisMessaging;
import net.twlghtdrgn.twilightlib.TwilightPlugin;
import net.twlghtdrgn.twilightlib.api.config.Configuration;
import org.bukkit.Bukkit;
import org.spongepowered.configurate.ConfigurateException;

@Getter
public final class MiniChat extends TwilightPlugin {
    @Getter
    private static MiniChat plugin;
    private Configuration<Config> conf;
    private RedisMessaging redisMessaging;
    @Override
    protected void enable() throws Exception {
        plugin = this;

        conf = new Configuration<>(this, Config.class);
        if (!reload()) throw new IllegalStateException("Config cannot be loaded");

        redisMessaging = new RedisMessaging(this);

        if (!conf.get().getCrossServer().isUseRedisEnabled()) {
            getServer().getMessenger().registerIncomingPluginChannel(this, MessageChannel.SERVER, new CrossServerMessaging());
            getServer().getMessenger().registerOutgoingPluginChannel(this, MessageChannel.SERVER);
        }

        if (conf.get().getProxySync().isEnabled()) {
            getServer().getMessenger().registerIncomingPluginChannel(this, MessageChannel.PROXY, new ProxyMessaging());
            getServer().getMessenger().registerOutgoingPluginChannel(this, MessageChannel.PROXY);
        }

        Bukkit.getPluginManager().registerEvents(new PlayerJoinLeaveListener(this), this);
        Bukkit.getPluginManager().registerEvents(new ChatListener(this), this);
        Bukkit.getCommandMap().register("minichat", new MiniChatCommand());

        redisMessaging.sendSync();
    }

    @Override
    protected void disable() {
        if (redisMessaging != null) redisMessaging.unsubscribe();

        if (!conf.get().getCrossServer().isUseRedisEnabled()) {
            getServer().getMessenger().unregisterOutgoingPluginChannel(this, MessageChannel.SERVER);
            getServer().getMessenger().unregisterIncomingPluginChannel(this, MessageChannel.SERVER);
        }

        if (conf.get().getProxySync().isEnabled()) {
            getServer().getMessenger().unregisterOutgoingPluginChannel(this, MessageChannel.PROXY);
            getServer().getMessenger().unregisterIncomingPluginChannel(this, MessageChannel.PROXY);
        }
    }

    @Override
    public boolean reload() {
        try {
            conf.reload();
            if (redisMessaging != null) redisMessaging.sendSync();
            return true;
        } catch (ConfigurateException e) {
            log().error("Unable to reload configuration files", e);
            return false;
        }
    }
}
