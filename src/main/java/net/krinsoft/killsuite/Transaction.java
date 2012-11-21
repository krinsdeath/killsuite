package net.krinsoft.killsuite;

/**
 * @author krinsdeath
 */
class Transaction {

    private final String name;
    private final double amount;
    private final int type;

    public Transaction(String name, double amt, int type) {
        this.name = name;
        this.amount = amt;
        this.type = type;
    }

    public String getName() {
        return this.name;
    }

    public double getAmount() {
        return this.amount;
    }

    public int getType() {
        return this.type;
    }

}
