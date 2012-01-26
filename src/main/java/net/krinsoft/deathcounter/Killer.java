package net.krinsoft.deathcounter;

import java.util.HashMap;
import java.util.Map;

/**
 * @author krinsdeath
 */
public class Killer {
    private int ID;
    private String name;
    private Map<String, Integer> kills = new HashMap<String, Integer>();

    public Killer(int id, String name, Map<String, Integer> kills) {
        this.ID = id;
        this.name = name;
        this.kills.clear();
        this.kills.putAll(kills);
    }
    
    public String getName() {
        return this.name;
    }
    
    public int get(String field) {
        Monster m = Monster.getType(field);
        if (m == null) { return -1; }
        return kills.get(m.getName());
    }
    
    public int update(String field) {
        Monster m = Monster.getType(field);
        if (m == null) { return -1; }
        DeathCounter.instance.debug("Incrementing " + m.getFancyName() + " from " + kills.get(m.getName()) + " to " + (kills.get(m.getName()) + 1));
        kills.put(m.getName(), (kills.get(m.getName()) + 1));
        return kills.get(m.getName());
    }

    @Override
    public String toString() {
        return "Killer{name=" + this.name + "}@" + this.ID;
    }
    
    @Override
    public int hashCode() {
        int hash = 19;
        hash = hash * 15 + (this.toString().hashCode());
        hash = hash + this.ID;
        return hash;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (this.getClass() != o.getClass()) { return false; }
        Killer killer = (Killer) o;
        return killer.hashCode() == this.hashCode() || killer.toString().equals(this.toString());
    }
}
