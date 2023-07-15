package net.twlghtdrgn.minichat.config;

import lombok.Data;
import lombok.Getter;
import net.twlghtdrgn.minichat.MiniChatVelocity;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.yaml.NodeStyle;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.IOException;
import java.nio.file.Path;

public class Lang {
    private Lang() {}
    @Getter
    private static LangConfig config;
    private static final String PATH = MiniChatVelocity.getPlugin().getDataDirectory().toString();

    public static void load() throws IOException {
        Path path = Path.of(PATH + "/lang.yml");
        YamlConfigurationLoader loader = YamlConfigurationLoader.builder()
                .path(path)
                .nodeStyle(NodeStyle.BLOCK)
                .build();

        CommentedConfigurationNode node = loader.load();

        config = node.get(LangConfig.class);

        node.set(LangConfig.class, config);
        loader.save(node);
    }

    @Data
    @ConfigSerializable
    public static class LangConfig {
        private String directMessageInFormat = "<gray>[<white>%sender% <gold>-> <white>%recipients%<gray>] <reset>> %message%";
        private String directMessageOutFormat = "<gray>[<white>You <gold>-> <white>%recipients%<gray>] <reset>> %message%";
        private String directMessageOverLimit = "You have tried to send message to %current% players. Maximum players is %max%";

        private String directMessagePlayerNotFound = "%player% is offline";

        private String directMessageBlock = "You have blocked %player%'s ability to send you DMs";
        private String directMessageUnblock = "Now %player% can send you DMs again";
        private String directMessageBlocked = "You have been blocked by that player";

        private String alert = "<gray>[<red>Alert</red>]</gray> %message%";

        private String socialSpyFormat = "<gray>[%sender% -> %recipients%] > %message%";
        private String networkSpyFormat = "<gray>[%server%] %channel_type% %sender% > %message%";

        private String directMessageConsoleLogFormat = "[MSG] %sender% -> %recipients% > %message%";
        private String networkMessageConsoleLogFormat = "[CHAT | %server%] %channel_type% %sender% > %message%";

        private String notEnoughArgs = "Not enough arguments. Usage: %usage%";
    }
}
