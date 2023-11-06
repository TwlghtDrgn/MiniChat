package net.twlghtdrgn.minichat.command;

import com.velocitypowered.api.command.SimpleCommand;
import net.kyori.adventure.text.Component;
import net.twlghtdrgn.minichat.ChatPermission;
import net.twlghtdrgn.minichat.MiniChat;
import net.twlghtdrgn.minichat.util.EmojiUtil;
import net.twlghtdrgn.twilightlib.api.util.Format;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.StringJoiner;
import java.util.concurrent.CompletableFuture;

public class AlertCommand implements SimpleCommand {
    @Override
    public void execute(@NotNull Invocation invocation) {
        String[] args = invocation.arguments();
        if (args.length < 1) {
            invocation.source().sendMessage(Format.parse(MiniChat.getPlugin().getLang().get().getNotEnoughArgs()
                    .replace("%usage%","/alert <message>")));
            return;
        }

        StringJoiner sj = new StringJoiner(" ");
        for (String arg:args) sj.add(arg);

        String msg = MiniChat.getPlugin().getConf().get().getEmojis().isEmojiReplacerEnabled()
                ? EmojiUtil.replaceEmojis(sj.toString())
                : sj.toString();

        Component broadcastMessage = Format.parse(MiniChat.getPlugin().getLang().get().getAlert().replace("%message%", msg));
        MiniChat.getPlugin().getServer().sendMessage(broadcastMessage);
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
        return invocation.source().hasPermission(ChatPermission.COMMAND_ALERT);
    }
}
