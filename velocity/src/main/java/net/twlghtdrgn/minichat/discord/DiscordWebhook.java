package net.twlghtdrgn.minichat.discord;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import net.twlghtdrgn.minichat.MiniChatVelocity;
import net.twlghtdrgn.minichat.config.MainConfig;
import net.twlghtdrgn.minichat.util.SkinUtil;

import java.util.Arrays;

public class DiscordWebhook {
    private static WebhookClient client;
    private final String nickname;
    private final String server;
    private String message;
    public DiscordWebhook(String nickname, String server, String message) {
        this.nickname = nickname;
        this.server = server;
        this.message = message;
    }

    public static void load() {
        String url = MainConfig.getConfig().getDiscordWebhookLink();
        if (url.equals("none")) {
            MiniChatVelocity.getPlugin().getLogger().warn("You have not changed webhook link from default. Disabling this feature until reload");
            MainConfig.getConfig().setDiscordWebhook(false);
            return;
        }
        client = WebhookClient.withUrl(MainConfig.getConfig().getDiscordWebhookLink());
    }

    public void send() {
        boolean channeled = Arrays.asList(MainConfig.getConfig().getChanneled()).contains(server);
        message = channeled ? message.replaceFirst("!", "Global | ") : "Local | " + message;

        WebhookMessageBuilder builder = new WebhookMessageBuilder();
        builder.setAvatarUrl(SkinUtil.getSkinLink(nickname));
        builder.setUsername(nickname + " (" + server + ")");
        builder.setContent(message);

        client.send(builder.build());
    }
}
