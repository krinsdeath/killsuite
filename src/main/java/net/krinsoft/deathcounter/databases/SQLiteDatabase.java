package net.krinsoft.deathcounter.databases;

import net.krinsoft.deathcounter.DeathCounter;
import net.krinsoft.deathcounter.Killer;
import org.bukkit.entity.Player;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

/**
 * @author krinsdeath
 */
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
            Connection conn = DriverManager.getConnection(connectionURL);
            Statement state = conn.createStatement();
            String query = "UPDATE `killers` SET " +
                    "`chicken`=" + player.get("chicken") + "," +
                    "`cow`=" + player.get("cow") + "," +
                    "`pig`=" + player.get("pig") + "," +
                    "`sheep`=" + player.get("sheep") + "," +
                    "`squid`=" + player.get("squid") + "," +
                    "`wolf`=" + player.get("wolf") + "," +
                    "`cavespider`=" + player.get("cavespider") + "," +
                    "`creeper`=" + player.get("creeper") + "," +
                    "`enderdragon`=" + player.get("enderdragon") + "," +
                    "`enderman`=" + player.get("enderman") + "," +
                    "`ghast`=" + player.get("ghast") + "," +
                    "`giant`=" + player.get("giant") + "," +
                    "`pigzombie`=" + player.get("pigzombie") + "," +
                    "`silverfish`=" + player.get("silverfish") + "," +
                    "`skeleton`=" + player.get("skeleton") + "," +
                    "`slime`=" + player.get("slime") + "," +
                    "`spider`=" + player.get("spider") + "," +
                    "`zombie`=" + player.get("zombie") + "," +
                    "`blaze`=" + player.get("blaze") + "," +
                    "`lavaslime`=" + player.get("lavaslime") + "," +
                    "`mushroomcow`=" + player.get("mushroomcow") + "," +
                    "`snowman`=" + player.get("snowman") + "," +
                    "`villager`=" + player.get("villager") + "," +
                    "`player`=" + player.get("player") + " " +
                    "WHERE `name`='" + player.getName() + "';";
            state.execute(query);
            state.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Killer fetch(String player) {
        Killer killer = null;
        try {
            Connection conn = DriverManager.getConnection(connectionURL);
            Statement state = conn.createStatement();
            String query = "SELECT * FROM `killers` WHERE `name`='" + player + "';";
            ResultSet result = state.executeQuery(query);
            if (result.next()) {
                Map<String, Integer> kills = new HashMap<String, Integer>();
                kills.put("chicken", result.getInt("chicken"));
                kills.put("cow", result.getInt("cow"));
                kills.put("pig", result.getInt("pig"));
                kills.put("sheep", result.getInt("sheep"));
                kills.put("squid", result.getInt("squid"));
                kills.put("wolf", result.getInt("wolf"));
                kills.put("cavespider", result.getInt("cavespider"));
                kills.put("creeper", result.getInt("creeper"));
                kills.put("enderdragon", result.getInt("enderdragon"));
                kills.put("enderman", result.getInt("enderman"));
                kills.put("ghast", result.getInt("ghast"));
                kills.put("giant", result.getInt("giant"));
                kills.put("pigzombie", result.getInt("pigzombie"));
                kills.put("silverfish", result.getInt("silverfish"));
                kills.put("skeleton", result.getInt("skeleton"));
                kills.put("slime", result.getInt("slime"));
                kills.put("spider", result.getInt("spider"));
                kills.put("zombie", result.getInt("zombie"));
                kills.put("blaze", result.getInt("blaze"));
                kills.put("lavaslime", result.getInt("lavaslime"));
                kills.put("mushroomcow", result.getInt("mushroomcow"));
                kills.put("snowman", result.getInt("snowman"));
                kills.put("villager", result.getInt("villager"));
                kills.put("player", result.getInt("player"));
                killer = new Killer(result.getInt("id"), result.getString("name"), kills);
            }
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
            String query = "UPDATE `killers` SET " +
                    "`chicken`=?," +
                    "`cow`=?," +
                    "`pig`=?," +
                    "`sheep`=?," +
                    "`squid`=?," +
                    "`wolf`=?," +
                    "`cavespider`=?," +
                    "`creeper`=?," +
                    "`enderdragon`=?," +
                    "`enderman`=?," +
                    "`ghast`=?," +
                    "`giant`=?," +
                    "`pigzombie`=?," +
                    "`silverfish`=?," +
                    "`skeleton`=?," +
                    "`slime`=?," +
                    "`spider`=?," +
                    "`zombie`=?," +
                    "`blaze`=?," +
                    "`lavaslime`=?," +
                    "`mushroomcow`=?," +
                    "`snowman`=?," +
                    "`villager`=?," +
                    "`player`=?," +
                    "WHERE `name`='?';";
            PreparedStatement state = conn.prepareStatement(query);
            for (Player p : plugin.getServer().getOnlinePlayers()) {
                Killer k = plugin.getManager().getKiller(p.getName());
                state.setInt(1, k.get("chicken"));
                state.setInt(2, k.get("cow"));
                state.setInt(3, k.get("pig"));
                state.setInt(4, k.get("sheep"));
                state.setInt(5, k.get("squid"));
                state.setInt(6, k.get("wolf"));
                state.setInt(7, k.get("cavespider"));
                state.setInt(8, k.get("creeper"));
                state.setInt(9, k.get("enderdragon"));
                state.setInt(10, k.get("enderman"));
                state.setInt(11, k.get("ghast"));
                state.setInt(12, k.get("giant"));
                state.setInt(13, k.get("pigzombie"));
                state.setInt(14, k.get("silverfish"));
                state.setInt(15, k.get("skeleton"));
                state.setInt(16, k.get("slime"));
                state.setInt(17, k.get("spider"));
                state.setInt(18, k.get("zombie"));
                state.setInt(19, k.get("blaze"));
                state.setInt(20, k.get("lavaslime"));
                state.setInt(21, k.get("mushroomcow"));
                state.setInt(22, k.get("snowman"));
                state.setInt(23, k.get("villager"));
                state.setInt(24, k.get("player"));
                state.setString(25, k.getName());
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
        plugin.debug("Fetching database...");
        String query = "CREATE TABLE IF NOT EXISTS `killers` " +
                "(id INTEGER PRIMARY KEY," +
                "name VARCHAR(32) UNIQUE KEY," +
                "cow INTEGER," +
                "chicken INTEGER," +
                "pig INTEGER," +
                "sheep INTEGER," +
                "squid INTEGER," +
                "wolf INTEGER," +
                "cavespider INTEGER," +
                "creeper INTEGER," +
                "enderdragon INTEGER," +
                "enderman INTEGER," +
                "ghast INTEGER," +
                "giant INTEGER," +
                "pigzombie INTEGER," +
                "silverfish INTEGER," +
                "skeleton INTEGER," +
                "slime INTEGER," +
                "spider INTEGER," +
                "zombie INTEGER," +
                "blaze INTEGER," +
                "lavaslime INTEGER," +
                "mushroomcow INTEGER," +
                "snowman INTEGER," +
                "villager INTEGER," +
                "player INTEGER);";
        state.execute(query);
    }

    private void loadKillers(Statement state) throws SQLException {
        plugin.debug("Fetching players...");
        for (Player p : plugin.getServer().getOnlinePlayers()) {
            plugin.getManager().register(fetch(p.getName()));
        }
    }}
