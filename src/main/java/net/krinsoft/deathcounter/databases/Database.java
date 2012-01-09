package net.krinsoft.deathcounter.databases;

import net.krinsoft.deathcounter.Killer;

/**
 * @author krinsdeath
 */
public interface Database {

    // updates the database for the specified player
    public void update(Killer player);

    // fetches the specified player's database information
    public Killer fetch(String player);

    public void save();
    
}
