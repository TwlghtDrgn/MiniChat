package net.twlghtdrgn.minichat.event;

import com.velocitypowered.api.event.ResultedEvent;
import com.velocitypowered.api.proxy.Player;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class PrivateMessageEvent implements ResultedEvent<ResultedEvent.GenericResult> {
    @Getter
    private final Player sender;
    @Getter
    private final Set<Player> recipients;
    @Getter
    private final String message;
    @Getter
    @Setter
    @NotNull
    private GenericResult result = GenericResult.allowed();

    public PrivateMessageEvent(Player sender, Set<Player> recipients, String message) {
        this.sender = sender;
        this.recipients = recipients;
        this.message = message;
    }
}
