package net.krinsoft.killsuite.databases;

import net.krinsoft.killsuite.KillSuite;
import net.krinsoft.killsuite.Killer;
import net.krinsoft.killsuite.Monster;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
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
        Map<Monster, Integer> kills = new HashMap<Monster, Integer>();
        ConfigurationSection users = this.users.getConfigurationSection(player);
        if (users == null) {
            makeNode(player);
            users = this.users.getConfigurationSection(player);
        }
        for (Monster m : Monster.values()) {
            kills.put(m, users.getInt(m.getName(), 0));
        }
        return new Killer(player, kills);
    }

    @Override
    public LinkedHashMap<String, Integer> fetchAll(String monster) {
        LinkedHashMap<String, Integer> leaders = new LinkedHashMap<String, Integer>();
        LinkedList<Map.Entry<String, Integer>> monsters = new LinkedList<Map.Entry<String, Integer>>();
        Monster m = Monster.getType(monster);
        if (m == null) { return null; }
        for (String key : this.users.getKeys(false)) {
            leaders.put(key, this.users.getInt(key + "." + m.getName()));
        }
        monsters.addAll(leaders.entrySet());
        Collections.sort(monsters, new Comparator<Map.Entry<String, Integer>>() {
            @Override
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                return o2.getValue().compareTo(o1.getValue());
            }
        });
        leaders.clear();
        for (Map.Entry<String, Integer> entry : monsters) {
            leaders.put(entry.getKey(), entry.getValue());
        }
        return leaders;
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
