package net.krinsoft.deathcounter.databases;

import net.krinsoft.deathcounter.DeathCounter;
import net.krinsoft.deathcounter.Killer;
import net.krinsoft.deathcounter.Monster;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

/**
 * @author krinsdeath
 */
@SuppressWarnings("unused")
public class MySQLDatabase implements Database {
    private DeathCounter plugin;
    private String connectionURL;
    private String username;
    private String password;
    private Vector<Connection> connections = new Vector<Connection>();
    
    public MySQLDatabase(DeathCounter plugin) {
        this.plugin = plugin;
        ConfigurationSection conf = plugin.getConfig().getConfigurationSection("database");
        this.connectionURL = "jdbc:mysql://" +
                conf.getString("hostname") + ":" + conf.getString("port") +
                "/" + conf.getString("database");
        this.username = conf.getString("user");
        this.password = conf.getString("password");
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection conn = getConnection();
            Statement state = conn.createStatement();
            plugin.log("Connection to MySQL Established Successfully.");
            loadDatabase(state);
            loadKillers(state);
            state.close();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Killer player) {
        try {
            if (player == null) {
                plugin.debug("Attempted to save a null player; aborting...");
                return;
            }
            Connection conn = getConnection();
            Statement state = conn.createStatement();
            String mons = "";
            String vals = "";
            for (Monster m : Monster.values()) {
                mons += "`" + m.getName() + "`, ";
                vals += player.get(m.getName()) + ", ";
            }
            mons = mons.substring(0, mons.length() - 2);
            vals = vals.substring(0, vals.length() - 2);
            String query = "REPLACE INTO `killers` (`name`, " + mons + ") VALUES('" + player.getName() + "', " + vals + ");";
            state.execute(query);
            state.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Killer fetch(String player) {
        player = player.replaceAll("(?i)([;'\"\\\\/]|LIKE|WHERE|SELECT|DROP)", "");
        Killer killer = null;
        try {
            Connection conn = getConnection();
            Statement state = conn.createStatement();
            String query = "SELECT * FROM `killers` WHERE `name`='" + player + "';";
            ResultSet result = state.executeQuery(query);
            Map<String, Integer> kills = new HashMap<String, Integer>();
            int id = 1;
            if (result.next()) {
                for (Monster m : Monster.values()) {
                    kills.put(m.getName(), result.getInt(m.getName()));
                }
                id = result.getInt("id");
                plugin.debug("Loading player id '" + id + "'...");
            } else {
                query = "SELECT `id` FROM `killers` ORDER BY `id` ASC LIMIT 1;";
                result = state.executeQuery(query);
                if (result.next()) {
                    id = result.getInt("id")+1;
                }
                for (Monster m : Monster.values()) {
                    kills.put(m.getName(), 0);
                }
                plugin.debug("New player! Creating default entry with id '" + id + "'");
            }
            killer = new Killer(plugin, id, player, kills);
            state.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return killer;
    }

    @Override
    public void save() {
        try {
            Connection conn = getConnection();
            conn.setAutoCommit(false);
            String mons = "";
            String vals = "";
            for (Monster m : Monster.values()) {
                mons += "`" + m.getName() + "`, ";
                vals += "?, ";
            }
            mons = mons.substring(0, mons.length() - 2);
            vals = vals.substring(0, vals.length() - 2);
            String query = "REPLACE INTO `killers` (`name`, " + mons + ") VALUES(?, " + vals + ");";
            PreparedStatement state = conn.prepareStatement(query);
            for (Player p : plugin.getServer().getOnlinePlayers()) {
                Killer k = plugin.getManager().getKiller(p.getName());
                if (k == null) {
                    plugin.debug("Attempted to save a null player; aborting...");
                    continue;
                }
                state.setString(1, k.getName());
                int i = 2;
                for (Monster m : Monster.values()) {
                    state.setInt(i, k.get(m.getName()));
                    i++;
                }
                state.executeUpdate();
            }
            conn.commit();
            conn.setAutoCommit(true);
            state.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private void loadDatabase(Statement state) throws SQLException {
        String mons = "";
        for (Monster m : Monster.values()) {
            mons += m.getName() + " INTEGER, ";
        }
        mons = mons.substring(0, mons.length() - 2);
        String query = "CREATE TABLE IF NOT EXISTS `killers` " +
                "(id INTEGER PRIMARY KEY AUTO_INCREMENT," +
                "name VARCHAR(32) UNIQUE," +
                mons +
                ");";
        plugin.log("Building database...");
        state.execute(query);
        plugin.log("Updating database to latest schema...");
        for (Monster m : Monster.values()) {
            query = "ALTER TABLE `killers` ADD `" + m.getName() + "` INTEGER;";
            try {
                state.execute(query);
            } catch (SQLException e) {
                plugin.debug(e.getLocalizedMessage());
            }
        }
        plugin.log("... done!");
        query = "SELECT COUNT(*) AS `total_killers` FROM `killers`;";
        ResultSet res = state.executeQuery(query);
        if (res.next()) {
            plugin.log("Total entries: " + res.getInt("total_killers"));
        }
    }
    
    private void loadKillers(Statement state) throws SQLException {
        plugin.debug("Fetching players...");
        for (Player p : plugin.getServer().getOnlinePlayers()) {
            plugin.getManager().register(fetch(p.getName()));
        }
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(connectionURL, username, password);
    }

}
