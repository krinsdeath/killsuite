package net.krinsoft.killsuite.databases;

import net.krinsoft.killsuite.Killer;

import java.util.LinkedHashMap;

/**
 * @author krinsdeath
 */
public interface Database {

    // updates the database for the specified player
    public void update(Killer player);

    // fetches the specified player's database information
    public Killer fetch(String player);

    /**
     * Fetches and creates a sorted leaders list for kills of the specified monster
     * note: this method is definitely not efficient, and alternative sorting methods should be sought
     * @param monster The monster's database name
     * @return A sorted leaderboard
     */
    public LinkedHashMap<String, Integer> fetchAll(String monster);

    public void save();
    
}
