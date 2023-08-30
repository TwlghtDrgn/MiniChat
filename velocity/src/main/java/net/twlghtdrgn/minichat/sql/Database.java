package net.twlghtdrgn.minichat.sql;

import net.twlghtdrgn.minichat.MiniChat;
import net.twlghtdrgn.twilightlib.api.sql.SQL;
import net.twlghtdrgn.twilightlib.api.sql.SQLiteConnector;
import org.jetbrains.annotations.NotNull;

import java.sql.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Database {
    private Database() {}
    private static SQL connector;

    public static void load() throws SQLException, ClassNotFoundException {
        connector = new SQLiteConnector(MiniChat.getPlugin());
        try (Connection conn = connector.getConnection()) {
            try (Statement st = conn.createStatement()) {
                st.execute("create table if not exists blacklist(player varchar(40) unique, blocked_players text)");
                MiniChat.getPlugin().getLogger().info("Database loaded");
            }
        }
    }

    public static Set<String> getBlockedPlayers(@NotNull UUID sender) {
        try (Connection conn = connector.getConnection()) {
            try (PreparedStatement pst = conn.prepareStatement("select blocked_players from blacklist where player = ?")) {
                pst.setString(1, sender.toString());
                ResultSet rst = pst.executeQuery();
                HashSet<String> ignored = new HashSet<>();
                while (rst.next()) {
                    ignored.addAll(deserialize(rst.getString("blocked_players")));
                }
                return ignored;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return Set.of();
        }
    }

    public static void addBlockedPlayer(@NotNull UUID sender, @NotNull String target) {
        final Set<String> ignoredPlayers = getBlockedPlayers(sender);
        ignoredPlayers.add(target);
        try (Connection conn = connector.getConnection()) {
            try (PreparedStatement pst = conn.prepareStatement(
                    "insert into blacklist values (?, ?)" +
                            " on conflict(player) do update set blocked_players = ?")) {
                String players = serialize(ignoredPlayers);
                pst.setString(1, sender.toString());
                pst.setString(2, players);
                pst.setString(3, players);
                pst.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void removeBlockedPlayer(@NotNull UUID sender, @NotNull String target) {
        final Set<String> ignoredPlayers = getBlockedPlayers(sender);
        ignoredPlayers.remove(target);
        try (Connection conn = connector.getConnection()) {
            try (PreparedStatement pst = conn.prepareStatement(
                    "insert into blacklist values (?, ?)" +
                            " on conflict(player) do update set blocked_players = ?")) {
                String players = serialize(ignoredPlayers);
                pst.setString(1, sender.toString());
                pst.setString(2, players);
                pst.setString(3, players);
                pst.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean isBlocked(@NotNull UUID sender, @NotNull String target) {
        return getBlockedPlayers(sender).contains(target);
    }

    private static @NotNull String serialize(@NotNull Set<String> nicks) {
        final StringBuilder sb = new StringBuilder("[");
        for (String s:nicks) {
            sb.append(s).append(",");
        }
        sb.append("]");
        return sb.toString();
    }

    private static Set<String> deserialize(@NotNull String s) {
        if (s.startsWith("[") && s.endsWith("]")) {
            String[] u = s
                    .replaceFirst("\\[","")
                    .replaceFirst("]","")
                    .split(",");
            return new HashSet<>(Arrays.asList(u));
        } else return Set.of();
    }
}
