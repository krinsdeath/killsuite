package net.krinsoft.killsuite.util;

import java.text.DecimalFormat;

/**
 * @author krinsdeath
 */
public class RewardGenerator {
    private final double lower;
    private final double upper;

    public RewardGenerator(double l, double u) {
        this.lower = l;
        this.upper = u;
    }

    public double generateRandom() {
        return Double.valueOf(new DecimalFormat("#.##").format(lower + (Math.random() * ((upper - lower)))));
    }

}
