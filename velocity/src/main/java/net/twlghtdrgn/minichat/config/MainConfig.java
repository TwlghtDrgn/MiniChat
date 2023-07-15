package net.twlghtdrgn.minichat.config;


import jdk.jfr.Description;
import lombok.Data;
import lombok.Getter;
import net.twlghtdrgn.minichat.MiniChatVelocity;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;
import org.spongepowered.configurate.objectmapping.meta.Setting;
import org.spongepowered.configurate.yaml.NodeStyle;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class MainConfig {
    private MainConfig() {}
    @Getter
    private static ConfigurationFile config;
    private static final String PATH = MiniChatVelocity.getPlugin().getDataDirectory().toString();

    public static void load() throws IOException {
        Path path = Path.of(PATH + "/config.yml");
        YamlConfigurationLoader loader = YamlConfigurationLoader.builder()
                .path(path)
                .nodeStyle(NodeStyle.BLOCK)
                .build();
        CommentedConfigurationNode node = loader.load();

        config = node.get(ConfigurationFile.class);

        node.set(ConfigurationFile.class, config);
        loader.save(node);

    }

    @Data
    @ConfigSerializable
    public static class ConfigurationFile {
        @Setting(value = "enable-discord-webhook")
        @Comment(value = "Enable this if you want to log chat messages to discord")
        private boolean discordWebhook = false;
        @Setting(value = "discord-webhook-link")
        @Comment(value = "If you have enabled discord webhook, then you should change this value to your webhook link")
        private String discordWebhookLink = "none";

        @Setting(value = "replace-emojis")
        @Comment(value = "If you wish to use unicode, or any other emojis in chat, that enable this and add your emojis")
        private boolean emojiReplacer = false;
        @Setting(value = "emojis")
        @Description(value = "Usage:\n - \"emoji_name\":\"emoji\"")
        private Map<String, String> emojis = new HashMap<>();

        @Setting("global-chat-prefix")
        @Description(value = "If you use MiniChat on your servers (or any plugin with global/local channels, really), set your global chat prefix from the servers")
        private String globalChatPrefix = "!";
        private String localChatSymbol = "L";
        private String globalChatSymbol = "G";
        @Setting(value = "channeled-servers")
        @Comment(value = "If you use MiniChat on your servers (or any plugin with global/local channels, really), list those servers here (used for ServerSpy feature)")
        private String[] channeled = {"example-server","another-example-server"};

        @Setting(value = "max-recipients")
        @Comment(value = "Maximum amount of recipients for direct messages")
        private int maxRecipients = 5;
        private String[] messageAliases = {"message","msg","tell","t","whisper","w","pm","t"};
        private String[] replyAliases = {"reply","r"};

        private boolean networkLoggingEnabled = false;
        private boolean directMessageLoggingEnabled = false;
    }
}
