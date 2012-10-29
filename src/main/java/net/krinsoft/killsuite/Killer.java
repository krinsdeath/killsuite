package net.krinsoft.killsuite;

import java.util.HashMap;
import java.util.Map;

/**
 * @author krinsdeath
 */
public class Killer {
    private final KillSuite plugin;
    private final int ID;
    private final String name;
    private final Map<String, Integer> kills = new HashMap<String, Integer>();

    public Killer(KillSuite inst, String name, Map<String, Integer> kills) {
        this.plugin = inst;
        this.ID = name.hashCode();
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
        long n = System.nanoTime();
        Monster m = Monster.getType(field);
        if (m == null) { return -1; }
        int k = kills.get(m.getName()) + 1;
        plugin.debug("Incrementing " + m.getFancyName() + " from " + (k - 1) + " to " + k);
        kills.put(m.getName(), k);
        n = System.nanoTime() - n;
        plugin.profile("killer.update", n);
        return k;
    }

    public long total() {
        long total = 0;
        for (Integer kills : this.kills.values()) {
            total += kills;
        }
        return total;
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
