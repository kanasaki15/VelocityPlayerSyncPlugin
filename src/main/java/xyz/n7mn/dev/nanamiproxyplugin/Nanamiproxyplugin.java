package xyz.n7mn.dev.nanamiproxyplugin;

import com.amihaiemil.eoyaml.Yaml;
import com.amihaiemil.eoyaml.YamlMapping;
import com.amihaiemil.eoyaml.YamlMappingBuilder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
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
import net.kyori.adventure.text.TextComponent;
import org.slf4j.Logger;
import xyz.n7mn.dev.nanamiproxyplugin.api.ServerInfo;
import xyz.n7mn.dev.nanamiproxyplugin.data.ServerData;
import xyz.n7mn.dev.nanamiproxyplugin.data.ServerInfoData;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

@Plugin(
        id = "nanamiproxyplugin",
        name = "NanamiProxyPlugin",
        version = BuildConstants.VERSION,
        description = "VelocityPlayerSyncPlugin",
        url = "https://twitter.com/7mi_network",
        authors = {"7mi_chan"}
)
public class Nanamiproxyplugin {

    @Inject
    private Logger logger;

    @Inject
    private ProxyServer proxyServer;

    private Optional<PluginContainer> plugin;

    private ServerInfo api;

    private HashMap<String, ServerInfoData> ServerList = new HashMap<>();

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {

        plugin = proxyServer.getPluginManager().getPlugin("nanamiproxyplugin");

        File file1 = new File("./plugins/" + plugin.get().getDescription().getName().get());
        File file2 = new File("./plugins/" + plugin.get().getDescription().getName().get() + "/server-sample.7mi.xyz.yml");
        File file3 = new File("./plugins/" + plugin.get().getDescription().getName().get() + "/config.yml");

        api = new ServerInfo("./plugins/" + plugin.get().getDescription().getName().get());

        new SyncServer(file3, api, logger).start();

        if (!file1.exists()){
            file1.mkdir();
        }

        if (!file2.exists()){

            // すでに他のファイルあったら生成しない
            File[] files = new File("./plugins/" + plugin.get().getDescription().getName().get()).listFiles();
            boolean f = false;
            for (File file : files){
                if (file.getName().toLowerCase().startsWith("server-") && file.getName().toLowerCase().endsWith(".yml")){
                    f = true;
                    break;
                }
            }

            if (!f){
                String yml = "" +
                        "ProxyName: Sample\n" +
                        "MinProtocolVer: 47\n" +
                        "MaxProtocolVer: 758\n" +
                        "VersionText: NanamiProxySystem 2.0\n" +
                        "ServerGroup: Sample\n" +
                        "ServerID: 0\n" +
                        "ServerName: Sample\n" +
                        "ServerText: サンプルファイルです。 「sample.7mi.xyz」の部分を実際のアドレスに置き換えてください。\n" +
                        "ServerMaxPlayers: 100";

                try {
                    PrintWriter writer = new PrintWriter(file2);
                    writer.print(yml);
                    writer.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }

        boolean NewConfig = false;
        if (!file3.exists()){
            String yml = "" +
                    "# trueで集計同期サーバー機能オン。1つだけtrueにしてあとはfalseにして同期させる想定。もちろんfalseでも同期できないだけで動く。\n" +
                    "ProxyMode: false\n" +
                    "# 上の集計同期サーバー機能がオンのとき、何番のポートで受付するかの設定\n" +
                    "ProxyPort: 30000\n" +
                    "# 今はまだ未実装。そのうちhttpが追加される\n" +
                    "ProxyServerMode: tcp\n" +
                    "# 集計同期サーバーのIP\n" +
                    "ProxyServerIP: localhost\n" +
                    "# 集計同期サーバーのポート\n" +
                    "ProxyServerPort: 30000\n" +
                    "\n" +
                    "\n" +
                    "# 下のいじらないでね\n" +
                    "ConfigVer: 1.1";
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

        // コンフィグなかったら以降の処理を設定してないので無駄なのでしない
        File ConfigFile = new File("./plugins/" + plugin.get().getDescription().getName().get() + "/config.yml");
        if (!ConfigFile.exists()){
            return;
        }

        YamlMapping ConfigYaml = null;
        try {
            ConfigYaml = Yaml.createYamlInput(ConfigFile).readYamlMapping();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        boolean isProxyMode = Boolean.parseBoolean(ConfigYaml.string("ProxyMode"));

        // 定期タスク生成して同期
        HashMap<String, UUID> tempPlayerList = new HashMap<>();

        Timer timer = new Timer();
        YamlMapping finalConfigYaml = ConfigYaml;
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                new Thread(()-> {
                    Collection<Player> players = proxyServer.getAllPlayers();
                    // 前回の同期と変わってなければ処理しない
                    if (tempPlayerList.size() == players.size() && ServerList.size() > 0){
                        return;
                    }

                    // 同期する鯖のリストを構築する
                    ServerList.clear();

                    File[] files = new File("./plugins/" + plugin.get().getDescription().getName().get()).listFiles();
                    for (File file : files){
                        if (file.getName().toLowerCase().startsWith("server-") && file.getName().toLowerCase().endsWith(".yml")){
                            try {
                                YamlMapping serverConfig = Yaml.createYamlInput(file).readYamlMapping();

                                Pattern compile = Pattern.compile("server\\-(.*)\\.yml");
                                Matcher matcher = compile.matcher(file.getName());
                                String hostName = matcher.find() ? matcher.group(1) : "";

                                //ServerInfoData(String proxyServerName, int joinMinProtocolVer, int joinMaxProtocolVer, String verText, String serverName, int serverID, String serverBio, int serverMaxPlayers, HashMap<UUID, String> playerList)
                                ServerInfoData data = new ServerInfoData(
                                        hostName,
                                        serverConfig.string("ProxyName"),
                                        serverConfig.integer("MinProtocolVer"),
                                        serverConfig.integer("MaxProtocolVer"),
                                        serverConfig.string("VersionText"),
                                        serverConfig.string("ServerGroup"),
                                        serverConfig.integer("ServerID"),
                                        serverConfig.string("ServerText"),
                                        serverConfig.integer("ServerMaxPlayers"),
                                        new HashMap<>()
                                );

                                if (!hostName.equals("")){
                                    ServerList.put(hostName, data);
                                }

                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }

                    // プレーヤーのリストを構築
                    tempPlayerList.clear();
                    for (Player player : players){
                        if (player.getCurrentServer().isPresent()){
                            tempPlayerList.put(player.getCurrentServer().get().getServerInfo().getName(), player.getUniqueId());

                            ServerInfoData data = ServerList.get(player.getVirtualHost().get().getHostName());
                            if (data != null){
                                // 設定ファイルで指定されている接続先のサーバー名と一致しなければ追加しない
                                if (data.getProxyServerName().equals(player.getCurrentServer().get().getServerInfo().getName())){
                                    HashMap<UUID, String> list = new HashMap<>(data.getPlayerList());
                                    list.put(player.getUniqueId(), player.getUsername());
                                    data.setPlayerList(list);
                                }
                            }
                        }
                    }

                    // 同期開始
                    ServerList.forEach((HostName, ServerInfoData) -> {
                        ServerData data = new ServerData(ServerInfoData.getProxyServerName(), ServerInfoData.getServerName(), ServerInfoData.getServerID(), ServerInfoData.getPlayerList());

                        if (isProxyMode){
                            api.setServer(data);
                        } else {
                            // json化
                            String json = new Gson().toJson(data);

                            // とりあえずちょっとでもいいから通信量減らすためにgzip圧縮
                            ByteArrayOutputStream out = new ByteArrayOutputStream();
                            GZIPOutputStream gzip;
                            try {
                                gzip = new GZIPOutputStream(out);
                                gzip.write(json.getBytes(StandardCharsets.UTF_8));
                                gzip.close();
                            } catch ( Exception e) {
                                e.printStackTrace();
                            }

                            // TCP通信
                            try {
                                Socket socket = new Socket(finalConfigYaml.string("ServerIP"), finalConfigYaml.integer("ServerPort"));

                                OutputStream outputStream = socket.getOutputStream();
                                InputStream inputStream = socket.getInputStream();

                                outputStream.write(out.toByteArray());
                                outputStream.flush();
                                outputStream.close();

                                // 送られてきたリストをぶっこむ
                                if (inputStream.readAllBytes().length == 0){
                                    socket.close();
                                    return;
                                }

                                ByteArrayInputStream in = new ByteArrayInputStream(inputStream.readAllBytes());
                                ByteArrayOutputStream out1 = new ByteArrayOutputStream();
                                try {
                                    GZIPInputStream ungzip = new GZIPInputStream(in);
                                    byte[] buffer = new byte[256];
                                    int n;
                                    while ((n = ungzip.read(buffer)) >= 0) {
                                        out1.write(buffer, 0, n);
                                    }
                                } catch (Exception e){
                                    e.printStackTrace();
                                }

                                String ReceiveJson = out1.toString(StandardCharsets.UTF_8);
                                out1.close();
                                in.close();

                                List<ServerData> dataList = new Gson().fromJson(ReceiveJson, new TypeToken<List<ServerData>>() {}.getType());

                                for (ServerData t : dataList){
                                    api.setServer(t);
                                }
                                socket.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                    });

                }).start();
            }
        };

        timer.scheduleAtFixedRate(task, 0L, 1000L);

    }

    @Subscribe
    public void ProxyPingEvent(ProxyPingEvent e){
        String hostName = e.getConnection().getVirtualHost().get().getHostName();
        ServerPing ping = e.getPing();
        ServerPing.Builder builder = ping.asBuilder();

        ServerInfoData data = ServerList.get(hostName);

        if (data == null){
            return;
        }

        int PlayerCount = 0;
        HashMap<UUID, String> nameList = new HashMap<>();
        List<ServerData> list = api.getServerList(data.getServerName());

        for (ServerData data1 : list){
            PlayerCount = PlayerCount + data1.getPlayerList().size();
            nameList.putAll(data1.getPlayerList());
        }

        // オンライン人数
        builder.onlinePlayers(PlayerCount);

        List<ServerPing.SamplePlayer> players = new ArrayList<>();
        data.getPlayerList().forEach((uuid, name) -> {
            ServerPing.SamplePlayer player = new ServerPing.SamplePlayer(name, uuid);
            players.add(player);
        });
        builder.samplePlayers(players.toArray(ServerPing.SamplePlayer[]::new));
        builder.maximumPlayers(data.getServerMaxPlayers());


        if (data.getVerText() != null){
            int minProtocolVer = data.getJoinMinProtocolVer();
            int maxProtocolVer = data.getJoinMaxProtocolVer();
            String verText = data.getVerText();

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

        File iconFile = new File("./plugins/" + plugin.get().getDescription().getName().get() + "/" + hostName + ".png");

        if (iconFile.exists()){
            //System.out.println("!!");
            String contentType = null;
            try {
                contentType = Files.probeContentType(iconFile.toPath());

                StringBuilder sb = new StringBuilder();
                sb.append("data:");
                sb.append(contentType);
                sb.append(";base64,");
                sb.append(Base64.getEncoder().encodeToString(Files.readAllBytes(iconFile.toPath())));

                Favicon favicon = new Favicon(sb.toString());
                //System.out.println(sb.toString());
                builder.favicon(favicon);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        if (data.getServerBio() != null){
            builder.description(Component.text(data.getServerBio()));
        } else {
            builder.description(ping.getDescriptionComponent());
        }

        e.setPing(builder.build());

    }
}