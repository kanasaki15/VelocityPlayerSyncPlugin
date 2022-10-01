package xyz.n7mn.dev.nanamiproxyplugin;

import com.amihaiemil.eoyaml.Yaml;
import com.amihaiemil.eoyaml.YamlMapping;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.slf4j.Logger;
import xyz.n7mn.dev.nanamiproxyplugin.api.ServerInfo;
import xyz.n7mn.dev.nanamiproxyplugin.data.ServerData;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class SyncServer extends Thread {

    private final File config;
    private final ServerInfo ServerInfoAPI;
    private final Logger logger;

    public SyncServer(File file, ServerInfo api, Logger logger) {
        this.config = file;
        this.ServerInfoAPI = api;
        this.logger = logger;
    }

    @Override
    public void run() {
        try {
            if (config.exists()){
                YamlMapping ConfigYaml = null;
                try {
                    ConfigYaml = Yaml.createYamlInput(config).readYamlMapping();
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }

                if (ConfigYaml.string("ProxyMode").toLowerCase().equals("false")){
                    return;
                }

                int proxyPort = ConfigYaml.integer("ProxyPort");
                ServerSocket ServerSocket = new ServerSocket(proxyPort);

                logger.info("サーバー機能をTCP ポート "+proxyPort+"で起動しました。");

                while (true) {
                    Socket socket = ServerSocket.accept();
                    InputStream inputStream = socket.getInputStream();
                    if (inputStream != null){
                        // 受信データ ---> Json
                        ByteArrayInputStream in = new ByteArrayInputStream(inputStream.readAllBytes());
                        OutputStream out = socket.getOutputStream();

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

                        // jsonを格納
                        String jsonText = out1.toString(StandardCharsets.UTF_8);
                        ServerData json = null;
                        if (jsonText != null){
                            json = new Gson().fromJson(jsonText, ServerData.class);
                            ServerInfoAPI.setServer(json);
                        }

                        out1.close();

                        if (json == null){
                            return;
                        }

                        // 送られてきたjsonを反映させた分のリストをjson化、gzipして送り返す
                        String toJson = new GsonBuilder().serializeNulls().setPrettyPrinting().create().toJson(ServerInfoAPI.getServerList(json.getServerName()));

                        ByteArrayOutputStream out2 = new ByteArrayOutputStream();
                        GZIPOutputStream gzip;
                        try {
                            gzip = new GZIPOutputStream(out2);
                            gzip.write(toJson.getBytes(StandardCharsets.UTF_8));
                            gzip.close();
                        } catch ( Exception e) {
                            e.printStackTrace();
                        }
                        byte[] sendBytes = out2.toByteArray();
                        out2.close();

                        out.write(sendBytes);
                        out.flush();
                        out.close();

                        inputStream.close();
                    }
                    socket.close();
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
