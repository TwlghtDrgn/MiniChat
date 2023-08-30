package net.twlghtdrgn.minichat.util;

import net.twlghtdrgn.minichat.config.Configuration;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class EmojiUtil {
    private EmojiUtil() {}
    public static String replaceEmojis(String message) {
        Map<String, String> emojis = Configuration.getConfig().getEmojis().getEmojis();
        for (Map.Entry<String,String> emoji:emojis.entrySet()) {
            message = message.replace(emoji.getKey(), emoji.getValue());
        }
        return message;
    }

    public static @NotNull List<String> getSortedEmojis(String content) {
        Set<String> emojis = Configuration.getConfig().getEmojis().getEmojis().keySet();
        final List<String> sortedEmojis = new ArrayList<>();
        for (String emoji:emojis)
            if (emoji.startsWith(content)) sortedEmojis.add(emoji);
        Collections.sort(sortedEmojis);
        return sortedEmojis;
    }
}
