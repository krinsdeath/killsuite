package net.krinsoft.killsuite.databases;

import net.krinsoft.killsuite.KillSuite;
import net.krinsoft.killsuite.Killer;
import net.krinsoft.killsuite.Monster;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author krinsdeath
 */
public class YamlDatabase implements Database {
    private final KillSuite plugin;
    private final FileConfiguration users;
    
    public YamlDatabase(KillSuite plugin) {
        this.plugin = plugin;
        File file = new File(plugin.getDataFolder(), "users.yml"); 
        this.users = YamlConfiguration.loadConfiguration(file);
        this.users.setDefaults(YamlConfiguration.loadConfiguration(file));
        this.users.options().copyDefaults(true);
    }

    public void update(Killer player) {
        for (Monster m : Monster.values()) {
            users.set(player.getName() + "." + m.getName(), player.get(m.getName()));
        }
    }

    @Override
    public Killer fetch(String player) {
        plugin.debug("Loading player '" + player + "'...");
        Map<String, Integer> kills = new HashMap<String, Integer>();
        ConfigurationSection users = this.users.getConfigurationSection(player);
        if (users == null) {
            makeNode(player);
            users = this.users.getConfigurationSection(player);
        }
        for (Monster m : Monster.values()) {
            kills.put(m.getName(), users.getInt(m.getName(), 0));
        }
        return new Killer(plugin, player, kills);
    }
    
    @Override
    public void save() {
        try {
            users.save(new File(plugin.getDataFolder(), "users.yml"));
        } catch (IOException e) {
            plugin.debug("Error while saving file 'users.yml'");
            e.printStackTrace();
        }
    }
    
    private void makeNode(String player) {
        plugin.debug("New player: " + player + "! Creating default entry...");
        for (Monster m : Monster.values()) {
            users.set(player + "." + m.getName(), 0);
        }
        save();
    }
}
