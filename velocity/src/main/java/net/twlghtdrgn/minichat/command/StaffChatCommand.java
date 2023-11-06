package net.twlghtdrgn.minichat.command;

import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import net.twlghtdrgn.minichat.ChatPermission;
import net.twlghtdrgn.minichat.MiniChat;
import net.twlghtdrgn.minichat.PlayerCache;
import net.twlghtdrgn.minichat.message.StaffMessage;
import net.twlghtdrgn.minichat.util.EmojiUtil;
import net.twlghtdrgn.twilightlib.api.util.Format;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.StringJoiner;
import java.util.concurrent.CompletableFuture;

public class StaffChatCommand implements SimpleCommand {
    @Override
    public void execute(@NotNull Invocation invocation) {
        if (!(invocation.source() instanceof Player p)) return;
        String[] args = invocation.arguments();

        if (args.length < 1) {
            if (PlayerCache.toggleStaffChat(p.getUniqueId())) {
                p.sendMessage(Format.parse(MiniChat.getPlugin().getLang().get().getStaffChatModeToggleOn()));
            } else {
                p.sendMessage(Format.parse(MiniChat.getPlugin().getLang().get().getStaffChatModeToggleOff()));
            }
            return;
        }

        StringJoiner sj = new StringJoiner(" ");
        for (String arg:args) sj.add(arg);

        new StaffMessage(p, sj.toString()).send();
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
        return invocation.source().hasPermission(ChatPermission.COMMAND_STAFF);
    }
}
