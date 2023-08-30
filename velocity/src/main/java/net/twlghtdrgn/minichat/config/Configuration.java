package net.twlghtdrgn.minichat.config;

import lombok.Data;
import lombok.Getter;
import net.twlghtdrgn.minichat.MiniChat;
import net.twlghtdrgn.twilightlib.api.config.AbstractConfig;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import java.util.HashMap;
import java.util.Map;

public class Configuration extends AbstractConfig {
    @Getter
    private static Config config;

    public Configuration(String configName) {
        super(configName, Config.class);
    }

    @Override
    public void reload() throws ConfigurateException {
        config = (Config) MiniChat.getPlugin().getConfigLoader().load(this);
    }

    @Data
    @ConfigSerializable
    public static class Config {
        @Setting("direct-message-recipient-limit")
        private int maxRecipients = 5;

        private Aliases aliases = new Aliases();
        private Emoji emojis = new Emoji();
        private Spy spy = new Spy();
        private Logging logging = new Logging();

        @Data
        @ConfigSerializable
        public static class Aliases {
            @Setting("message")
            private String[] messageAliases = {
                    "message",
                    "whisper",
                    "tell",
                    "msg",
                    "pm",
                    "t",
                    "w",
                    "m"
            };
            @Setting("reply")
            private String[] replyAliases = {
                    "reply",
                    "r"
            };
            private String[] staffChatAliases = {
                    "mc",
                    "sc",
                    "staff",
                    "modchat",
                    "staffchat"
            };
        }

        @Data
        @ConfigSerializable
        public static class Spy {
            @Setting("auto-network-spy")
            private boolean autoNetworkSpyEnabled;
            private String localChatSymbol = "L";
            private String globalChatSymbol = "G";
        }

        @Data
        @ConfigSerializable
        public static class Emoji {
            @Setting(value = "replace-emojis")
            private boolean emojiReplacerEnabled;
            @Setting(value = "emojis")
            private final Map<String, String> emojis = new HashMap<>();
        }

        @Data
        @ConfigSerializable
        public static class Logging {
            @Setting(value = "log-network-messages")
            boolean networkLoggingEnabled;
            @Setting(value = "log-direct-messages")
            boolean directMessageLoggingEnabled;
        }
    }
}
