package net.twlghtdrgn.minichat.listener;

import de.myzelyam.api.vanish.VanishAPI;
import io.papermc.paper.event.player.AsyncChatEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;
import net.twlghtdrgn.minichat.PlayerCache;
import net.twlghtdrgn.minichat.config.Configuration;
import net.twlghtdrgn.minichat.event.GlobalChatEvent;
import net.twlghtdrgn.minichat.event.LocalChatEvent;
import net.twlghtdrgn.minichat.messaging.CrossServerMessaging;
import net.twlghtdrgn.twilightlib.api.util.Format;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ChatListener implements Listener {
    private final MiniMessage chatFormatter = MiniMessage.builder()
            .tags(TagResolver.builder()
                    .resolver(StandardTags.clickEvent())
                    .resolver(StandardTags.color())
                    .build()
            ).build();

    @EventHandler
    public void onAsyncChatEvent(AsyncChatEvent e) {
        if ((Bukkit.getPluginManager().isPluginEnabled("SuperVanish")
            || Bukkit.getPluginManager().isPluginEnabled("PremiumVanish"))
                && VanishAPI.isInvisible(e.getPlayer())) {
            e.getPlayer().sendMessage(Format.parse(Configuration.getConfig().getMessages().getVanishChatMessagePrevent()));
            e.setCancelled(true);
            return;
        }

        Component placeholder = Format.parse(
                PlaceholderAPI.setPlaceholders(e.getPlayer(),
                        Configuration.getConfig().getPlaceholder()
                                .replace("{playername}", e.getPlayer().getName())));
        Message msg = hasColorPermission(e.getPlayer(), Format.parse(e.message()).trim());
        e.message(msg.getComponent());

        if (msg.getRaw().length() < 1) {
            e.setCancelled(true);
            return;
        }

        if (!Configuration.getConfig().getGlobalChat().isEnabled() ||
                msg.getRaw().startsWith(Configuration.getConfig().getGlobalChat().getPrefix())) {
            sendToGlobal(e, placeholder, msg.getRaw());
        } else {
            sendToLocal(e, placeholder);
        }
    }

    private void sendToLocal(@NotNull AsyncChatEvent e, Component format) {
        LocalChatEvent event = new LocalChatEvent(e.getPlayer(), format, e.message());
        if (event.callEvent()) {
            double distance = Configuration.getConfig().getLocalChat().getRange();
            List<Player> players = e.getPlayer().getWorld().getPlayers();
            Location c = e.getPlayer().getLocation();
            Component render = Format.parse(Configuration.getConfig().getLocalChat().getIcon()).append(event.getFormat());

            e.viewers().clear();
            e.viewers().add(Bukkit.getConsoleSender());
            for (Player p:players)
                if (p.getLocation().distanceSquared(c) <= distance * distance)
                    e.viewers().add(p);
            PlayerCache.getLocalSpies().stream()
                    .filter(player -> !e.viewers().contains(player))
                    .forEach(player -> e.viewers().add(player));

            e.renderer(((source, sourceDisplayName, component, viewer) ->
                    render.appendSpace().append(event.getMessage())));
        } else e.setCancelled(true);
    }

    private void sendToGlobal(@NotNull AsyncChatEvent e, Component format, String message) {
        GlobalChatEvent event = new GlobalChatEvent(e.getPlayer(), format, e.message());
        if (event.callEvent()) {
            Component render;
            if (!Configuration.getConfig().getGlobalChat().isEnabled()) render = event.getFormat();
            else render = Format.parse(Configuration.getConfig().getGlobalChat().getIcon()).append(event.getFormat());

            if (Configuration.getConfig().getCrossServer().isCrossServerEnabled())
                CrossServerMessaging.sendMessage(e.getPlayer(), Format.parse(render) + " " + message.replaceFirst("!", ""));

            e.renderer(((source, sourceDisplayName, component, viewer) ->
                    render.appendSpace().append(event.getMessage())));
        } else e.setCancelled(true);
    }

    private @NotNull Message hasColorPermission(@NotNull final Player player, @NotNull final String originalMessage) {
        String modifiedMessage;
        if  (originalMessage.contains("§") || originalMessage.contains("&")) modifiedMessage = Format.fromLegacy(originalMessage);
        else modifiedMessage = originalMessage;

        if (player.hasPermission("minichat.colors"))
            return new Message(chatFormatter.deserialize(modifiedMessage.replaceFirst("!","")), modifiedMessage);
        else return new Message(Component.text(modifiedMessage.replaceFirst("!","")), modifiedMessage.replaceAll("<.[A-Za-z0-9:]*>",""));
    }

    @Getter
    @AllArgsConstructor
    protected class Message {
        private Component component;
        private String raw;
    }
}
