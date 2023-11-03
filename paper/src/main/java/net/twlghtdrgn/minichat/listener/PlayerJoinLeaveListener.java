package net.twlghtdrgn.minichat.listener;

import net.twlghtdrgn.minichat.config.Configuration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerJoinLeaveListener implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        if (Configuration.getConfig().getEnable().isJoinMessageEnabled()) e.joinMessage(null);
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent e) {
        if (Configuration.getConfig().getEnable().isLeaveMessageEnabled()) e.quitMessage(null);
    }
}
