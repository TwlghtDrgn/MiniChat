package net.twlghtdrgn.minichat;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public class PlayerCache {
    private static final ProxyServer server = MiniChat.getPlugin().getServer();
    private PlayerCache() {}

    private static final HashMap<UUID, Set<UUID>> lastRecipients = new HashMap<>();
    /**
     * Returns last recipients
     * @param uuid last messenger's uuid
     */
    public static Optional<Set<UUID>> getLastRecipients(UUID uuid) {
        if (lastRecipients.containsKey(uuid))
            return Optional.of(lastRecipients.get(uuid));
        return Optional.empty();
    }

    /**
     * Saves last recipients
     * @param sender message sender's uuid
     * @param lastPlayers a set of player who got a message
     */
    public static void setLastRecipients(UUID sender, @NotNull Set<Player> lastPlayers) {
        Set<UUID> recipientsUUID = lastPlayers.stream()
                .map(Player::getUniqueId)
                .collect(Collectors.toSet());
        lastRecipients.put(sender, recipientsUUID);
    }

    private static final HashSet<UUID> chatMode = new HashSet<>();
    /**
     * Check if player is in a chat mode
     * @param uuid player's uuid
     */
    public static boolean isChatModeEnabled(UUID uuid) {
        return chatMode.contains(uuid);
    }

    /**
     * Toggles chat mode for a player
     * @param uuid command executor's uuid
     */
    public static boolean toggleChatMode(UUID uuid) {
        if (!isChatModeEnabled(uuid)) {
            chatMode.add(uuid);
            return true;
        } else {
            chatMode.remove(uuid);
            return false;
        }
    }

    private static final HashSet<UUID> socialSpy = new HashSet<>();
    /**
     * Add or remove a new private message spy
     * @param uuid command executor's uuid
     */
    public static boolean setSocialSpy(UUID uuid) {
        if (!isSocialSpy(uuid)) {
            socialSpy.add(uuid);
            return true;
        } else {
            socialSpy.remove(uuid);
            return false;
        }
    }

    /**
     * Check if player is a social spy
     * @param uuid player's uuid
     */
    public static boolean isSocialSpy(UUID uuid) {
        return socialSpy.contains(uuid);
    }

    /**
     * Get online private message spies
     */
    public static List<Player> getSocialSpies() {
        return socialSpy.stream().filter(uuid -> server.getPlayer(uuid).isPresent() && server.getPlayer(uuid).get().isActive())
                .map(uuid -> server.getPlayer(uuid).get()).toList();
    }

    private static final HashSet<UUID> networkSpy = new HashSet<>();
    /**
     * Add or remove a new network chat spy
     * @param uuid command executor's uuid
     */
    public static boolean setNetworkSpy(UUID uuid) {
        if (!isNetworkSpy(uuid)) {
            networkSpy.add(uuid);
            return true;
        } else {
            networkSpy.remove(uuid);
            return false;
        }
    }
    /**
     * Check if player is a network spy
     * @param uuid player's uuid
     */
    public static boolean isNetworkSpy(UUID uuid) {
        return networkSpy.contains(uuid);
    }

    /**
     * Get network spies that are online
     */
    public static List<Player> getNetworkSpies() {
        return networkSpy.stream().filter(uuid -> server.getPlayer(uuid).isPresent() && server.getPlayer(uuid).get().isActive())
                .map(uuid -> server.getPlayer(uuid).get()).toList();
    }

    private static final HashSet<UUID> staffChat = new HashSet<>();
    /**
     * Add or remove a new staff chat user
     * @param uuid command executor's uuid
     */
    public static boolean toggleStaffChat(UUID uuid) {
        if (!isStaffChatEnabled(uuid)) {
            staffChat.add(uuid);
            return true;
        } else {
            staffChat.remove(uuid);
            return false;
        }
    }

    /**
     * Check if a user is in a staff chat
     * @param uuid player's uuid
     */
    public static boolean isStaffChatEnabled(UUID uuid) {
        return staffChat.contains(uuid);
    }

    /**
     * Get players that are in staff chat channel who are online
     */
    public static List<Player> getStaffChatUsers() {
        return staffChat.stream().filter(uuid -> server.getPlayer(uuid).isPresent() && server.getPlayer(uuid).get().isActive())
                .map(uuid -> server.getPlayer(uuid).get()).toList();
    }
}