package net.twlghtdrgn.minichat;

import lombok.Getter;
import net.twlghtdrgn.minichat.command.MiniChatCommand;
import net.twlghtdrgn.minichat.config.Configuration;
import net.twlghtdrgn.minichat.listener.ChatListener;
import net.twlghtdrgn.minichat.listener.PlayerJoinLeaveListener;
import net.twlghtdrgn.minichat.messaging.CrossServerMessaging;
import net.twlghtdrgn.minichat.messaging.ProxySyncMessaging;
import net.twlghtdrgn.twilightlib.TwilightPlugin;
import net.twlghtdrgn.twilightlib.exception.PluginLoadException;
import org.bukkit.Bukkit;

public final class MiniChat extends TwilightPlugin {
    @Getter
    private static MiniChat plugin;

    @Override
    protected void enable() throws PluginLoadException {
        plugin = this;
        getConfigLoader().addConfig(new Configuration("config.yml"));

        if (!getConfigLoader().reload()) throw new PluginLoadException("Config cannot be loaded");

        getServer().getMessenger().registerIncomingPluginChannel(this, CrossServerMessaging.PROXY_CHANNEL,
                new CrossServerMessaging());
        getServer().getMessenger().registerIncomingPluginChannel(this, ProxySyncMessaging.PROXY_SYNC_CHANNEL,
                new ProxySyncMessaging());
        getServer().getMessenger().registerOutgoingPluginChannel(this, CrossServerMessaging.PROXY_CHANNEL);
        getServer().getMessenger().registerOutgoingPluginChannel(this, ProxySyncMessaging.PROXY_SYNC_CHANNEL);

        Bukkit.getPluginManager().registerEvents(new PlayerJoinLeaveListener(), this);
        Bukkit.getPluginManager().registerEvents(new ChatListener(), this);
        Bukkit.getCommandMap().register("minichat", new MiniChatCommand());
    }

    @Override
    protected void disable() {
        getServer().getMessenger().unregisterOutgoingPluginChannel(this, CrossServerMessaging.PROXY_CHANNEL);
        getServer().getMessenger().unregisterOutgoingPluginChannel(this, ProxySyncMessaging.PROXY_SYNC_CHANNEL);
        getServer().getMessenger().unregisterIncomingPluginChannel(this, CrossServerMessaging.PROXY_CHANNEL);
        getServer().getMessenger().unregisterIncomingPluginChannel(this, ProxySyncMessaging.PROXY_SYNC_CHANNEL);
    }
}
