package net.twlghtdrgn.minichat.config;

import lombok.Data;
import lombok.Getter;
import net.twlghtdrgn.minichat.MiniChat;
import net.twlghtdrgn.twilightlib.api.config.AbstractConfig;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

public class Language extends AbstractConfig {
    @Getter
    private static Config config;
    public Language(String configName) {
        super(configName, Config.class);
    }

    @Override
    public void reload() throws ConfigurateException {
        config = (Config) MiniChat.getPlugin().getConfigLoader().load(this);
    }

    @Data
    @ConfigSerializable
    public static class Config {
        private String alert = "<red>Alert <gray>►►<reset> %message%";
        private String staffChatFormat = "<gray>[<white>STAFF<gray> | <white>%server%<gray>] <gold>%sender% <gray>►►<reset> %message%";
        private String notEnoughArgs = "<red>Not enough arguments. Usage: %usage%";
        private String clickToReply = "<gold>Click on message to reply";
        private String chatModeToggleOn = "<green>You have enabled DM-as-chat mode";
        private String chatModeToggleOff = "<red>You have disabled DM-as-chat mode";
        private String staffChatModeToggleOn = "<green>You have enabled staff-as-chat mode";
        private String staffChatModeToggleOff = "<red>You have disabled staff-as-chat mode";
        private DirectMessage directMessage = new DirectMessage();
        private Spy spy = new Spy();

        @Data
        @ConfigSerializable
        public static class DirectMessage {
            private String senderFormat = "You <gold>→ <white>%recipients% <gray>►►<reset> %message%";
            private String receiverFormat = "%sender% <gold>→ <white>%recipients% <gray>►►<reset> %message%";

            private String recipientLimit = "<red>You have tried to send message to %current% players. Maximum players is %max%";

            private String playerNotFound = "<red>There's no one to send a message..";
            private String playerOffline = "<red>%player% is offline";

            private String alreadyBlocked = "You have already blocked that player";
            private String notBlocked = "This player is not blocked";
            private String block = "<red>You have blocked %player%'s ability to send you DMs";
            private String unblock = "<green>Now %player% can send you DMs again";
            private String blocked = "<red>You have been blocked by %player%";
            private String blockedError = "<red>You can't send messages to %player%, because you've added that player to a blacklist";
            private String selfBlockedError = "<red>You can't block yourself.";
        }

        @Data
        @ConfigSerializable
        public static class Spy {
            private String socialSpyFormat = "<gray>[%sender% -> %recipients%] > %message%";
            private String networkSpyFormat = "<gray>[%server%] %channel_type% %sender% > %message%";

            private String socialSpyConsoleFormat = "[DM] %sender% -> %recipients% > %message%";
            private String networkSpyConsoleFormat = "[CHAT | %server%] %channel_type% %sender% > %message%";

            private String localChatSymbol = "L";
            private String globalChatSymbol = "G";
        }
    }
}
