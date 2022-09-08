package xyz.n7mn.dev.nanamiproxyplugin;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;

public class SyncServer extends Thread {

    private final File config;

    public SyncServer(File file) {
        this.config = file;
    }

    @Override
    public void run() {
        try {
            if (!config.exists()){
                ServerSocket svSock = new ServerSocket(30001);

                System.out.println("サーバー機能をTCP ポートで起動しました。");
                while (true) {
                    svSock.accept();
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
