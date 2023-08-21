package de.tamion;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;

public final class AutoServerUpdater extends JavaPlugin {

    @Override
    public void onEnable() {
        File serverjar = new File(System.getProperty("java.class.path"));
        String[] buildversion = Bukkit.getVersion().split(" ");
        try {
            if (buildversion[0].contains("Paper")) {
                String build = buildversion[0].replaceAll("git-Paper-", "");
                String version = buildversion[2].replaceAll("\\)", "");
                String[] builds = new ObjectMapper().readTree(new URL("https://api.papermc.io/v2/projects/paper/versions/" + version)).get("builds").toString().replaceAll("\\[", "").replaceAll("]", "").split(",");
                String latestbuild = builds[builds.length - 1];
                if (!latestbuild.equals(build)) {
                    getLogger().warning("Old Paper build detected. Updating from build " + build + " to " + latestbuild);
                    FileUtils.copyURLToFile(new URL("https://api.papermc.io/v2/projects/paper/versions/" + version + "/builds/" + latestbuild + "/downloads/paper-" + version + "-" + latestbuild + ".jar"), serverjar);
                    getLogger().warning("Downloaded Paper build " + latestbuild + ". Restarting... If no restart script has been setup you will need to manually start the server");
                    Bukkit.getServer().spigot().restart();
                    return;
                }
            } else if (buildversion[0].contains("Purpur")) {
                String build = buildversion[0].replaceAll("git-Purpur-", "");
                String version = buildversion[2].replaceAll("\\)", "");
                String latestbuild = new ObjectMapper().readTree(new URL("https://api.purpurmc.org/v2/purpur/" + version + "/latest")).get("build").asText();
                if (!latestbuild.equals(build)) {
                    getLogger().warning("Old Purpur build detected. Updating from build " + build + " to " + latestbuild);
                    FileUtils.copyURLToFile(new URL("https://api.purpurmc.org/v2/purpur/" + version + "/" + latestbuild + "/download"), serverjar);
                    getLogger().warning("Downloaded Purpur build " + latestbuild + ". Restarting... If no restart script has been setup you will need to manually start the server");
                    Bukkit.getServer().spigot().restart();
                    return;
                }
            } else {
                getLogger().log(Level.SEVERE, "SERVER SOFTWARE NOT SUPPORTED BY AUTOSERVERUPDATER");
                Bukkit.getPluginManager().disablePlugin(this);
                return;
            }
            getLogger().info("Latest Build installed!");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
