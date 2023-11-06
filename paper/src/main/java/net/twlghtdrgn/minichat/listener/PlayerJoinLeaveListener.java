package net.twlghtdrgn.minichat.listener;

import net.twlghtdrgn.minichat.MiniChat;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerJoinLeaveListener implements Listener {
    private final MiniChat plugin;
    public PlayerJoinLeaveListener(MiniChat plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        if (!plugin.getConf().get().getEnable().isJoinMessageEnabled()) e.joinMessage(null);
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent e) {
        if (!plugin.getConf().get().getEnable().isLeaveMessageEnabled()) e.quitMessage(null);
    }
}
