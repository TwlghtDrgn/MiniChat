package net.twlghtdrgn.minichat.util;

import net.twlghtdrgn.minichat.config.MainConfig;

public class ReplaceUtil {
    private ReplaceUtil() {}
    public static String replaceEmojis(String message) {
        for (String s:MainConfig.getConfig().getEmojis().keySet()) {
            String replacement = MainConfig.getConfig().getEmojis().get(s);
            message = message.replace(s, replacement);
        }

        return message;
    }
}
