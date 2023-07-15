package net.twlghtdrgn.minichat.command;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;
import net.twlghtdrgn.minichat.MiniChatVelocity;
import net.twlghtdrgn.minichat.PlayerCache;
import net.twlghtdrgn.minichat.config.Lang;
import net.twlghtdrgn.minichat.config.MainConfig;
import net.twlghtdrgn.minichat.listener.ChatListener;
import net.twlghtdrgn.minichat.message.PrivateMessage;
import net.twlghtdrgn.vanishbridge.VanishBridge;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class MessageCommand implements SimpleCommand {
    @Override
    public void execute(@NotNull Invocation invocation) {
        String[] messageAliases = MainConfig.getConfig().getMessageAliases();
        String[] replyAliases = MainConfig.getConfig().getReplyAliases();

        String alias = invocation.alias();

        if (Arrays.asList(messageAliases).contains(alias)) {
            msg(invocation);
        } else if (Arrays.asList(replyAliases).contains(alias)) {
            reply(invocation);
        } else if (alias.equals("chat")) {
            if (invocation.source() instanceof Player p) {
                if (!ChatListener.getDirectMessageChatMode().contains(p)) {
                    ChatListener.getDirectMessageChatMode().add(p);
                    p.sendMessage(Component.text("Direct message as chat has been enabled"));
                } else {
                    ChatListener.getDirectMessageChatMode().remove(p);
                    p.sendMessage(Component.text("Direct message as chat has been disabled"));
                }
            } else invocation.source().sendMessage(Component.text("This function can be used only by player"));
        }
    }

    private void msg(@NotNull Invocation invocation) {
        CommandSource source = invocation.source();
        String[] args = invocation.arguments();

        if (args.length < 2) {
            source.sendMessage(Component.text(Lang.getConfig().getNotEnoughArgs()));
            return;
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 1; i < args.length; i++)
            sb.append(args[i]).append(" ");

        new PrivateMessage(source,args[0], sb.toString()).send();
    }

    private void reply(@NotNull Invocation invocation) {
        if (!(invocation.source() instanceof Player p)) {
            return;
        }

        String[] args = invocation.arguments();
        if (args.length < 1) {
            p.sendMessage(Component.text(Lang.getConfig().getNotEnoughArgs()));
            return;
        }

        Optional<HashSet<UUID>> lastRecipients = PlayerCache.getLastRecipients(p.getUniqueId());
        if (lastRecipients.isEmpty()) {
            p.sendMessage(Component.text(Lang.getConfig().getDirectMessagePlayerNotFound()));
            return;
        }

        StringBuilder recipientList = new StringBuilder();
        for (UUID last:lastRecipients.get()) {
            MiniChatVelocity.getPlugin().getServer().getPlayer(last).ifPresent(r -> recipientList.append(r.getUsername()).append(","));
        }

        StringBuilder sb = new StringBuilder();
        for (String arg:args) sb.append(arg).append(" ");

        new PrivateMessage(invocation.source(), recipientList.toString(), sb.toString()).send();
    }

    @Override
    public List<String> suggest(Invocation invocation) {
        return SimpleCommand.super.suggest(invocation);
    }

    @Override
    public CompletableFuture<List<String>> suggestAsync(Invocation invocation) {
        List<String> nicknames = new ArrayList<>();
        Collection<Player> onlinePlayers = MiniChatVelocity.getPlugin().getServer().getAllPlayers();
        for (Player p:onlinePlayers) {
            if (MiniChatVelocity.getPlugin().isVanishBridgeInstalled()
                    && VanishBridge.getPlugin().isVanished(p.getUsername())) continue;
            nicknames.add(p.getUsername());
        }

        if (invocation.arguments().length < 1)
            return CompletableFuture.completedFuture(nicknames);
        else {
            List<String> sortedNicknames = new ArrayList<>();
            for (String n:nicknames) {
                if (n.startsWith(invocation.arguments()[0]))
                    sortedNicknames.add(n);
            }
            Collections.sort(sortedNicknames);
            return CompletableFuture.completedFuture(sortedNicknames);
        }
    }

    @Override
    public boolean hasPermission(@NotNull Invocation invocation) {
        return invocation.source().hasPermission("minichat.command.dm");
    }
}
