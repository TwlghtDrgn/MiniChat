package net.twlghtdrgn.minichat;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;

public class PlayerCache {
    private PlayerCache() {}

    private static final HashSet<UUID> localSpies = new HashSet<>();
    public static boolean setLocalSpy(UUID uuid) {
        if (!isLocalSpy(uuid)) {
            localSpies.add(uuid);
            return true;
        } else {
            localSpies.remove(uuid);
            return false;
        }
    }

    public static boolean isLocalSpy(UUID uuid) {
        return localSpies.contains(uuid);
    }

    public static List<Player> getLocalSpies() {
        return localSpies.stream().filter(uuid -> Bukkit.getOfflinePlayer(uuid).isOnline())
                .map(Bukkit::getPlayer).toList();
    }
}
