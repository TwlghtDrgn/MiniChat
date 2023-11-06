package net.twlghtdrgn.minichat.event;

import io.papermc.paper.event.player.AsyncChatEvent;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.twlghtdrgn.minichat.MiniChat;
import net.twlghtdrgn.minichat.config.Config;
import net.twlghtdrgn.minichat.messaging.CrossServerMessaging;
import net.twlghtdrgn.twilightlib.api.util.Format;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@Getter
@Setter
public class GlobalChatEvent extends Event implements Cancellable {
    private static final MiniMessage MINIMESSAGE = MiniMessage.miniMessage();

    @Getter
    private static final HandlerList HANDLER_LIST = new HandlerList();
    private final Player player;
    private final Component symbol;
    private final Component format;
    private Component message;
    private boolean cancelled;

    public GlobalChatEvent(Player player, Component format, Component message) {
        super(true);
        this.player = player;
        Config conf = MiniChat.getPlugin().getConf().get();

        if (conf.getGlobalChat().isEnabled())
            this.symbol = Format.parse(MiniChat.getPlugin().getConf().get().getGlobalChat().getIcon());
        else this.symbol = Component.empty();

        this.format = format;
        this.message = message;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static void send(@NotNull AsyncChatEvent chatEvent, Component format) {
        final Player player = chatEvent.getPlayer();
        final GlobalChatEvent event = new GlobalChatEvent(player, format, chatEvent.message());
        if (!event.callEvent()) {
            chatEvent.setCancelled(true);
            return;
        }

        final Component render = event.getSymbol()
                .append(event.getFormat())
                .appendSpace()
                .append(event.getMessage());

        chatEvent.renderer((source, sourceDisplayName, msg, viewer) -> render);

        if (MiniChat.getPlugin().getConf().get().getCrossServer().isCrossServerEnabled()) {
            if (MiniChat.getPlugin().getConf().get().getCrossServer().isUseRedisEnabled())
                MiniChat.getPlugin().getRedisMessaging().sendCrossServer(player, MINIMESSAGE.serialize(render));
            else CrossServerMessaging.sendMessage(player, MINIMESSAGE.serialize(render));
        }
    }
}
