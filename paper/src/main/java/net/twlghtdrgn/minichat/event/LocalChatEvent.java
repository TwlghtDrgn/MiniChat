package net.twlghtdrgn.minichat.event;

import io.papermc.paper.event.player.AsyncChatEvent;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import net.twlghtdrgn.minichat.MiniChat;
import net.twlghtdrgn.minichat.PlayerCache;
import net.twlghtdrgn.twilightlib.api.util.Format;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
public class LocalChatEvent extends Event implements Cancellable {
    @Getter
    private static final HandlerList HANDLER_LIST = new HandlerList();
    private final Player player;
    private final Component symbol;
    private final Component format;
    private Component message;
    private boolean cancelled;

    public LocalChatEvent(Player player, Component format, Component message) {
        super(true);
        this.player = player;

        this.symbol = Format.parse(MiniChat.getPlugin().getConf().get().getLocalChat().getIcon());

        this.format = format;
        this.message = message;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static void send(@NotNull AsyncChatEvent chatEvent, Component format) {
        final Player player = chatEvent.getPlayer();
        final LocalChatEvent event = new LocalChatEvent(player, format, chatEvent.message());
        if (!event.callEvent()) {
            chatEvent.setCancelled(true);
            return;
        }
        chatEvent.viewers().clear();
        chatEvent.viewers().add(Bukkit.getConsoleSender());
        chatEvent.viewers().addAll(calculatePlayersInRange(player));

        chatEvent.renderer((source, sourceDisplayName, msg, viewer) ->
                event.getSymbol()
                        .append(event.getFormat())
                        .appendSpace()
                        .append(event.getMessage()));
    }

    private static @NotNull Set<Player> calculatePlayersInRange(@NotNull Player player) {
        final List<Player> worldPlayers = player.getWorld().getPlayers();
        double range = MiniChat.getPlugin().getConf().get().getLocalChat().getRange();
        final Location loc = player.getLocation();

        final Set<Player> rangedPlayers = worldPlayers.stream()
                .filter(p -> p.getLocation().distanceSquared(loc) <= range * range)
                .filter(p -> !PlayerCache.isLocalSpy(p.getUniqueId()))
                .collect(Collectors.toSet());

        rangedPlayers.addAll(PlayerCache.getLocalSpies().stream()
                .filter(p -> !rangedPlayers.contains(p))
                .toList());

        return rangedPlayers;
    }
}
