package net.twlghtdrgn.minichat.listener;

import de.myzelyam.api.vanish.VanishAPI;
import io.papermc.paper.event.player.AsyncChatEvent;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;
import net.twlghtdrgn.minichat.ChatPermission;
import net.twlghtdrgn.minichat.MiniChat;
import net.twlghtdrgn.minichat.event.GlobalChatEvent;
import net.twlghtdrgn.minichat.event.LocalChatEvent;
import net.twlghtdrgn.twilightlib.api.util.Format;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

public class ChatListener implements Listener {
    private final MiniChat plugin;
    public ChatListener(MiniChat plugin) {
        this.plugin = plugin;
    }

    private static final String[] VANISH_PLUGINS = {"SuperVanish", "PremiumVanish"};

    private final MiniMessage chatFormatter = MiniMessage.builder()
            .tags(TagResolver.builder()
                    .resolver(StandardTags.color())
                    .resolver(StandardTags.decorations())
                    .resolver(StandardTags.gradient())
                    .resolver(StandardTags.rainbow())
                    .build()
            ).build();

    @EventHandler
    public void onAsyncChatEvent(@NotNull AsyncChatEvent e) {
        final Player player = e.getPlayer();
        if (isVanished(player)) {
            player.sendMessage(Format.parse(plugin.getConf().get().getMessages().getVanishChatMessagePrevent()));
            e.setCancelled(true);
            return;
        }

        final boolean isGlobalChat = isGlobalChat(e.originalMessage());
        final Component displayName = formatDisplayName(player);
        final Component message = formatPlayerMessage(player, Format.parse(e.message()));
        e.message(message);

        if (Format.parse(e.message().compact()).isEmpty()) {
            e.setCancelled(true);
            return;
        }

        if (plugin.getConf().get().getGlobalChat().isEnabled() && !isGlobalChat) {
            LocalChatEvent.send(e, displayName);
        } else {
            GlobalChatEvent.send(e, displayName);
        }
    }

    private boolean isVanished(Player player) {
        boolean vanishInstalled = false;
        for (String s:VANISH_PLUGINS) {
            if (Bukkit.getPluginManager().isPluginEnabled(s)) {
                vanishInstalled = true;
                break;
            }
        }
        if (!vanishInstalled) return false;
        return VanishAPI.isInvisible(player);
    }

    private @NotNull Component formatDisplayName(@NotNull Player player) {
        final String placeholder = plugin.getConf().get().getDisplayNameFormat().replace("{PLAYERNAME}", player.getName());
        if (!Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) return Format.parse(placeholder + plugin.getConf().get().getDivider() + "<reset>");
        final String parsedPlaceholder = PlaceholderAPI.setPlaceholders(player, placeholder);
        return Format.parse(parsedPlaceholder.trim() + plugin.getConf().get().getDivider() + "<reset>");
    }

    private Component formatPlayerMessage(@NotNull Player player, String message) {
        final String prefix = plugin.getConf().get().getGlobalChat().getPrefix();
        if (plugin.getConf().get().getGlobalChat().isEnabled())
            message = message.replaceFirst(prefix, "");
        message = Format.fromLegacy(message);
        if (player.hasPermission(ChatPermission.ALL_FORMATTING)) return Format.parse(message);
        if (player.hasPermission(ChatPermission.COLOR)) return chatFormatter.deserialize(message);
        return Component.text(message);
    }

    private boolean isGlobalChat(Component message) {
        return Format.parse(message).startsWith(plugin.getConf().get().getGlobalChat().getPrefix());
    }
}
