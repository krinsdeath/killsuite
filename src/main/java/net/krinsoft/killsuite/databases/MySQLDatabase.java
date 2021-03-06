package net.krinsoft.killsuite.databases;

import net.krinsoft.killsuite.KillSuite;
import net.krinsoft.killsuite.Killer;
import net.krinsoft.killsuite.Monster;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
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
@SuppressWarnings("unused")
public class MySQLDatabase implements Database {
    private final KillSuite plugin;
    private final String connectionURL;
    private final String username;
    private final String password;

    public MySQLDatabase(KillSuite plugin) {
        this.plugin = plugin;
        ConfigurationSection conf = plugin.getConfig().getConfigurationSection("database");
        this.connectionURL = "jdbc:mysql://" +
                conf.getString("hostname", "localhost") + ":" + conf.getString("port", "3306") +
                "/" + conf.getString("database", "killsuite");
        this.username = conf.getString("user", "root");
        this.password = conf.getString("password", "root");
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection conn = getConnection();
            Statement state = conn.createStatement();
            plugin.log("Connection to MySQL Established Successfully.");
            loadDatabase(state);
            //loadKillers();
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
                mons += m.getName() + ", ";
                vals += player.get(m.getName()) + ", ";
            }
            mons = mons.substring(0, mons.length() - 2);
            vals = vals.substring(0, vals.length() - 2);
            String query = "REPLACE INTO killers (name, " + mons + ") VALUES('" + player.getName() + "', " + vals + ");";
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
            String query = "SELECT * FROM killers WHERE name='" + player + "';";
            ResultSet result = state.executeQuery(query);
            Map<Monster, Integer> kills = new HashMap<Monster, Integer>();
            if (result.next()) {
                for (Monster m : Monster.values()) {
                    kills.put(m, result.getInt(m.getName()));
                }
                plugin.debug("Loading player '" + player + "'...");
            } else {
                for (Monster m : Monster.values()) {
                    kills.put(m, 0);
                }
                plugin.debug("New player: " + player + "! Creating default entry...");
            }
            killer = new Killer(player, kills);
            state.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return killer;
    }

    @Override
    public LinkedHashMap<String, Integer> fetchAll(String monster) {
        LinkedHashMap<String, Integer> leaders = new LinkedHashMap<String, Integer>();
        LinkedList<Map.Entry<String, Integer>> monsters = new LinkedList<Map.Entry<String, Integer>>();
        Monster m = Monster.getType(monster);
        if (m == null) { return null; }
        try {
            Connection conn = getConnection();
            Statement state = conn.createStatement();
            ResultSet result = state.executeQuery("SELECT " + m.getName() + ",name FROM killers;");
            while (result.next()) {
                leaders.put(result.getString("name"), result.getInt(m.getName()));
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
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void save() {
        try {
            Connection conn = getConnection();
            conn.setAutoCommit(false);
            String mons = "";
            String vals = "";
            for (Monster m : Monster.values()) {
                mons += m.getName() + ", ";
                vals += "?, ";
            }
            mons = mons.substring(0, mons.length() - 2);
            vals = vals.substring(0, vals.length() - 2);
            String query = "REPLACE INTO killers (name, " + mons + ") VALUES(?, " + vals + ");";
            PreparedStatement state = conn.prepareStatement(query);
            for (Killer k : plugin.getManager().getKillers()) {
                if (k == null || k.total() == 0) {
                    plugin.debug("Attempted to save blank player; aborting...");
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
        String query = "CREATE TABLE IF NOT EXISTS killers " +
                "(" +
                "id INTEGER PRIMARY KEY AUTO_INCREMENT, " +
                "name VARCHAR(32) UNIQUE, " +
                mons +
                ");";
        plugin.debug("Building database...");
        state.execute(query);
        plugin.debug("Updating database to latest schema...");
        for (Monster m : Monster.values()) {
            query = "ALTER TABLE killers ADD " + m.getName() + " INTEGER;";
            try {
                state.execute(query);
            } catch (SQLException e) {
                plugin.debug(e.getLocalizedMessage());
            }
        }
        plugin.debug("... done!");
        query = "SELECT AUTO_INCREMENT AS total FROM information_schema.tables WHERE table_name = 'killers';";
        ResultSet res = state.executeQuery(query);
        if (res.next()) {
            plugin.log("Total KillSuite entries: " + res.getInt("total"));
        }
    }

    private void loadKillers() {
        plugin.debug("Fetching players...");
        for (Player p : plugin.getServer().getOnlinePlayers()) {
            plugin.getManager().register(fetch(p.getName()));
        }
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(connectionURL, username, password);
    }

}
