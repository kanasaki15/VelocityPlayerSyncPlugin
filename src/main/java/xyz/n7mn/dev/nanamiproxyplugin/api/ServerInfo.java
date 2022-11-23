package xyz.n7mn.dev.nanamiproxyplugin.api;

import xyz.n7mn.dev.nanamiproxyplugin.data.ServerData;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ServerInfo {

    private final Connection con;
    private HashMap<String, ServerData> ServerInfoList = new HashMap<>();

    public ServerInfo(){
        con = null;
    }
    public ServerInfo(String pass){

        Connection temp;

        try {
            Class.forName("org.sqlite.JDBC");
            temp = DriverManager.getConnection("jdbc:sqlite:"+pass+"/data.db");
        } catch (SQLException e){
            e.printStackTrace();
            temp = null;
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        con = temp;

    }

    @Deprecated
    public List<ServerData> getServerList(){

        List<ServerData> list = new ArrayList<>();

        ServerInfoList.forEach((s, serverData) -> {
            list.add(serverData);
        });

        return list;
    }

    public List<ServerData> getServerList(String ServerName){

        ArrayList<ServerData> list = new ArrayList<>();

        ServerInfoList.forEach((s, serverData) -> {
            if (s.startsWith(ServerName+"_")){
                list.add(serverData);
            }
        });

        return list;
    }

    public ServerData getServer(String ServerName, int ServerID){
        return ServerInfoList.get(ServerName+"_"+ServerID);
    }

    public void addServer(ServerData data){
        ServerInfoList.put(data.getServerName()+"_"+data.getServerID(), data);
    }

    public void setServer(ServerData data){
        deleteServer(data);
        addServer(data);
    }

    public void deleteServer(ServerData data){
        ServerData temp = ServerInfoList.get(data.getServerName()+"_"+data.getServerID());

        if (temp != null){
            ServerInfoList.remove(temp);
        }
    }

    public void deleteServer(String ServerName, int ServerID){
        ServerData temp = ServerInfoList.get(ServerName + "_" + ServerID);
        if (temp != null){
            ServerInfoList.remove(temp);
        }
    }
}
