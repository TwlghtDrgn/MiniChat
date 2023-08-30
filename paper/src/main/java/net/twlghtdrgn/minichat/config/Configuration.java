package net.twlghtdrgn.minichat.config;

import lombok.Data;
import lombok.Getter;
import net.twlghtdrgn.minichat.MiniChat;
import net.twlghtdrgn.twilightlib.api.config.AbstractConfig;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

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
        private String placeholder = "%luckperms_prefix% {playername} %luckperms_suffix% <gray>►►<reset>";
        private GlobalChat globalChat = new GlobalChat();
        private LocalChat localChat = new LocalChat();
        private Disable disable = new Disable();
        private CrossServer crossServer = new CrossServer();
        private Messages messages = new Messages();

        @Data
        @ConfigSerializable
        public static class GlobalChat {
            private boolean enabled = false;
            private String prefix = "!";
            private String icon = "[G]";
        }

        @Data
        @ConfigSerializable
        public static class LocalChat {
            private String icon = "[L]";
            private int range = 100;
        }

        @Data
        @ConfigSerializable
        public static class Disable {
            @Setting("join-message")
            private boolean joinMessageDisabled = false;
            @Setting("leave-message")
            private boolean leaveMessageDisabled = false;
            @Setting("proxy-chat-logging")
            private boolean proxyChatLoggingDisabled = false;
        }

        @Data
        @ConfigSerializable
        public static class CrossServer {
            @Setting("cross-server")
            private boolean crossServerEnabled = false;
            private String serverId = "main";
        }

        @Data
        @ConfigSerializable
        public static class Messages {
            private String vanishChatMessagePrevent = "Чат отключен, так как вы находитесь в режиме невидимки";
            private String spyEnabled = "Просмотр локального чата включен";
            private String spyDisabled = "Просмотр локального чата выключен";
        }
    }
}
