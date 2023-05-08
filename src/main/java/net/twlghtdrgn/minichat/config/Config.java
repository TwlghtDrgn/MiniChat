package net.twlghtdrgn.minichat.config;

import net.twlghtdrgn.twilightlib.config.ConfigBuilder;
import net.twlghtdrgn.twilightlib.config.ConfigLoader;
import org.spongepowered.configurate.ConfigurationNode;

import java.io.IOException;

public class Config {
    private Config() {}
    private static ConfigurationNode cfg;
    public static void load() throws IOException {
        ConfigBuilder builder = ConfigBuilder.newBuilder()
                .name("config.yml")
                .addRow("%luckperms_prefix% {playername} %luckperms_suffix% <gray>►►<reset>", "placeholder")
                .addRow(false,"global-chat")
                .addRow("!","global-chat-prefix")
                .addRow("[G]","global-chat-icon")
                .addRow("[L]","local-chat-icon")
                .addRow(100,"local-chat-range")
                .addRow(false,"cross-server")
                .addRow(false,"disable-join-message")
                .addRow(false,"disable-leave-message")
                .addRow("Чат отключен, так как вы находитесь в режиме невидимки","vanish-send-prevent-message")
                .build();
        cfg = ConfigLoader.load(builder);
    }

    public static String getPlaceholder() {
        return cfg.node("placeholder").getString("<MiniChat>");
    }

    public static boolean isGlobalEnabled() {
        return cfg.node("global-chat").getBoolean(false);
    }

    public static String getGlobalChatPrefix() {
        return cfg.node("global-chat-prefix").getString("!");
    }

    public static boolean isCrossServerEnabled() {
        return cfg.node("cross-server").getBoolean(false);
    }

    public static double getLocalChatRange() {
        return cfg.node("local-chat-range").getDouble(100);
    }

    public static String getGlobalChatIcon() {
        return cfg.node("global-chat-icon").getString("");
    }

    public static String getLocalChatIcon() {
        return cfg.node("local-chat-icon").getString("");
    }

    public static boolean getJoinMessageDisabled() {
        return cfg.node("disable-join-message").getBoolean(false);
    }

    public static boolean getLeaveMessageDisabled() {
        return cfg.node("disable-leave-message").getBoolean(false);
    }

    public static String getVanishChatDisabledMessage() {
        return cfg.node("vanish-send-prevent-message").getString("<red>Chat is disabled when you are vanished");
    }
}
