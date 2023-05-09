package net.twlghtdrgn.minichat.listener;

import de.myzelyam.api.vanish.VanishAPI;
import io.papermc.paper.event.player.AsyncChatEvent;
import lombok.Getter;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.twlghtdrgn.minichat.ProxyMessaging;
import net.twlghtdrgn.minichat.config.Config;
import net.twlghtdrgn.twilightlib.util.Format;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.List;

public class ChatListener implements Listener {
    @Getter
    private static final List<Player> spies = new ArrayList<>();

    @EventHandler
    public void onAsyncChatEvent(AsyncChatEvent e) {
        if ((Bukkit.getPluginManager().isPluginEnabled("SuperVanish") || Bukkit.getPluginManager().isPluginEnabled("PremiumVanish"))
                && VanishAPI.isInvisible(e.getPlayer())) {
            e.getPlayer().sendMessage(Format.parse(Config.getVanishChatDisabledMessage()));
            e.setCancelled(true);
            return;
        }

        String message = convertToString(e.message()).trim();
        StringBuilder sb = new StringBuilder()
                .append(PlaceholderAPI.setPlaceholders(e.getPlayer(), Config.getPlaceholder()
                                .replace("{playername}",e.getPlayer().getName()))
                        .replaceAll("([§&])[0-9A-Fa-f]",""))
                .append(" ")
                .append(message.replaceFirst("!","").replaceAll("([§&])[0-9A-Fa-f]",""));

        if (!Config.isGlobalEnabled() || (Config.isGlobalEnabled()
                && message.startsWith(Config.getGlobalChatPrefix()))) {
            sendToGlobal(e, sb);
        } else {
            sendToLocal(e, sb);
        }
    }

    private static void sendToLocal(AsyncChatEvent e, StringBuilder sb) {
        double distance = Config.getLocalChatRange();
        List<Player> players = e.getPlayer().getWorld().getPlayers();
        Location c = e.getPlayer().getLocation();
        String msg = Config.getLocalChatIcon() + " " + sb;

        e.viewers().clear();
        e.viewers().add(Bukkit.getConsoleSender());
        for (Player p:players) {
            if (p.getLocation().distanceSquared(c) <= distance * distance) e.viewers().add(p);
        }
        for (Player p:spies) {
            if (p.isOnline()) e.viewers().add(p);
        }

        e.renderer(((source, sourceDisplayName, component, viewer) ->
            Format.parse(msg)));
    }

    private static void sendToGlobal(AsyncChatEvent e, StringBuilder sb) {
        String msg;
        if (!Config.isGlobalEnabled()) msg = sb.toString();
        else msg = Config.getGlobalChatIcon() + " " + sb;
        if (Config.isCrossServerEnabled()) ProxyMessaging.sendMessage(e.getPlayer(), msg);
        e.renderer(((source, sourceDisplayName, component, viewer) ->
                Format.parse(msg)));
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        if (Config.getJoinMessageDisabled()) e.joinMessage(null);
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent e) {
        if (Config.getLeaveMessageDisabled()) e.quitMessage(null);
    }

    private String convertToString(Component c) {
        return MiniMessage.miniMessage().serialize(c);
    }
}
