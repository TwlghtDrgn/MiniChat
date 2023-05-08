package net.twlghtdrgn.minichat;

import lombok.Getter;
import net.twlghtdrgn.minichat.command.MiniChatCommand;
import net.twlghtdrgn.minichat.config.Config;
import net.twlghtdrgn.minichat.listener.ChatListener;
import net.twlghtdrgn.twilightlib.TwilightLib;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class MiniChat extends JavaPlugin {
    @Getter
    private static MiniChat plugin;

    @Override
    public void onEnable() {
        // Plugin startup logic
        TwilightLib.setPlugin(this);
        plugin = this;
        try {
            getLogger().info(String.format("""
                    
                    || MiniChat v%s
                    ||
                    || Server version: %s
                    || GitHub: https://github.com/TwlghtDrgn/MiniChat""",
                    getPluginMeta().getVersion(),
                    getServer().getVersion()));
            Config.load();
            getServer().getMessenger().registerOutgoingPluginChannel(plugin, ProxyMessaging.PROXY_CHANNEL);
            getServer().getMessenger().registerIncomingPluginChannel(plugin, ProxyMessaging.PROXY_CHANNEL, new ProxyMessaging());
            Bukkit.getPluginManager().registerEvents(new ChatListener(), this);
            Bukkit.getCommandMap().register("minichat", new MiniChatCommand());
        } catch (Exception e) {
            getLogger().severe(String.format("Unable to start %s.%nReason: %s", getPluginMeta().getName(), e.getMessage()));
            e.printStackTrace();
            Bukkit.getPluginManager().disablePlugin(this);
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getServer().getMessenger().unregisterOutgoingPluginChannel(plugin, ProxyMessaging.PROXY_CHANNEL);
        getServer().getMessenger().unregisterIncomingPluginChannel(plugin, ProxyMessaging.PROXY_CHANNEL);
    }
}
