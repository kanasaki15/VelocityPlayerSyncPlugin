package xyz.n7mn.dev.nanamiproxyplugin.ServerData;

public class ServerList {

    private String GroupName;
    private int ServerID;
    private String ServerName;
    private int PlayerCount;
    private String[] PlayerUUIDList;
    private String[] PlayerNameList;

    public ServerList(String groupName, int serverId, String serverName, int playerCount, String[] playerUUIDList, String[] playerNameList) {
        GroupName = groupName;
        ServerID = serverId;
        ServerName = serverName;
        PlayerCount = playerCount;
        PlayerUUIDList = playerUUIDList;
        PlayerNameList = playerNameList;
    }

    public String getGroupName() {
        return GroupName;
    }

    public void setGroupName(String groupName) {
        GroupName = groupName;
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

    public int getPlayerCount() {
        return PlayerCount;
    }

    public void setPlayerCount(int playerCount) {
        PlayerCount = playerCount;
    }

    public String[] getPlayerUUIDList() {
        return PlayerUUIDList;
    }

    public void setPlayerUUIDList(String[] playerUUIDList) {
        PlayerUUIDList = playerUUIDList;
    }

    public String[] getPlayerNameList() {
        return PlayerNameList;
    }

    public void setPlayerNameList(String[] playerNameList) {
        PlayerNameList = playerNameList;
    }

    public boolean equals(ServerList data) {
        if (this == data) return true;
        if (data == null || getClass() != data.getClass()) return false;
        return GroupName.equals(data.GroupName);
    }
}
