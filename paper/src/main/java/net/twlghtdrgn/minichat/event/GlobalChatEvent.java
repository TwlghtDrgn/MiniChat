package net.twlghtdrgn.minichat.event;

import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class GlobalChatEvent extends Event implements Cancellable {
    @Getter
    private static final HandlerList HANDLER_LIST = new HandlerList();
    @Getter
    private final Player player;
    @Getter
    @Setter
    private Component format;
    @Getter
    @Setter
    private Component message;
    @Getter
    @Setter
    private boolean cancelled;
    public GlobalChatEvent(Player player, Component format, Component message) {
        super(true);
        this.player = player;
        this.format = format;
        this.message = message;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLER_LIST;
    }
}
