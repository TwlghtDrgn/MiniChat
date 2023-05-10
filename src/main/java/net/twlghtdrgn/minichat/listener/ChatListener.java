package net.twlghtdrgn.minichat.listener;

import de.myzelyam.api.vanish.VanishAPI;
import io.papermc.paper.event.player.AsyncChatEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
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

        String format = PlaceholderAPI.setPlaceholders(e.getPlayer(), Config.getPlaceholder()
                .replace("{playername}",e.getPlayer().getName()));
        String rawMessage = Format.parse(e.message()).trim();

        Message msg = checkPermission(e.getPlayer(),rawMessage);

        e.message(msg.getComponent());

        if (Format.parse(e.message()).length() < 1) {
            e.setCancelled(true);
            return;
        }

        if (!Config.isGlobalEnabled() || (Config.isGlobalEnabled()
                && rawMessage.startsWith(Config.getGlobalChatPrefix()))) {
            sendToGlobal(e, format, msg.getRaw());
        } else {
            sendToLocal(e, format);
        }
    }

    private static void sendToLocal(AsyncChatEvent e, String format) {
        double distance = Config.getLocalChatRange();
        List<Player> players = e.getPlayer().getWorld().getPlayers();
        Location c = e.getPlayer().getLocation();
        String render = Config.getLocalChatIcon() + " " + format;

        e.viewers().clear();
        e.viewers().add(Bukkit.getConsoleSender());
        for (Player p:players)
            if (p.getLocation().distanceSquared(c) <= distance * distance)
                e.viewers().add(p);
        for (Player p:spies)
            if (p.isOnline())
                e.viewers().add(p);

        e.renderer(((source, sourceDisplayName, component, viewer) ->
            Format.parse(render).appendSpace().append(component)));
    }

    private static void sendToGlobal(AsyncChatEvent e, String format, String message) {
        String render;
        if (!Config.isGlobalEnabled()) render = format;
        else render = Config.getGlobalChatIcon() + " " + format;
        if (Config.isCrossServerEnabled())
            ProxyMessaging.sendMessage(e.getPlayer(), render + " " + message);

        e.renderer(((source, sourceDisplayName, component, viewer) ->
                Format.parse(render).appendSpace().append(component)));
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        if (Config.getJoinMessageDisabled()) e.joinMessage(null);
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent e) {
        if (Config.getLeaveMessageDisabled()) e.quitMessage(null);
    }

    private Message checkPermission(Player p, String s) {
        Message m;

        String raw = s.replaceFirst("!", "")
                .replaceAll("[&§][0-9A-Fa-fK-Ok-oRr]","");
        if (p.hasPermission("minichat.colors")) {
            m = new Message(Format.parse(raw),raw);
        } else {
            m = new Message(Component.text(raw), raw
                    .replaceAll("<[A-Za-z]*>",""));
        }

        return m;
    }

    @Getter
    @AllArgsConstructor
    private static class Message {
        private Component component;
        private String raw;
    }
}
