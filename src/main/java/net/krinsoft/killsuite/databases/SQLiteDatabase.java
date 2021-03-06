package net.krinsoft.killsuite.databases;

import net.krinsoft.killsuite.KillSuite;
import net.krinsoft.killsuite.Killer;
import net.krinsoft.killsuite.Monster;
import org.bukkit.entity.Player;

import java.sql.*;
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
public class SQLiteDatabase implements Database {
    private final KillSuite plugin;
    private final String connectionURL;

    public SQLiteDatabase(KillSuite plugin) {
        this.plugin = plugin;
        this.connectionURL = "jdbc:sqlite:plugins/KillSuite/users.db";
        try {
            Class.forName("org.sqlite.JDBC");
            Connection conn = DriverManager.getConnection(connectionURL);
            Statement state = conn.createStatement();
            loadDatabase(state);
            state.close();
            conn.close();
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
            Connection conn = DriverManager.getConnection(connectionURL);
            Statement state = conn.createStatement();
            String mons = "";
            for (Monster m : Monster.values()) {
                mons += m.getName() + "=" + player.get(m.getName()) + ", ";
            }
            mons = mons.substring(0, mons.length() - 2);
            String query = "UPDATE killers SET " + mons + " WHERE name='" + player.getName() + "';";
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
            Connection conn = DriverManager.getConnection(connectionURL);
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
            Connection conn = DriverManager.getConnection(connectionURL);
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
            Connection conn = DriverManager.getConnection(connectionURL);
            conn.setAutoCommit(false);
            String mons = "";
            for (Monster m : Monster.values()) {
                mons += m.getName() + "=?, ";
            }
            mons = mons.substring(0, mons.length() - 2);
            String query = "UPDATE killers SET " + mons + " WHERE name=?";
            PreparedStatement state = conn.prepareStatement(query);
            for (Killer k : plugin.getManager().getKillers()) {
                if (k == null || k.total() == 0) {
                    plugin.debug("Attempted to save blank player; aborting...");
                    continue;
                }
                int i = 1;
                for (Monster m : Monster.values()) {
                    state.setInt(i, k.get(m.getName()));
                    i++;
                }
                state.setString(i, k.getName());
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
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
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
        plugin.log("... done!");
        query = "SELECT ROWID AS total FROM killers ;";
        ResultSet res = state.executeQuery(query);
        if (res.next()) {
            plugin.log("Total DB entries: " + res.getInt("total"));
        }
    }

    private void loadKillers() {
        plugin.debug("Fetching players...");
        for (Player p : plugin.getServer().getOnlinePlayers()) {
            plugin.getManager().register(fetch(p.getName()));
        }
    }

}