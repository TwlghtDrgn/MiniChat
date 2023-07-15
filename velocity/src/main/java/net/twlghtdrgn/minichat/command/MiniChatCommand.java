package net.twlghtdrgn.minichat.command;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.RawCommand;
import com.velocitypowered.api.proxy.Player;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.twlghtdrgn.minichat.MiniChatVelocity;
import net.twlghtdrgn.minichat.config.Lang;
import net.twlghtdrgn.minichat.config.MainConfig;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class MiniChatCommand implements RawCommand {
    @Getter
    private static final List<Player> socialSpyPlayers = new ArrayList<>();
    @Getter
    private static final List<Player> networkSpyPlayers = new ArrayList<>();

    @Override
    public void execute(@NotNull Invocation invocation) {
        String arguments = invocation.arguments();
        CommandSource source = invocation.source();
        if (arguments == null) {
            source.sendMessage(Component.text(Lang.getConfig().getNotEnoughArgs()));
            return;
        }
        switch (arguments) {
            case "socialspy" -> {
                // logic for allowing seeing DMs
                if (!source.hasPermission("minichat.command.socialspy")) break;
                if (source instanceof Player p) {
                    if (!socialSpyPlayers.contains(p)) {
                        socialSpyPlayers.add(p);
                        p.sendMessage(Component.text("Socialspy enabled"));
                    } else {
                        socialSpyPlayers.remove(p);
                        p.sendMessage(Component.text("Socialspy disabled"));
                    }
                } else source.sendMessage(Component.text("Enable private message logging in plugin settings"));
            }
            case "networkspy" -> {
                // logic for allowing seeing all chats
                if (!source.hasPermission("minichat.command.networkspy")) break;
                if (source instanceof Player p) {
                    if (!networkSpyPlayers.contains(p)) {
                        networkSpyPlayers.add(p);
                        p.sendMessage(Component.text("Networkspy enabled"));
                    } else {
                        networkSpyPlayers.remove(p);
                        p.sendMessage(Component.text("Networkspy disabled"));
                    }
                } else source.sendMessage(Component.text("Enable network message logging in plugin settings"));
            }
            case "reload" -> {
                // config reload logic
                if (!source.hasPermission("minichat.command.reload")) break;
                try {
                    MainConfig.load();
                    Lang.load();
                    source.sendMessage(Component.text("Config reloaded"));
                } catch (Exception e) {
                    source.sendMessage(Component.text("Unable to reload config. Check console."));
                    MiniChatVelocity.getPlugin().getLogger().error("Unable to reload config files. Additional info can be found below");
                    e.printStackTrace();
                }
            }
            default -> source.sendMessage(Component.text("Unknown subcommand."));
        }

    }

    @Override
    public boolean hasPermission(@NotNull Invocation invocation) {
        return invocation.source().hasPermission("minichat.command");
    }
}
