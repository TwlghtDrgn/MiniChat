package net.twlghtdrgn.minichat.event;

import lombok.Getter;

@Getter
public class NetworkMessageEvent {
    private final String sender;
    private final String message;
    private final String server;
    private final String channelType;

    public NetworkMessageEvent(String sender, String message, String server, String channelType) {
        this.sender = sender;
        this.message = message;
        this.server = server;
        this.channelType = channelType;
    }
}
