package xyz.n7mn.dev.nanamiproxyplugin.data;

import java.util.HashMap;
import java.util.UUID;

public class ServerInfoData {

    /*
    ProxyName: Sample
    MinProtocolVer: 47
    MaxProtocolVer: 758
    VersionText: NanamiProxySystem 2.0
    ServerGroup: Sample
    ServerID: 0
    ServerName: Sample
    ServerText: サンプルファイルです。 「sample.7mi.xyz」の部分を実際のアドレスに置き換えてください。
    ServerMaxPlayers: 100
     */

    private String HostName;
    private String ProxyServerName;
    private int JoinMinProtocolVer;
    private int JoinMaxProtocolVer;
    private String VerText;
    private String ServerName;
    private int ServerID;
    private String ServerBio;
    private int ServerMaxPlayers;
    private HashMap<UUID, String> PlayerList;

    public ServerInfoData(){
        this.HostName = "sample.7mi.xyz";
        this.ProxyServerName = "Sample";
        this.JoinMinProtocolVer = 47;
        this.JoinMaxProtocolVer = 760;
        this.VerText = "NanamiProxy 1.8-1.19.2";
        this.ServerName = "sample";
        this.ServerID = 0;
        this.ServerBio = "サンプルサンプルサンプルサンプルサンプル";
        this.ServerMaxPlayers = 500;
        this.PlayerList = new HashMap<>();
    }

    public ServerInfoData(String hostName, String proxyServerName, int joinMinProtocolVer, int joinMaxProtocolVer, String verText, String serverName, int serverID, String serverBio, int serverMaxPlayers, HashMap<UUID, String> playerList) {
        HostName = hostName;
        ProxyServerName = proxyServerName;
        JoinMinProtocolVer = joinMinProtocolVer;
        JoinMaxProtocolVer = joinMaxProtocolVer;
        VerText = verText;
        ServerName = serverName;
        ServerID = serverID;
        ServerBio = serverBio;
        ServerMaxPlayers = serverMaxPlayers;
        PlayerList = playerList;
    }

    public String getHostName() {
        return HostName;
    }

    public void setHostName(String hostName) {
        HostName = hostName;
    }

    public String getProxyServerName() {
        return ProxyServerName;
    }

    public void setProxyServerName(String proxyServerName) {
        ProxyServerName = proxyServerName;
    }

    public int getJoinMinProtocolVer() {
        return JoinMinProtocolVer;
    }

    public void setJoinMinProtocolVer(int joinMinProtocolVer) {
        JoinMinProtocolVer = joinMinProtocolVer;
    }

    public int getJoinMaxProtocolVer() {
        return JoinMaxProtocolVer;
    }

    public void setJoinMaxProtocolVer(int joinMaxProtocolVer) {
        JoinMaxProtocolVer = joinMaxProtocolVer;
    }

    public String getVerText() {
        return VerText;
    }

    public void setVerText(String verText) {
        VerText = verText;
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

    public String getServerBio() {
        return ServerBio;
    }

    public void setServerBio(String serverBio) {
        ServerBio = serverBio;
    }

    public int getServerMaxPlayers() {
        return ServerMaxPlayers;
    }

    public void setServerMaxPlayers(int serverMaxPlayers) {
        ServerMaxPlayers = serverMaxPlayers;
    }

    public HashMap<UUID, String> getPlayerList() {
        return PlayerList;
    }

    public void setPlayerList(HashMap<UUID, String> playerList) {
        PlayerList = playerList;
    }
}
