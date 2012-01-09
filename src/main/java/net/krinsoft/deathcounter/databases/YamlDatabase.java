package net.krinsoft.deathcounter.databases;

import net.krinsoft.deathcounter.DeathCounter;
import net.krinsoft.deathcounter.Killer;
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
        this.users.set(player.getName() + ".chicken", player.get("chicken"));
        this.users.set(player.getName() + ".cow", player.get("cow"));
        this.users.set(player.getName() + ".pig", player.get("pig"));
        this.users.set(player.getName() + ".sheep", player.get("sheep"));
        this.users.set(player.getName() + ".squid", player.get("squid"));
        this.users.set(player.getName() + ".wolf", player.get("wolf"));
        this.users.set(player.getName() + ".cavespider", player.get("cavespider"));
        this.users.set(player.getName() + ".creeper", player.get("creeper"));
        this.users.set(player.getName() + ".enderdragon", player.get("enderdragon"));
        this.users.set(player.getName() + ".enderman", player.get("enderman"));
        this.users.set(player.getName() + ".ghast", player.get("ghast"));
        this.users.set(player.getName() + ".giant", player.get("giant"));
        this.users.set(player.getName() + ".pigzombie", player.get("pigzombie"));
        this.users.set(player.getName() + ".silverfish", player.get("silverfish"));
        this.users.set(player.getName() + ".skeleton", player.get("skeleton"));
        this.users.set(player.getName() + ".slime", player.get("slime"));
        this.users.set(player.getName() + ".spider", player.get("spider"));
        this.users.set(player.getName() + ".zombie", player.get("zombie"));
        this.users.set(player.getName() + ".blaze", player.get("blaze"));
        this.users.set(player.getName() + ".lavaslime", player.get("lavaslime"));
        this.users.set(player.getName() + ".mushroomcow", player.get("mushroomcow"));
        this.users.set(player.getName() + ".snowman", player.get("snowman"));
        this.users.set(player.getName() + ".villager", player.get("villager"));
        this.users.set(player.getName() + ".player", player.get("player"));
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
        kills.put("chicken", users.getInt("chicken", 0));
        kills.put("cow", users.getInt("cow", 0));
        kills.put("pig", users.getInt("pig", 0));
        kills.put("sheep", users.getInt("sheep", 0));
        kills.put("squid", users.getInt("squid", 0));
        kills.put("wolf", users.getInt("wolf", 0));
        kills.put("cavespider", users.getInt("cavespider", 0));
        kills.put("creeper", users.getInt("creeper", 0));
        kills.put("enderdragon", users.getInt("enderdragon", 0));
        kills.put("enderman", users.getInt("enderman", 0));
        kills.put("ghast", users.getInt("ghast", 0));
        kills.put("giant", users.getInt("giant", 0));
        kills.put("pigzombie", users.getInt("pigzombie", 0));
        kills.put("silverfish", users.getInt("silverfish", 0));
        kills.put("skeleton", users.getInt("skeleton", 0));
        kills.put("slime", users.getInt("slime", 0));
        kills.put("spider", users.getInt("spider", 0));
        kills.put("zombie", users.getInt("zombie", 0));
        kills.put("blaze", users.getInt("blaze", 0));
        kills.put("lavaslime", users.getInt("lavaslime", 0));
        kills.put("mushroomcow", users.getInt("mushroomcow", 0));
        kills.put("snowman", users.getInt("snowman", 0));
        kills.put("villager", users.getInt("villager", 0));
        kills.put("player", users.getInt("player", 0));
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
        this.users.set("profiles", ID);
        this.users.set(player + ".id", ID);
        this.users.set(player + ".chicken", 0);
        this.users.set(player + ".cow", 0);
        this.users.set(player + ".pig", 0);
        this.users.set(player + ".sheep", 0);
        this.users.set(player + ".squid", 0);
        this.users.set(player + ".wolf", 0);
        this.users.set(player + ".cavespider", 0);
        this.users.set(player + ".creeper", 0);
        this.users.set(player + ".enderdragon", 0);
        this.users.set(player + ".enderman", 0);
        this.users.set(player + ".ghast", 0);
        this.users.set(player + ".giant", 0);
        this.users.set(player + ".pigzombie", 0);
        this.users.set(player + ".silverfish", 0);
        this.users.set(player + ".skeleton", 0);
        this.users.set(player + ".slime", 0);
        this.users.set(player + ".spider", 0);
        this.users.set(player + ".zombie", 0);
        this.users.set(player + ".blaze", 0);
        this.users.set(player + ".lavaslime", 0);
        this.users.set(player + ".mushroomcow", 0);
        this.users.set(player + ".snowman", 0);
        this.users.set(player + ".villager", 0);
        this.users.set(player + ".player", 0);
        save();
    }
}
