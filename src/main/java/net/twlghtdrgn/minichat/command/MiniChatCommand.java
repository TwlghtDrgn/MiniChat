package net.twlghtdrgn.minichat.command;

import net.twlghtdrgn.minichat.MiniChat;
import net.twlghtdrgn.minichat.config.Config;
import net.twlghtdrgn.twilightlib.util.Format;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
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
            sender.sendMessage(Format.parse(String.format("Running MiniChat v%s", MiniChat.getPlugin().getPluginMeta().getVersion())));
            return false;
        }
        if (args[0].equals("reload")) {
            try {
                Config.load();
                sender.sendMessage(Format.parse("<green>Config reloaded"));
                return true;
            } catch (Exception e) {
                sender.sendMessage(Format.parse("<red>There's an error. Check console for information"));
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }
}
