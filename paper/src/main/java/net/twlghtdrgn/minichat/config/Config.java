package net.twlghtdrgn.minichat.config;

import lombok.Data;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

@Data
@ConfigSerializable
public class Config {
    private String displayNameFormat = "%luckperms_prefix% {PLAYERNAME} %luckperms_suffix%";
    private String divider = " <gray>►►";
    private GlobalChat globalChat = new GlobalChat();
    private LocalChat localChat = new LocalChat();
    private Enable enable = new Enable();
    private CrossServer crossServer = new CrossServer();
    private ProxySync proxySync = new ProxySync();
    private Messages messages = new Messages();

    @Data
    @ConfigSerializable
    public static class GlobalChat {
        private boolean enabled = false;
        private String prefix = "!";
        private String icon = "[G] ";
    }

    @Data
    @ConfigSerializable
    public static class LocalChat {
        private int range = 100;
        private String icon = "[L] ";
    }

    @Data
    @ConfigSerializable
    public static class Enable {
        @Setting("join-message")
        private boolean joinMessageEnabled = true;
        @Setting("leave-message")
        private boolean leaveMessageEnabled = true;
        @Setting("proxy-chat-logging")
        private boolean proxyChatLoggingEnabled = false;
    }

    @Data
    @ConfigSerializable
    public static class CrossServer {
        @Setting("cross-server")
        private boolean crossServerEnabled = false;
        @Setting("use-redis")
        private boolean useRedisEnabled = true;
        private String serverId = "main";
    }

    @Data
    @ConfigSerializable
    public static class ProxySync {
        private boolean enabled = false;
        private String serverName = "";
    }

    @Data
    @ConfigSerializable
    public static class Messages {
        private String vanishChatMessagePrevent = "Чат отключен, так как вы находитесь в режиме невидимки";
        private String spyEnabled = "Просмотр локального чата включен";
        private String spyDisabled = "Просмотр локального чата выключен";
    }
}
