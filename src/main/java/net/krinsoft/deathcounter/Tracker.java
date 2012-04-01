package net.krinsoft.deathcounter;

import net.krinsoft.deathcounter.databases.Database;
import net.krinsoft.deathcounter.databases.MySQLDatabase;
import net.krinsoft.deathcounter.databases.SQLiteDatabase;
import net.krinsoft.deathcounter.databases.YamlDatabase;

/**
 * @author krinsdeath
 */
public class Tracker {
    private DeathCounter plugin;
    private Database db;
    
    public Tracker(DeathCounter plugin) {
        this.plugin = plugin;
        String dbType = plugin.getConfig().getString("database.type", "YAML");
        if (dbType.equalsIgnoreCase("YAML")) {
            db = new YamlDatabase(plugin);
        } else if (dbType.equalsIgnoreCase("SQLite")) {
            db = new SQLiteDatabase(plugin);
        } else if (dbType.equalsIgnoreCase("MySQL")) {
            db = new MySQLDatabase(plugin);
        }
        // make sure nothing went wrong with the db
        if (db == null) {
            db = new YamlDatabase(plugin);
        }
    }
    
    public void save() {
        plugin.debug("Saving database...");
        this.db.save();
    }
    
    public Killer fetch(String player) {
        if (plugin.getManager().getKiller(player) != null) {
            return plugin.getManager().getKiller(player);
        }
        return this.db.fetch(player);
    }
    
    public void update(String player) {
        Killer killer = plugin.getManager().getKiller(player);
        this.db.update(killer);
    }
}
