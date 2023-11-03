package net.twlghtdrgn.minichat;

import lombok.Getter;
import net.twlghtdrgn.minichat.command.MiniChatCommand;
import net.twlghtdrgn.minichat.config.Configuration;
import net.twlghtdrgn.minichat.listener.ChatListener;
import net.twlghtdrgn.minichat.listener.PlayerJoinLeaveListener;
import net.twlghtdrgn.minichat.messaging.CrossServerMessaging;
import net.twlghtdrgn.minichat.messaging.MessageChannel;
import net.twlghtdrgn.minichat.messaging.ProxySyncMessaging;
import net.twlghtdrgn.twilightlib.TwilightPlugin;
import org.bukkit.Bukkit;

public final class MiniChat extends TwilightPlugin {
    @Getter
    private static MiniChat plugin;

    @Override
    protected void enable() throws Exception {
        plugin = this;
        getConfigLoader().addConfig(new Configuration("config.yml"));

        if (!getConfigLoader().reload()) throw new IllegalStateException("Config cannot be loaded");

        getServer().getMessenger().registerIncomingPluginChannel(this, MessageChannel.SERVER,
                new CrossServerMessaging());
        getServer().getMessenger().registerIncomingPluginChannel(this, MessageChannel.PROXY,
                new ProxySyncMessaging());
        getServer().getMessenger().registerOutgoingPluginChannel(this, MessageChannel.SERVER);
        getServer().getMessenger().registerOutgoingPluginChannel(this, MessageChannel.PROXY);

        Bukkit.getPluginManager().registerEvents(new PlayerJoinLeaveListener(), this);
        Bukkit.getPluginManager().registerEvents(new ChatListener(), this);
        Bukkit.getCommandMap().register("minichat", new MiniChatCommand());
    }

    @Override
    protected void disable() {
        getServer().getMessenger().unregisterOutgoingPluginChannel(this, MessageChannel.SERVER);
        getServer().getMessenger().unregisterOutgoingPluginChannel(this, MessageChannel.PROXY);
        getServer().getMessenger().unregisterIncomingPluginChannel(this, MessageChannel.SERVER);
        getServer().getMessenger().unregisterIncomingPluginChannel(this, MessageChannel.PROXY);
    }

    @Override
    public boolean reload() {
        return false;
    }
}
