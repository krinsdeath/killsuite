package net.krinsoft.killsuite;

/**
 * @author krinsdeath
 */
class Leader {

    private final String name;
    private final int kills;
    private final int rank;

    public Leader(String name, int kills, int rank) {
        this.name = name;
        this.kills = kills;
        this.rank = rank;
    }

    public String getName() {
        return this.name;
    }

    public int getKills() {
        return this.kills;
    }

    public int getRank() {
        return this.rank;
    }

}
