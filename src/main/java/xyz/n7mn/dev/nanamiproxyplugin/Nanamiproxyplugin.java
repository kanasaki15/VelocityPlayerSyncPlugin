package xyz.n7mn.dev.nanamiproxyplugin;

import com.amihaiemil.eoyaml.Yaml;
import com.amihaiemil.eoyaml.YamlMapping;
import com.amihaiemil.eoyaml.YamlMappingBuilder;
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
import xyz.n7mn.dev.nanamiproxyplugin.data.ServerData;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;

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

    private List<ServerData> ServerList = new ArrayList<>();

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {

        plugin = proxyServer.getPluginManager().getPlugin("nanamiproxyplugin");

        File file1 = new File("./plugins/" + plugin.get().getDescription().getName().get());
        File file2 = new File("./plugins/" + plugin.get().getDescription().getName().get() + "/server-sample.7mi.xyz.yml");
        File file3 = new File("./plugins/" + plugin.get().getDescription().getName().get() + "/config.yml");
        if (!file1.exists()){
            file1.mkdir();
        }

        if (!file2.exists()){
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
                    "ProxyServerPort: 30000";

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

        // 定期タスク生成して同期
        Timer timer = new Timer();
        YamlMapping finalConfigYaml = ConfigYaml;
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                new Thread(()->{

                    // 同期する鯖のリストを構築する
                    //logger.info("同期するサーバーのリストを構築中...");
                    File[] files = new File("./plugins/" + plugin.get().getDescription().getName().get()).listFiles();

                    //logger.info("構築完了。同期対象サーバー数： " + list.size());

                    // 同期スタート

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
        Component text = ping.getDescriptionComponent();


    }
}