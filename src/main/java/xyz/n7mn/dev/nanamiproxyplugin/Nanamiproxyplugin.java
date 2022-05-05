package xyz.n7mn.dev.nanamiproxyplugin;

import com.amihaiemil.eoyaml.Yaml;
import com.amihaiemil.eoyaml.YamlMapping;
import com.amihaiemil.eoyaml.YamlMappingBuilder;
import com.google.inject.Inject;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyPingEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.PluginContainer;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.ServerPing;
import com.velocitypowered.api.util.Favicon;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
import org.slf4j.Logger;

import java.io.*;
import java.nio.file.Files;
import java.util.Base64;
import java.util.Optional;

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

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {

        plugin = proxyServer.getPluginManager().getPlugin("nanamiproxyplugin");

        File file1 = new File("./plugins/" + plugin.get().getDescription().getName().get());
        File file2 = new File("./plugins/" + plugin.get().getDescription().getName().get() + "/config.yml");
        File file3 = new File("./plugins/" + plugin.get().getDescription().getName().get() + "/server.yml");
        if (!file1.exists()){
            file1.mkdir();
        }

        if (!file3.exists()){
            YamlMappingBuilder builder = Yaml.createYamlMappingBuilder();
            YamlMapping mapping = builder.add(
                    "7mi.xyz", "[1.8-1.18.2] ななみ鯖"
            ).build();

            String yml = mapping.toString();

            try {
                PrintWriter writer = new PrintWriter(file3);
                writer.print(yml);
                writer.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        }

    }

    @Subscribe
    public void ProxyPingEvent(ProxyPingEvent e){
        String hostName = e.getConnection().getVirtualHost().get().getHostName();

        try {
            YamlMapping mapping = Yaml.createYamlInput(new File("./plugins/" + plugin.get().getDescription().getName().get() + "/server.yml")).readYamlMapping();
            // System.out.println("---- debug ----\n"+mapping.toString()+"\n---- debug ----");

            String desc = mapping.string(hostName);

            ServerPing ping = e.getPing();
            ServerPing.Builder builder = ping.asBuilder();

            if (desc == null){
                return;
            }

            Component text = Component.text(desc);

            if (desc.length() == 0){
                text = ping.getDescriptionComponent();
            }

            //String url = ping.getFavicon().get().getBase64Url();
            //System.out.println("icon : "+url);

            File file = new File("./plugins/" + plugin.get().getDescription().getName().get() + "/" + hostName + ".png");
            //System.out.println(file.toPath());

            if (file.exists()){
                //System.out.println("!!");
                String contentType = Files.probeContentType(file.toPath());

                StringBuilder sb = new StringBuilder();
                sb.append("data:");
                sb.append(contentType);
                sb.append(";base64,");
                sb.append(Base64.getEncoder().encodeToString(Files.readAllBytes(file.toPath())));

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