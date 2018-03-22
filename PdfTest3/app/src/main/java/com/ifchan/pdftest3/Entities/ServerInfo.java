package com.ifchan.p2p.Netword;

public class ServerInfo {
    private String ip;
    private String roomName;

    public ServerInfo(String ip, String roomName) {
        this.ip = ip;
        this.roomName = roomName;
    }

    public String getIp() {
        return ip;
    }

    public String getRoomName() {
        return roomName;
    }
}
