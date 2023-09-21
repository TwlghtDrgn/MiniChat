package net.twlghtdrgn.minichat.command;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;
import net.twlghtdrgn.minichat.MiniChat;
import net.twlghtdrgn.minichat.PlayerCache;
import net.twlghtdrgn.minichat.config.Language;
import net.twlghtdrgn.minichat.messaging.ProxyMessaging;
import net.twlghtdrgn.twilightlib.api.util.Format;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class MiniChatCommand implements SimpleCommand {
    @Override
    public void execute(@NotNull Invocation invocation) {
        String[] args = invocation.arguments();
        CommandSource source = invocation.source();
        if (args.length < 1) {
            source.sendMessage(Format.parse(Language.getConfig().getNotEnoughArgs()
                    .replace("%usage%","/minichat-velocity <subcommand>")));
            return;
        }
        String arg = invocation.arguments()[0];
        switch (arg) {
            case "socialspy" -> {
                // logic for allowing seeing DMs
                if (!source.hasPermission("minichat.command.socialspy")) break;
                if (source instanceof Player p) {
                    if (PlayerCache.setSocialSpy(p.getUniqueId())) {
                        p.sendMessage(Component.text("Socialspy enabled"));
                    } else {
                        p.sendMessage(Component.text("Socialspy disabled"));
                    }
                } else source.sendMessage(Component.text("You can enable private message logging in plugin settings"));
            }
            case "networkspy" -> {
                // logic for allowing seeing all chats
                if (!source.hasPermission("minichat.command.networkspy")) break;
                if (source instanceof Player p) {
                    if (PlayerCache.setNetworkSpy(p.getUniqueId())) {
                        p.sendMessage(Component.text("Networkspy enabled"));
                    } else {
                        p.sendMessage(Component.text("Networkspy disabled"));
                    }
                    ProxyMessaging.sendMessage(p, ProxyMessaging.SyncID.SPY);
                } else source.sendMessage(Component.text("Enable network message logging in plugin settings"));
            }
            case "reload" -> {
                // config reload logic
                if (!source.hasPermission("minichat.command.reload")) break;
                if (MiniChat.getPlugin().getConfigLoader().reload()) source.sendMessage(Component.text("Config reloaded"));
                else source.sendMessage(Component.text("Check console for info. Configs are not reloaded."));
            }
            default -> source.sendMessage(Component.text("Unknown subcommand."));
        }
    }

    @Override
    public CompletableFuture<List<String>> suggestAsync(@NotNull Invocation invocation) {
        List<String> allSubcommands = new ArrayList<>();
        CommandSource source = invocation.source();
        String[] args = invocation.arguments();
        if (source.hasPermission("minichat.command.socialspy")) allSubcommands.add("socialspy");
        if (source.hasPermission("minichat.command.networkspy")) allSubcommands.add("networkspy");
        if (source.hasPermission("minichat.command.reload")) allSubcommands.add("reload");
        if (args.length == 0) {
            return CompletableFuture.completedFuture(allSubcommands);
        } else if (args.length == 1) {
            final List<String> sortedSubcommands = new ArrayList<>(
                    allSubcommands.stream()
                            .filter(s -> s.startsWith(args[0]))
                            .toList());
            Collections.sort(sortedSubcommands);
            return CompletableFuture.completedFuture(sortedSubcommands);
        }
        return CompletableFuture.completedFuture(allSubcommands);
    }

    @Override
    public boolean hasPermission(@NotNull Invocation invocation) {
        return invocation.source().hasPermission("minichat.command");
    }
}
