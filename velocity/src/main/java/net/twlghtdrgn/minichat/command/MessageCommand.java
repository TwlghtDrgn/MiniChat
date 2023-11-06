package net.twlghtdrgn.minichat.command;

import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import net.kyori.adventure.text.Component;
import net.twlghtdrgn.minichat.ChatPermission;
import net.twlghtdrgn.minichat.MiniChat;
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
            player.sendMessage(Format.parse(MiniChat.getPlugin().getLang().get().getNotEnoughArgs()
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

        if (args.length <= 1) {
            final List<String> onlineNicknames = new ArrayList<>(MiniChat.getPlugin().getServer().getAllPlayers().stream()
                    .map(Player::getUsername)
                    .filter(nickname -> (!MiniChat.getPlugin().isVanishBridgeInstalled() || !VanishBridge.getPlugin().isVanished(nickname)))
                    .filter(nickname -> !player.getUsername().equals(nickname))
                    .toList());
            if (args.length == 0) return CompletableFuture.completedFuture(onlineNicknames);
            final String[] recipients = invocation.arguments()[0].split(",");
            for (String s:recipients) {
                onlineNicknames.remove(s);
            }

            String fullNames = "";
            if (recipients.length > 1) fullNames = Arrays.stream(Arrays.copyOf(recipients, recipients.length - 1))
                    .collect(Collectors.joining(",", "", ","));

            List<String> sortedNicknames = new ArrayList<>();
            for (String n:onlineNicknames) {
                if (n.toLowerCase().startsWith(recipients[recipients.length - 1].toLowerCase()))
                    sortedNicknames.add(fullNames + n);
            }

            Collections.sort(sortedNicknames);
            return CompletableFuture.completedFuture(sortedNicknames);
        }

        if (args.length > 2 && MiniChat.getPlugin().getConf().get().getEmojis().isEmojiReplacerEnabled()) {
            String arg = args[args.length - 1];
            if (arg.startsWith(":"))
                return CompletableFuture.completedFuture(EmojiUtil.getSortedEmojis(arg));
        }

        return CompletableFuture.completedFuture(List.of());
    }

    @Override
    public boolean hasPermission(@NotNull Invocation invocation) {
        return invocation.source().hasPermission(ChatPermission.COMMAND_MESSAGE);
    }
}
