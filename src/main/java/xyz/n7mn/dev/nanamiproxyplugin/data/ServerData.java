package xyz.n7mn.dev.nanamiproxyplugin.data;

import java.util.HashMap;
import java.util.UUID;

public class ServerData {

    private String ProxyName;
    private String ServerName;
    private int ServerID;
    private HashMap<UUID,String> PlayerList;

    public ServerData(){
        this.ProxyName = "";
        this.ServerName = "";
        this.ServerID = -1;
        this.PlayerList = new HashMap<>();
    }

    public ServerData(String proxyName, String serverName, int serverID, HashMap<UUID,String> playerList){
        this.ProxyName = proxyName;
        this.ServerName = serverName;
        this.ServerID = serverID;
        this.PlayerList = playerList;
    }

    public String getProxyName() {
        return ProxyName;
    }

    public void setProxyName(String proxyName) {
        ProxyName = proxyName;
    }

    public String getServerName() {
        return ServerName;
    }

    public void setServerName(String serverName) {
        ServerName = serverName;
    }

    public int getServerID() {
        return ServerID;
    }

    public void setServerID(int serverID) {
        ServerID = serverID;
    }

    public HashMap<UUID, String> getPlayerList() {
        return PlayerList;
    }

    public void setPlayerList(HashMap<UUID, String> playerList) {
        PlayerList = playerList;
    }
}