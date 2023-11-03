package net.twlghtdrgn.minichat.command;

import net.twlghtdrgn.minichat.MiniChat;
import net.twlghtdrgn.minichat.PlayerCache;
import net.twlghtdrgn.minichat.config.Configuration;
import net.twlghtdrgn.minichat.messaging.ProxySyncMessaging;
import net.twlghtdrgn.minichat.messaging.SyncType;
import net.twlghtdrgn.twilightlib.api.util.Format;
import org.bukkit.Bukkit;
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

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        if (!sender.hasPermission("minichat.command")) return false;
        if (args.length < 1) {
            sender.sendMessage(Format.parse(String.format("Running MiniChat v%s", MiniChat.getPlugin().getPluginInfo().getPluginVersion())));
            return false;
        }
        if (args[0].equals("reload") && sender.hasPermission("minichat.command.reload")) {
            if (MiniChat.getPlugin().getConfigLoader().reload()) {
                sender.sendMessage(Format.parse("<green>Config reloaded"));
                if (sender instanceof Player p) {
                    ProxySyncMessaging.sendMessage(p, SyncType.SETTINGS);
                    return true;
                } else {
                    if (Bukkit.getOnlinePlayers().isEmpty()) {
                        sender.sendMessage("Some functions (like proxy synchronization) requires at least one player online on the server");
                        return false;
                    } else {
                        ProxySyncMessaging.sendMessage(Bukkit.getOnlinePlayers().stream().findFirst().get(), SyncType.SETTINGS);
                        return true;
                    }
                }
            } else {
                sender.sendMessage(Format.parse("<red>There's an error. Check console for information"));
                return false;
            }
        } else if (args[0].equals("spy") && sender.hasPermission("minichat.command.spy")) {
            if (sender instanceof Player p) {
                if (PlayerCache.setLocalSpy(p.getUniqueId())) {
                    sender.sendMessage(Format.parse(Configuration.getConfig().getMessages().getSpyEnabled()));
                } else {
                    sender.sendMessage(Format.parse(Configuration.getConfig().getMessages().getSpyDisabled()));
                }
                ProxySyncMessaging.sendMessage(p, SyncType.SPY);
            } else sender.sendMessage(Format.parse("Uh-oh! Console always spies, ya know?"));
        }
        return false;
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String @NotNull [] args) throws IllegalArgumentException {
        List<String> subcommands = new ArrayList<>();
        if (sender.hasPermission("minichat.command.reload")) subcommands.add("reload");
        if (sender.hasPermission("minichat.command.spy")) subcommands.add("spy");
        if (args.length == 1)
            subcommands = subcommands.stream().filter(s -> s.startsWith(args[0])).toList();
        return subcommands;
    }
}
