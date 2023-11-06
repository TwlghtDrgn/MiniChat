package net.twlghtdrgn.minichat.util;

import net.twlghtdrgn.minichat.MiniChat;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public class EmojiUtil {
    private EmojiUtil() {}
    public static String replaceEmojis(String message) {
        final Map<String, String> emojis = MiniChat.getPlugin().getConf().get().getEmojis().getEmojis();
        for (Map.Entry<String,String> emoji:emojis.entrySet()) {
            message = message.replace(emoji.getKey(), emoji.getValue());
        }
        return message;
    }

    public static @NotNull List<String> getSortedEmojis(String content) {
        return MiniChat.getPlugin().getConf().get().getEmojis().getEmojis().keySet().stream()
                .filter(s -> s.startsWith(content))
                .sorted()
                .toList();
    }
}
