package net.twlghtdrgn.minichat.listener;

import net.twlghtdrgn.minichat.MiniChat;
import net.twlghtdrgn.minichat.config.Configuration;
import net.twlghtdrgn.minichat.messaging.ProxySyncMessaging;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.concurrent.TimeUnit;

public class PlayerJoinLeaveListener implements Listener {
    private static boolean messageSent = false;
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        if (Configuration.getConfig().getDisable().isJoinMessageDisabled()) e.joinMessage(null);
        if (!messageSent) {
            Bukkit.getAsyncScheduler().runDelayed(MiniChat.getPlugin(),scheduledTask ->
                    ProxySyncMessaging.sendMessage(e.getPlayer(), ProxySyncMessaging.Values.SETTINGS), 1L, TimeUnit.SECONDS);
            setMessageSent();
        }
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent e) {
        if (Configuration.getConfig().getDisable().isLeaveMessageDisabled()) e.quitMessage(null);
    }

    private static void setMessageSent() {
        messageSent = true;
    }

    public static void setResendMessage() {
        messageSent = false;
    }
}
