package xyz.n7mn.dev.nanamiproxyplugin;

import com.amihaiemil.eoyaml.Yaml;
import com.amihaiemil.eoyaml.YamlMapping;
import com.amihaiemil.eoyaml.YamlMappingBuilder;
import com.amihaiemil.eoyaml.YamlNode;
import com.google.gson.Gson;
import com.google.inject.Inject;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyPingEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.PluginContainer;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.proxy.server.ServerPing;
import com.velocitypowered.api.util.Favicon;
import net.kyori.adventure.text.Component;
import org.slf4j.Logger;
import xyz.n7mn.dev.nanamiproxyplugin.JsonData.ReceiveData;
import xyz.n7mn.dev.nanamiproxyplugin.JsonData.SendData;
import xyz.n7mn.dev.nanamiproxyplugin.ServerData.ServerList;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Plugin(
        id = "nanamiproxyplugin",
        name = "NanamiProxyPlugin",
        version = BuildConstants.VERSION,
        description = "NanamiProxyPlugin",
        url = "https://twitter.com/NanamiProxyPlugin",
        authors = {"7mi_chan"}
)
public class Nanamiproxyplugin {

    @Inject
    private Logger logger;

    @Inject
    private ProxyServer proxyServer;

    private Optional<PluginContainer> plugin;

    private HashMap<String, ServerList> ProxyServerList;

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {

        plugin = proxyServer.getPluginManager().getPlugin("nanamiproxyplugin");

        File file1 = new File("./plugins/" + plugin.get().getDescription().getName().get());
        File file2 = new File("./plugins/" + plugin.get().getDescription().getName().get() + "/server-7mi.xyz.yml");
        File file3 = new File("./plugins/" + plugin.get().getDescription().getName().get() + "/config.yml");
        if (!file1.exists()){
            file1.mkdir();
        }

        if (!file2.exists()){
            YamlMappingBuilder builder = Yaml.createYamlMappingBuilder();
            YamlMapping mapping = builder.add(
                    "ProxyName", "test"
            ).add(
                    "MinProtocolVer", "47"
            ).add(
                    "MaxProtocolVer", "758"
            ).add(
                    "VersionText","NanamiServer 1.8-1.18.2"
            ).add(
                    "ServerGroup","Sample"
            ).add(
                    "ServerID","0"
            ).add(
                    "ServerName","Sample"
            ).add(
                    "ServerText","[1.8-1.18.2] ななみ鯖"
            ).build();

            String yml = mapping.toString();

            try {
                PrintWriter writer = new PrintWriter(file2);
                writer.print(yml);
                writer.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        }

        boolean NewConfig = false;
        if (!file3.exists()){
            YamlMappingBuilder builder = Yaml.createYamlMappingBuilder();
            YamlMapping mapping = builder.add(
                    "ServerIP", "localhost"
            ).add(
                    "ServerPort", "26666"
            ).build();

            String yml = mapping.toString();

            try {
                PrintWriter writer = new PrintWriter(file3);
                writer.print(yml);
                writer.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            NewConfig = true;
        }


        if (NewConfig){
            logger.info("config.ymlの設定をしてください。");
            return;
        }


        File ConfigFile = new File("./plugins/" + plugin.get().getDescription().getName().get() + "/config.yml");
        if (!ConfigFile.exists()){
            return;
        }

        // 初期状態構築
        File[] files = new File("./plugins/" + plugin.get().getDescription().getName().get()).listFiles();
        for (File file : files){

            if (!file.getName().startsWith("server-")){
                continue;
            }

            try {
                YamlMapping mapping = Yaml.createYamlInput(file).readYamlMapping();
                String proxyName = mapping.string("ProxyName");
                String serverGroup = mapping.string("ServerGroup");
                int serverID = mapping.integer("ServerID");
                String serverName = mapping.string("ServerName");

                ProxyServerList.put(proxyName, new ServerList(serverGroup, serverID, serverName, 0, new String[0], new String[0]));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        String ServerIP = "localhost";
        int ServerPort = 25565;

        try {
            YamlMapping mapping = Yaml.createYamlInput(ConfigFile).readYamlMapping();
            ServerIP = mapping.string("ServerIP");
            ServerPort = mapping.integer("ServerPort");
        } catch (Exception e){
            e.printStackTrace();
        }

        logger.info("プレーヤー同期開始");
        Timer timer = new Timer();
        int finalServerPort = ServerPort;
        String finalServerIP = ServerIP;
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                new Thread(()-> {
                    try {
                        for (RegisteredServer server : proxyServer.getAllServers()){
                            String proxyName = server.getServerInfo().getName();
                            ServerList s = ProxyServerList.get(proxyName);
                            if (s == null){
                                continue;
                            }

                            // String serverGroup, long serverNo, String serverName, String[] playerUUIDList, String[] playerNameList
                            String[] temp1 = new String[server.getPlayersConnected().size()];
                            String[] temp2 = new String[server.getPlayersConnected().size()];

                            int i = 0;
                            for (Player p : server.getPlayersConnected()){
                                temp1[i] = p.getUniqueId().toString();
                                temp2[i] = p.getUsername();
                                i++;
                            }

                            SendData data = new SendData(s.getGroupName(), s.getServerID(), s.getServerName(), temp1, temp2);
                            Socket sock1 = new Socket(finalServerIP, finalServerPort);
                            OutputStream out1 = sock1.getOutputStream();
                            InputStream in1 = sock1.getInputStream();

                            out1.write(new Gson().toJson(data).getBytes(StandardCharsets.UTF_8));
                            out1.flush();

                            byte[] tempReceive = new byte[262144];
                            int size1 = in1.read(tempReceive);
                            tempReceive = Arrays.copyOf(tempReceive, size1);
                            ReceiveData receiveData = new Gson().fromJson(new String(tempReceive), ReceiveData.class);

                            ProxyServerList.remove(server.getServerInfo().getName());
                            ProxyServerList.put(server.getServerInfo().getName(), new ServerList(receiveData.getGroupName(), s.getServerID(), s.getServerName(), receiveData.getPlayerCount(), receiveData.getPlayerUUIDList(), receiveData.getPlayerList()));

                            out1.close();
                            in1.close();
                            sock1.close();

                        }
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }).start();
            }
        }, 0L, 1000L);

    }

    @Subscribe
    public void ProxyPingEvent(ProxyPingEvent e){
        String hostName = e.getConnection().getVirtualHost().get().getHostName();
        ServerPing ping = e.getPing();
        ServerPing.Builder builder = ping.asBuilder();
        Component text = ping.getDescriptionComponent();

        try {

            File file1 = new File("./plugins/" + plugin.get().getDescription().getName().get() + "/server-" + hostName + ".yml");

            if (file1.exists()){
                YamlMapping mapping = Yaml.createYamlInput(file1).readYamlMapping();
                // System.out.println("---- debug ----\n"+mapping.toString()+"\n---- debug ----");

                ServerList server = ProxyServerList.get(mapping.string("ProxyName"));
                if (server != null){
                    builder.onlinePlayers(server.getPlayerCount());
                }

                String desc = mapping.string("ServerText");
                String verText = mapping.string("VersionText");

                if (desc != null){
                    text = Component.text(desc);
                }

                if (verText != null){

                    int minProtocolVer = Integer.parseInt(mapping.string("MinProtocolVer"));
                    int maxProtocolVer = Integer.parseInt(mapping.string("MaxProtocolVer"));

                    if (e.getConnection().getProtocolVersion().getProtocol() >= minProtocolVer && e.getConnection().getProtocolVersion().getProtocol() <= maxProtocolVer){
                        builder.version(new ServerPing.Version(e.getConnection().getProtocolVersion().getProtocol(), verText));
                    } else if (e.getConnection().getProtocolVersion().getProtocol() >= maxProtocolVer){
                        builder.version(new ServerPing.Version(maxProtocolVer, verText));
                    } else if (e.getConnection().getProtocolVersion().getProtocol() <= minProtocolVer){
                        builder.version(new ServerPing.Version(minProtocolVer, verText));
                    } else {
                        builder.version(new ServerPing.Version(maxProtocolVer, verText));
                    }
                    
                }
            }


            //String url = ping.getFavicon().get().getBase64Url();
            //System.out.println("icon : "+url);

            File file2 = new File("./plugins/" + plugin.get().getDescription().getName().get() + "/" + hostName + ".png");
            //System.out.println(file.toPath());

            if (file2.exists()){
                //System.out.println("!!");
                String contentType = Files.probeContentType(file2.toPath());

                StringBuilder sb = new StringBuilder();
                sb.append("data:");
                sb.append(contentType);
                sb.append(";base64,");
                sb.append(Base64.getEncoder().encodeToString(Files.readAllBytes(file2.toPath())));

                Favicon favicon = new Favicon(sb.toString());
                //System.out.println(sb.toString());
                builder.favicon(favicon);
            }

            builder.description(text);
            e.setPing(builder.build());

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}