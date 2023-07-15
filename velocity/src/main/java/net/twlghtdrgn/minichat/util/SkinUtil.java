package net.twlghtdrgn.minichat.util;

import net.skinsrestorer.api.SkinsRestorerAPI;
import net.skinsrestorer.api.property.IProperty;
import net.twlghtdrgn.minichat.MiniChatVelocity;

public class SkinUtil {
    private SkinUtil() {}
    private static SkinsRestorerAPI api;
    private static final String SKIN_URL = "https://mc-heads.net/avatar/%nickname";
    public static void load() {
        try {
            api = SkinsRestorerAPI.getApi();
        } catch (Exception e) {
            MiniChatVelocity.getPlugin().getLogger().error("Unable to load SkinsRestorer, but it's loaded. Disabling SkinsRestorer support");
            MiniChatVelocity.getPlugin().setSkinsRestorerInstalled(false);
            e.printStackTrace();
        }
    }

    public static String getSkinLink(String nickname) {
        if (!MiniChatVelocity.getPlugin().isSkinsRestorerInstalled())
            return SKIN_URL.replace("%nickname", nickname);

        String skinNickname = api.getSkinName(nickname);
        IProperty skinData = api.getSkinData(skinNickname);

        if (skinData == null)
            return SKIN_URL.replace("%nickname", skinNickname != null ? skinNickname : nickname);

        return api.getSkinTextureUrl(skinData);
    }
}
