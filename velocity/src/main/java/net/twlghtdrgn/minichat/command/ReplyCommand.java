package net.twlghtdrgn.minichat.command;

import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;
import net.twlghtdrgn.minichat.ChatPermission;
import net.twlghtdrgn.minichat.MiniChat;
import net.twlghtdrgn.minichat.PlayerCache;
import net.twlghtdrgn.minichat.message.PrivateMessage;
import net.twlghtdrgn.minichat.util.EmojiUtil;
import net.twlghtdrgn.twilightlib.api.util.Format;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class ReplyCommand implements SimpleCommand {
    @Override
    public void execute(@NotNull Invocation invocation) {
        if (!(invocation.source() instanceof Player p)) {
            invocation.source().sendMessage(Component.text("This command can be used only by a player"));
            return;
        }

        if (invocation.alias().equals("chat")) {
            if (PlayerCache.toggleChatMode(p.getUniqueId())) {
                p.sendMessage(Format.parse(MiniChat.getPlugin().getLang().get().getChatModeToggleOn()));
            } else {
                p.sendMessage(Format.parse(MiniChat.getPlugin().getLang().get().getChatModeToggleOff()));
            }
            return;
        }

        String[] args = invocation.arguments();
        if (args.length < 1) {
            p.sendMessage(Format.parse(MiniChat.getPlugin().getLang().get().getNotEnoughArgs()
                    .replace("%usage%","/r <Message>")));
            return;
        }

        Optional<Set<UUID>> lastRecipients = PlayerCache.getLastRecipients(p.getUniqueId());
        if (lastRecipients.isEmpty()) {
            p.sendMessage(Format.parse(MiniChat.getPlugin().getLang().get().getDirectMessage().getPlayerNotFound()));
            return;
        }

        StringJoiner sj = new StringJoiner(" ");
        for (String arg:args) sj.add(arg);

        new PrivateMessage(p, lastRecipients.get(), sj.toString()).send();
    }

    @Override
    public CompletableFuture<List<String>> suggestAsync(@NotNull Invocation invocation) {
        String[] args = invocation.arguments();

        if (args.length < 1) return CompletableFuture.completedFuture(List.of());
        if (MiniChat.getPlugin().getConf().get().getEmojis().isEmojiReplacerEnabled()) {
            String arg = args[args.length - 1];
            if (arg.startsWith(":"))
                return CompletableFuture.completedFuture(EmojiUtil.getSortedEmojis(arg));
        }
        return CompletableFuture.completedFuture(List.of());
    }

    @Override
    public boolean hasPermission(@NotNull Invocation invocation) {
        return invocation.source().hasPermission(ChatPermission.COMMAND_REPLY);
    }
}
