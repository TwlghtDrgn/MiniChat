package net.twlghtdrgn.minichat.command;

import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import net.twlghtdrgn.minichat.ChatPermission;
import net.twlghtdrgn.minichat.MiniChat;
import net.twlghtdrgn.minichat.sql.Database;
import net.twlghtdrgn.twilightlib.api.util.Format;
import net.twlghtdrgn.vanishbridge.VanishBridge;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class BlockCommand implements SimpleCommand {
    @Override
    public void execute(@NotNull Invocation invocation) {
        if (!(invocation.source() instanceof Player)) {
            invocation.source().sendMessage(Format.parse("This command is player-only"));
            return;
        }
        String alias = invocation.alias();
        if (alias.equals("block")) {
            block(invocation);
        } else if (alias.equals("unblock")) {
            unblock(invocation);
        }
    }

    @Override
    public CompletableFuture<List<String>> suggestAsync(@NotNull Invocation invocation) {
        String alias = invocation.alias();
        if (!(invocation.source() instanceof Player sender)) {
            return CompletableFuture.completedFuture(List.of());
        }
        String[] args = invocation.arguments();
        if (args.length > 1) return CompletableFuture.completedFuture(List.of());
        if (alias.equals("block")) {
            Collection<Player> onlinePlayers = MiniChat.getPlugin().getServer().getAllPlayers();
            List<String> nicknames = new ArrayList<>();
            if (args.length < 1) {
                onlinePlayers.stream()
                        .filter(player ->
                            MiniChat.getPlugin().isVanishBridgeInstalled()
                            && !VanishBridge.getPlugin().isVanished(player.getUsername()))
                        .filter(player -> !player.getUsername().equals(sender.getUsername()))
                        .forEach(p -> nicknames.add(p.getUsername()));
                return CompletableFuture.completedFuture(nicknames);
            }

            onlinePlayers.stream()
                    .filter(player ->
                            MiniChat.getPlugin().isVanishBridgeInstalled()
                                    && !VanishBridge.getPlugin().isVanished(player.getUsername()))
                    .filter(player -> player.getUsername().startsWith(args[0]))
                    .filter(player -> !player.getUsername().equals(sender.getUsername()))
                    .forEach(p -> nicknames.add(p.getUsername()));

            Collections.sort(nicknames);
            return CompletableFuture.completedFuture(nicknames);
        } else if (alias.equals("unblock")) {
            Set<String> ignoredPlayers = Database.getBlockedPlayers(sender.getUniqueId());
            List<String> nicknames = new ArrayList<>();
            if (args.length < 1) {
                return CompletableFuture.completedFuture(new ArrayList<>(ignoredPlayers));
            }
            for (String nick:ignoredPlayers) {
                if (nick.startsWith(args[0])) {
                    nicknames.add(nick);
                }
            }
            return CompletableFuture.completedFuture(nicknames);
        }
        return CompletableFuture.completedFuture(List.of());
    }

    @Override
    public boolean hasPermission(@NotNull Invocation invocation) {
        return invocation.source().hasPermission(ChatPermission.COMMAND_BLOCK);
    }

    private void block(@NotNull Invocation invocation) {
        if (invocation.arguments().length < 1) {
            invocation.source().sendMessage(Format.parse(
                    MiniChat.getPlugin().getLang().get().getNotEnoughArgs()
                            .replace("%usage%","/block <nickname>")));
            return;
        }
        Player p = (Player) invocation.source();
        if (p.getUsername().equals(invocation.arguments()[0])) {
            p.sendMessage(Format.parse(MiniChat.getPlugin().getLang().get().getDirectMessage().getSelfBlockedError()));
            return;
        }
        if (!Database.isBlocked(p.getUniqueId(), invocation.arguments()[0])) {
            Database.addBlockedPlayer(p.getUniqueId(), invocation.arguments()[0]);
            p.sendMessage(Format.parse(MiniChat.getPlugin().getLang().get().getDirectMessage().getBlock()
                    .replace("%player%", invocation.arguments()[0])));
        } else p.sendMessage(Format.parse(MiniChat.getPlugin().getLang().get().getDirectMessage().getAlreadyBlocked()));
    }

    private void unblock(@NotNull Invocation invocation) {
        if (invocation.arguments().length < 1) {
            invocation.source().sendMessage(Format.parse(
                    MiniChat.getPlugin().getLang().get().getNotEnoughArgs()
                            .replace("%usage%","/unblock <nickname>")));
            return;
        }
        Player p = (Player) invocation.source();
        if (Database.isBlocked(p.getUniqueId(), invocation.arguments()[0])) {
            Database.removeBlockedPlayer(p.getUniqueId(), invocation.arguments()[0]);
            p.sendMessage(Format.parse(MiniChat.getPlugin().getLang().get().getDirectMessage().getUnblock()
                    .replace("%player%", invocation.arguments()[0])));
        } else p.sendMessage(Format.parse(MiniChat.getPlugin().getLang().get().getDirectMessage().getNotBlocked()));
    }
}
