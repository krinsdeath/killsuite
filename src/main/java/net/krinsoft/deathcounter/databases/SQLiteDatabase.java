package net.krinsoft.deathcounter.databases;

import net.krinsoft.deathcounter.DeathCounter;
import net.krinsoft.deathcounter.Killer;
import net.krinsoft.deathcounter.Monster;
import org.bukkit.entity.Player;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

/**
 * @author krinsdeath
 */
@SuppressWarnings("unused")
public class SQLiteDatabase implements Database {
    private DeathCounter plugin;
    private String connectionURL;

    public SQLiteDatabase(DeathCounter plugin) {
        this.plugin = plugin;
        this.connectionURL = "jdbc:sqlite:plugins/DeathCounter/users.db";
        try {
            Class.forName("org.sqlite.JDBC");
            Connection conn = DriverManager.getConnection(connectionURL);
            Statement state = conn.createStatement();
            loadDatabase(state);
            loadKillers(state);
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
                mons += "`" + m.getName() + "`=" + player.get(m.getName()) + ", ";
            }
            mons = mons.substring(0, mons.length() - 2);
            String query = "UPDATE `killers` SET " + mons + " WHERE `name`='" + player.getName() + "';";
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
                query = "SELECT `id` FROM `killers` ORDER BY `id` ASC LIMIT 1";
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
            Connection conn = DriverManager.getConnection(connectionURL);
            conn.setAutoCommit(false);
            String mons = "";
            for (Monster m : Monster.values()) {
                mons += "`" + m.getName() + "`=?, ";
            }
            mons = mons.substring(0, mons.length() - 2);
            String query = "UPDATE `killers` SET " + mons + " WHERE `name`=?";
            PreparedStatement state = conn.prepareStatement(query);
            for (Player p : plugin.getServer().getOnlinePlayers()) {
                Killer k = plugin.getManager().getKiller(p.getName());
                if (k == null) {
                    plugin.debug("Attempted to save a null player; aborting...");
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
        String query = "CREATE TABLE IF NOT EXISTS `killers` " +
                "(id INTEGER PRIMARY KEY AUTOINCREMENT," +
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

}