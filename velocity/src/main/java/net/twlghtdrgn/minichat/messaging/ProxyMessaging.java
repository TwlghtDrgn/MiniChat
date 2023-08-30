package net.twlghtdrgn.minichat.messaging;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.velocitypowered.api.event.player.ServerPostConnectEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import net.twlghtdrgn.minichat.MiniChat;
import net.twlghtdrgn.minichat.PlayerCache;
import net.twlghtdrgn.minichat.ServerCache;
import net.twlghtdrgn.minichat.config.Configuration;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class ProxyMessaging {
    private static final MinecraftChannelIdentifier proxyChannel = MinecraftChannelIdentifier.create("minichat","sync");
    public ProxyMessaging(@NotNull MiniChat plugin) {
        plugin.getServer().getChannelRegistrar().register(proxyChannel);
        plugin.getServer().getEventManager().register(plugin,this);
    }

    @Subscribe
    public void onPluginMessage(@NotNull PluginMessageEvent e) {
        if (e.getIdentifier() != proxyChannel) return;
        ByteArrayDataInput in = ByteStreams.newDataInput(e.getData());
        ServerConnection conn = (ServerConnection) e.getSource();
        String sub = in.readUTF();
        if (sub.equals("spy") && Configuration.getConfig().getSpy().isAutoNetworkSpyEnabled()) {
            Player p = conn.getPlayer();
            boolean b = in.readBoolean();
            if (b != PlayerCache.isNetworkSpy(p.getUniqueId())) PlayerCache.setNetworkSpy(p.getUniqueId());
        } else if (sub.equals("settings")) {
            ServerCache.CachedServer server = new ServerCache.CachedServer(in.readBoolean(), in.readUTF(), in.readBoolean());
            ServerCache.addCachedServer(conn.getServer().getServerInfo().getName(), server);
        }
    }

    @Subscribe
    public void onPlayerConnection(@NotNull ServerPostConnectEvent e) {
        sendMessage(e.getPlayer());
    }

    public static void sendMessage(@NotNull Player p) {
        if (!Configuration.getConfig().getSpy().isAutoNetworkSpyEnabled()) return;

        Optional<ServerConnection> conn = p.getCurrentServer();
        if (conn.isEmpty()) return;
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("spy");
        out.writeBoolean(PlayerCache.isNetworkSpy(p.getUniqueId()));
        conn.get().sendPluginMessage(proxyChannel, out.toByteArray());
    }
}
