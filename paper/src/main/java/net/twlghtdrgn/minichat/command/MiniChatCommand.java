package net.twlghtdrgn.minichat.command;

import net.twlghtdrgn.minichat.ChatPermission;
import net.twlghtdrgn.minichat.MiniChat;
import net.twlghtdrgn.minichat.PlayerCache;
import net.twlghtdrgn.minichat.messaging.ProxyMessaging;
import net.twlghtdrgn.minichat.messaging.SyncType;
import net.twlghtdrgn.twilightlib.api.util.Format;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class MiniChatCommand extends Command {
    public MiniChatCommand() {
        super("minichat", "MiniChat's main command", "/minichat reload", new ArrayList<>());
    }
    private static final String[] SUBCOMMANDS = {
            "reload",
            "localspy"
    };

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        if (!sender.hasPermission(ChatPermission.COMMAND)) return false;
        if (args.length < 1) {
            sender.sendMessage(Format.parse(String.format("Running MiniChat v%s", MiniChat.getPlugin().getPluginInfo().getPluginVersion())));
            return false;
        }

        if (args[0].equals(SUBCOMMANDS[0]) && sender.hasPermission(ChatPermission.COMMAND_RELOAD)) {
            if (MiniChat.getPlugin().reload()) {
                sender.sendMessage(Format.parse("<green>Config reloaded"));
                return true;
            }

            sender.sendMessage(Format.parse("<red>There's an error. Check console for info"));
            return false;
        } else if (args[0].equals(SUBCOMMANDS[1]) && sender.hasPermission(ChatPermission.COMMAND_LOCALSPY)) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage(Format.parse("Uh-oh! Console always spies, ya know?"));
                return false;
            }

            if (PlayerCache.setLocalSpy(player.getUniqueId())) {
                sender.sendMessage(Format.parse(MiniChat.getPlugin().getConf().get().getMessages().getSpyEnabled()));
            } else {
                sender.sendMessage(Format.parse(MiniChat.getPlugin().getConf().get().getMessages().getSpyDisabled()));
            }
            ProxyMessaging.sendMessage(player, SyncType.SPY);
            return true;
        }
        sender.sendMessage(Format.parse("<red>Unknown subcommand"));
        return false;
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String @NotNull [] args) throws IllegalArgumentException {
        final List<String> sorted = new ArrayList<>();
        if (sender.hasPermission(ChatPermission.COMMAND_RELOAD)) sorted.add(SUBCOMMANDS[0]);
        if (sender.hasPermission(ChatPermission.COMMAND_LOCALSPY)) sorted.add(SUBCOMMANDS[1]);
        if (args.length == 1) return sorted.stream().filter(s -> s.startsWith(args[0])).toList();
        if (args.length > 1) return List.of();
        return sorted;
    }
}
