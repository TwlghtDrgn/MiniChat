package net.twlghtdrgn.minichat.command;

import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import net.kyori.adventure.text.Component;
import net.twlghtdrgn.minichat.MiniChat;
import net.twlghtdrgn.minichat.config.Configuration;
import net.twlghtdrgn.minichat.config.Language;
import net.twlghtdrgn.minichat.message.PrivateMessage;
import net.twlghtdrgn.minichat.util.EmojiUtil;
import net.twlghtdrgn.twilightlib.api.util.Format;
import net.twlghtdrgn.vanishbridge.VanishBridge;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class MessageCommand implements SimpleCommand {
    private static final ProxyServer server = MiniChat.getPlugin().getServer();
    @Override
    public void execute(@NotNull Invocation invocation) {
        if (!(invocation.source() instanceof Player player)) {
            invocation.source().sendMessage(Component.text("This command can be used only by a player"));
            return;
        }
        String[] args = invocation.arguments();

        if (args.length < 2) {
            player.sendMessage(Format.parse(Language.getConfig().getNotEnoughArgs()
                    .replace("%usage%","/msg <Player[,Player]> <Message>")));
            return;
        }

        Set<UUID> receivers = Arrays.stream(args[0].split(","))
                .filter(nick -> server.getPlayer(nick).isPresent())
                .map(nick -> server.getPlayer(nick).get().getUniqueId())
                .collect(Collectors.toSet());

        StringJoiner joiner = new StringJoiner(" ");
        for (int i = 1; i < args.length; i++)
            joiner.add(args[i]);

        new PrivateMessage(player, receivers, joiner.toString()).send();
    }

    @Override
    public CompletableFuture<List<String>> suggestAsync(@NotNull Invocation invocation) {
        if (!(invocation.source() instanceof Player player)) return CompletableFuture.completedFuture(List.of());
        String[] args = invocation.arguments();

        if (args.length < 2) {
            Collection<Player> onlinePlayers = MiniChat.getPlugin().getServer().getAllPlayers();
            List<String> nicknames = new ArrayList<>();
            for (Player p : onlinePlayers) {
                if (player.equals(p) || (MiniChat.getPlugin().isVanishBridgeInstalled()
                        && VanishBridge.getPlugin().isVanished(p.getUsername()))) continue;
                nicknames.add(p.getUsername());
            }

            if (args.length == 0) return CompletableFuture.completedFuture(nicknames);

            String[] splitNames = invocation.arguments()[0].split(",");

            String fullNames = "";
            if (splitNames.length > 1) fullNames = Arrays.stream(Arrays.copyOf(splitNames, splitNames.length - 1))
                    .collect(Collectors.joining(",", "", ","));

            List<String> sortedNicknames = new ArrayList<>();
            for (String n : nicknames) {
                if (n.startsWith(splitNames[splitNames.length - 1]))
                    sortedNicknames.add(fullNames + n);
            }
            Collections.sort(sortedNicknames);
            return CompletableFuture.completedFuture(sortedNicknames);
        } else if (Configuration.getConfig().getEmojis().isEmojiReplacerEnabled()) {
            String arg = args[args.length - 1];
            if (arg.startsWith(":"))
                return CompletableFuture.completedFuture(EmojiUtil.getSortedEmojis(arg));
        }
        return CompletableFuture.completedFuture(List.of());
    }

    @Override
    public boolean hasPermission(@NotNull Invocation invocation) {
        return invocation.source().hasPermission("minichat.command.message");
    }
}
