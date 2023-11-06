package net.twlghtdrgn.minichat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class ServerCache {
    private ServerCache() {}

    private static final Map<String, CachedServer> cachedServers = new HashMap<>();

    public static boolean isCached(String serverName) {
        return cachedServers.containsKey(serverName);
    }

    public static Optional<CachedServer> getCachedServer(String serverName) {
        if (!isCached(serverName)) return Optional.empty();
        return Optional.of(cachedServers.get(serverName));
    }

    public static void addCachedServer(String serverName, CachedServer cachedServer) {
        cachedServers.put(serverName, cachedServer);
    }

    public static @NotNull Set<String> getCachedServerNames() {
        return cachedServers.keySet();
    }

    public static @NotNull Set<Map.Entry<String, CachedServer>> getCachedServers() {
        return cachedServers.entrySet();
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CachedServer {
        private boolean channeled;
        private String globalChatPrefix;
        private boolean loggingEnabled;
    }
}
