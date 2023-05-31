package net.twlghtdrgn.minichat;

import lombok.Getter;
import net.twlghtdrgn.minichat.command.MiniChatCommand;
import net.twlghtdrgn.minichat.config.Config;
import net.twlghtdrgn.minichat.listener.ChatListener;
import net.twlghtdrgn.twilightlib.TwilightPlugin;
import org.bukkit.Bukkit;

public final class MiniChat extends TwilightPlugin {
    @Getter
    private static MiniChat plugin;

    @Override
    protected void enable() {
        try {
            plugin = this;
            Config.load();
            getServer().getMessenger().registerOutgoingPluginChannel(this, ProxyMessaging.PROXY_CHANNEL);
            getServer().getMessenger().registerIncomingPluginChannel(this, ProxyMessaging.PROXY_CHANNEL, new ProxyMessaging());
            Bukkit.getPluginManager().registerEvents(new ChatListener(), this);
            Bukkit.getCommandMap().register("minichat", new MiniChatCommand());
        } catch (Exception e) {
            getLogger().severe(String.format("Unable to start %s.%nReason: %s", getPluginMeta().getName(), e.getMessage()));
            e.printStackTrace();
            Bukkit.getPluginManager().disablePlugin(this);
        }
    }

    @Override
    protected void disable() {
        getServer().getMessenger().unregisterOutgoingPluginChannel(this, ProxyMessaging.PROXY_CHANNEL);
        getServer().getMessenger().unregisterIncomingPluginChannel(this, ProxyMessaging.PROXY_CHANNEL);
    }
}
