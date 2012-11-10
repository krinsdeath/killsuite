package net.krinsoft.killsuite;

import java.util.Map;

/**
 * @author krinsdeath
 */
public class Killer {
    private final KillSuite plugin;
    private final int ID;
    private final String name;
    //private final Map<Monster, Integer> kills = new HashMap<Monster, Integer>(26);

    private final int[] killed = new int[32];

    public Killer(KillSuite inst, String name, Map<Monster, Integer> kills) {
        this.plugin = inst;
        this.ID = name.hashCode();
        this.name = name;
        for (Monster m : kills.keySet()) {
            killed[m.ordinal()] = kills.get(m);
        }
    }
    
    public String getName() {
        return this.name;
    }
    
    public int get(String field) {
        Monster m = Monster.getType(field);
        if (m == null) { return -1; }
        return killed[m.ordinal()];
    }

    public int get(Monster field) {
        return killed[field.ordinal()];
    }
    
    public int update(String field) {
        Monster m = Monster.getType(field);
        if (m == null) { return -1; }
        killed[m.ordinal()] += 1;
        return killed[m.ordinal()];
    }

    public long total() {
        long total = 0;
        for (Integer k : killed) {
            total += k;
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
        if (o == null) { return false; }
        if (this == o) { return true; }
        if (this.getClass() != o.getClass()) { return false; }
        Killer killer = (Killer) o;
        return killer.hashCode() == this.hashCode();
    }
}
