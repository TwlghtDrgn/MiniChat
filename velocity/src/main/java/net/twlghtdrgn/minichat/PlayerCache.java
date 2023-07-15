package net.twlghtdrgn.minichat;

import com.velocitypowered.api.proxy.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class PlayerCache {
    private PlayerCache() {}

    private static final HashMap<UUID, HashSet<UUID>> lastRecipients = new HashMap<>();

    public static Optional<HashSet<UUID>> getLastRecipients(UUID uuid) {
        if (lastRecipients.containsKey(uuid))
            return Optional.of(lastRecipients.get(uuid));
        return Optional.empty();
    }

    public static void setLastRecipients(UUID sender, @NotNull List<Player> lastPlayers) {
        HashSet<UUID> recipientsUUID = new HashSet<>();
        for (Player p:lastPlayers) {
            recipientsUUID.add(p.getUniqueId());
        }
        lastRecipients.put(sender, recipientsUUID);
    }
}
