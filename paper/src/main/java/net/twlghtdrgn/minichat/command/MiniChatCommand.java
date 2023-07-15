package net.twlghtdrgn.minichat.command;

import net.twlghtdrgn.minichat.MiniChatPaper;
import net.twlghtdrgn.minichat.config.Config;
import net.twlghtdrgn.minichat.listener.ChatListener;
import net.twlghtdrgn.twilightlib.util.Format;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class MiniChatCommand extends Command {
    public MiniChatCommand() {
        super("minichat", "MiniChat's main command", "/minichat reload", new ArrayList<>());
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        if (!sender.hasPermission("minichat.command")) return false;
        if (args.length < 1) {
            sender.sendMessage(Format.parse(String.format("Running MiniChat v%s", MiniChatPaper.getPlugin().getPluginMeta().getVersion())));
            return false;
        }
        if (args[0].equals("reload") && sender.hasPermission("minichat.command.reload")) {
            try {
                Config.load();
                sender.sendMessage(Format.parse("<green>Config reloaded"));
                return true;
            } catch (Exception e) {
                sender.sendMessage(Format.parse("<red>There's an error. Check console for information"));
                e.printStackTrace();
                return false;
            }
        } else if (args[0].equals("spy") && sender.hasPermission("minichat.command.spy")) {
            if (sender instanceof Player p) {
                if (!ChatListener.getSpies().contains(p)) {
                    ChatListener.getSpies().add(p);
                    p.sendMessage(Format.parse(Config.getSpyEnabledMessage()));
                } else {
                    ChatListener.getSpies().remove(p);
                    p.sendMessage(Format.parse(Config.getSpyDisabledMessage()));
                }
            } else sender.sendMessage(Format.parse("Uh-oh! Console always spies, ya know?"));
        }
        return false;
    }
}
