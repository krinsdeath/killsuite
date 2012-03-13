package net.krinsoft.deathcounter.databases;

import net.krinsoft.deathcounter.DeathCounter;
import net.krinsoft.deathcounter.Killer;
import net.krinsoft.deathcounter.Monster;
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
    private DeathCounter plugin;
    private FileConfiguration users;
    
    public YamlDatabase(DeathCounter plugin) {
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
        save();
    }

    @Override
    public Killer fetch(String player) {
        plugin.debug("Loading player '" + player + "'");
        Map<String, Integer> kills = new HashMap<String, Integer>();
        ConfigurationSection users = this.users.getConfigurationSection(player);
        if (users == null) {
            makeNode(player);
            users = this.users.getConfigurationSection(player);
        }
        for (Monster m : Monster.values()) {
            kills.put(m.getName(), users.getInt(m.getName(), 0));
        }
        return new Killer(users.getInt("id"), player, kills);
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
        plugin.log("Creating new profile for player '" + player + "'!");
        int ID = this.users.getInt("profiles", -1);
        ID++;
        users.set("profiles", ID);
        users.set(player + ".id", ID);
        for (Monster m : Monster.values()) {
            users.set(player + "." + m.getName(), 0);
        }
        save();
    }
}
