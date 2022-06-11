package xyz.n7mn.dev.nanamiproxyplugin.ServerData;

import java.util.Arrays;
import java.util.Objects;

public class ServerList {

    private String GroupName;
    private int PlayerCount;
    private String[] PlayerUUIDList;
    private String[] PlayerNameList;

    public ServerList(String groupName, int playerCount, String[] playerUUIDList, String[] playerNameList) {
        GroupName = groupName;
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
