package xyz.n7mn.dev.nanamiproxyplugin.JsonData;

public class ReceiveData {

    private String GroupName;
    private int PlayerCount;
    private String[] PlayerUUIDList;
    private String[] PlayerList;

    public ReceiveData(String groupName, int playerCount, String[] playerUUIDList, String[] playerList) {
        GroupName = groupName;
        PlayerCount = playerCount;
        PlayerUUIDList = playerUUIDList;
        PlayerList = playerList;
    }

    public ReceiveData() {

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

    public String[] getPlayerList() {
        return PlayerList;
    }

    public void setPlayerList(String[] playerList) {
        PlayerList = playerList;
    }
}
