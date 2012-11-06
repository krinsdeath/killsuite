package net.krinsoft.killsuite;

import net.krinsoft.killsuite.databases.Database;
import net.krinsoft.killsuite.databases.MySQLDatabase;
import net.krinsoft.killsuite.databases.SQLiteDatabase;
import net.krinsoft.killsuite.databases.YamlDatabase;
import net.krinsoft.killsuite.util.RewardGenerator;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author krinsdeath
 */
public class Manager {
    private final KillSuite plugin;
    private final Map<String, Killer> killers = new HashMap<String, Killer>();
    private final Map<String, RewardGenerator> rewards = new HashMap<String, RewardGenerator>();

    private Database database;
    
    public Manager(KillSuite plugin) {
        this.plugin = plugin;
        getDatabase();
        for (Player p : plugin.getServer().getOnlinePlayers()) {
            register(p.getName());
        }
    }

    public void disable() {
        plugin.debug("Unregistering users...");
        for (Killer killer : new HashSet<Killer>(killers.values())) {
            killers.remove(killer.getName());
        }
    }

    private void getDatabase() {
        String dbType = plugin.getConfig().getString("database.type", "YAML");
        if (dbType.equalsIgnoreCase("YAML")) {
            database = new YamlDatabase(plugin);
        } else if (dbType.equalsIgnoreCase("SQLite")) {
            database = new SQLiteDatabase(plugin);
        } else if (dbType.equalsIgnoreCase("MySQL")) {
            database = new MySQLDatabase(plugin);
        }
        // make sure nothing went wrong with the database
        if (database == null) {
            database = new YamlDatabase(plugin);
        }
    }

    public void save() {
        plugin.debug("Saving database...");
        database.save();
        for (String player : new HashSet<String>(killers.keySet())) {
            if (plugin.getServer().getPlayer(player) == null) {
                killers.remove(player);
            }
        }
    }

    /**
     * Registers the specifed killer to the list of players.
     * @param killer The killer object of the player we're registering
     */
    public void register(Killer killer) {
        if (killer == null) {
            plugin.getLogger().warning("null killer; failed to register player.");
            return;
        }
        killers.put(killer.getName(), killer);
        plugin.debug(killer.getName() + " was registered.");
    }

    /**
     * Registers the specified player by their name
     * @param player The name of the player being registered
     * @see #register(Killer)
     */
    public void register(String player) {
        register(getKiller(player));
    }

    /**
     * Attempts to obtain a Killer object from the specified player name, or grabs it from the database
     * @param player The player's name
     * @return The killer object associated with this player.
     */
    public Killer getKiller(String player) {
        Killer killer = killers.get(player);
        if (killer == null) {
            killer = database.fetch(player);
        }
        return killer;
    }

    public Set<Killer> getKillers() {
        return new HashSet<Killer>(killers.values());
    }

    public void addReward(String entry, RewardGenerator reward) {
        rewards.put(entry, reward);
    }

    public double getReward(String entry) {
        RewardGenerator r = rewards.get(entry);
        if (r != null) {
            return r.generateRandom();
        }
        return 0;
    }
    
}
