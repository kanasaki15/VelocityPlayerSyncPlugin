package xyz.n7mn.dev.nanamiproxyplugin.JsonData;

public class SendData {

    private String ServerGroup;
    private long ServerNo;
    private String ServerName;

    private String[] PlayerUUIDList;
    private String[] PlayerNameList;

    public SendData(){
        this.ServerGroup = "";
        this.ServerNo = 0;
        this.ServerName = "";
    }

    public SendData(String serverGroup, long serverNo, String serverName, String[] playerUUIDList, String[] playerNameList){
        this.ServerGroup = serverGroup;
        this.ServerNo = serverNo;
        this.ServerName = serverName;
        this.PlayerUUIDList = playerUUIDList;
        this.PlayerNameList = playerNameList;
    }

    public String getServerGroup() {
        return ServerGroup;
    }

    public void setServerGroup(String serverGroup) {
        ServerGroup = serverGroup;
    }

    public long getServerNo() {
        return ServerNo;
    }

    public void setServerNo(long serverNo) {
        ServerNo = serverNo;
    }

    public String getServerName() {
        return ServerName;
    }

    public void setServerName(String serverName) {
        ServerName = serverName;
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

}
