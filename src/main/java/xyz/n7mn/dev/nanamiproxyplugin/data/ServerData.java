package xyz.n7mn.dev.nanamiproxyplugin.data;

import java.util.List;
import java.util.UUID;

public class ServerData {

    private String ProxyName;
    private String ServerGroup;
    private int ServerID;
    private String ServerName;
    private List<PlayerData> playerList;

    public ServerData(String proxyName, String serverGroup, int serverID, String serverName, List<PlayerData> playerList) {
        ProxyName = proxyName;
        ServerGroup = serverGroup;
        ServerID = serverID;
        ServerName = serverName;
        this.playerList = playerList;
    }

    public String getProxyName() {
        return ProxyName;
    }

    public void setProxyName(String proxyName) {
        ProxyName = proxyName;
    }

    public String getServerGroup() {
        return ServerGroup;
    }

    public void setServerGroup(String serverGroup) {
        ServerGroup = serverGroup;
    }

    public int getServerID() {
        return ServerID;
    }

    public void setServerID(int serverID) {
        ServerID = serverID;
    }

    public String getServerName() {
        return ServerName;
    }

    public void setServerName(String serverName) {
        ServerName = serverName;
    }

    public List<PlayerData> getPlayerList() {
        return playerList;
    }

    public void setPlayerList(List<PlayerData> playerList) {
        this.playerList = playerList;
    }
}


class PlayerData {
    private UUID UserUUID;
    private String Username;

    public PlayerData(UUID userUUID, String username){
        this.UserUUID = userUUID;
        this.Username = username;
    }

    public String getUsername() {
        return Username;
    }

    public UUID getUserUUID() {
        return UserUUID;
    }
}