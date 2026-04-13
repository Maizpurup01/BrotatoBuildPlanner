package BrotatoBuildPlanner.Modelo.Stats;

import java.util.EnumMap;

/**
 *
 * @author Manuel
 */
public class Stats {

    private EnumMap<Stat, Double> stats;

    public Stats() {
        this.stats = new EnumMap(Stat.class);
    }

    public double getStat(Stat stat) {
        return stats.getOrDefault(stat, 0.0);
    }

    public void setStat(Stat stat, double value) {
        stats.put(stat, value);
    }

    public void addStat(Stat stat, double value) {
        double current = getStat(stat);
        stats.put(stat, current + value);
    }

    public void multiplyStat(Stat stat, double multiplier) {
        double current = getStat(stat);
        stats.put(stat, current * multiplier);
    }

    public void increasePercentage(Stat stat, double percent) {
        double current = getStat(stat);
        double increase = current * (percent / 100);
        setStat(stat, current + increase);
    }

    public void capStat(Stat stat, double maxValue) {
        double current = getStat(stat);
        if (current > maxValue) {
            setStat(stat, maxValue);
        }
    }

    public void addFromStats(Stats other) {
        for (Stat stat : Stat.values()) {
            double value = other.getStat(stat);
            if (value != 0) {
                addStat(stat, value);
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Stat stat : Stat.values()) {
            double value = getStat(stat);
            if (value != 0) {
                sb.append(stat)
                        .append(": ")
                        .append(value)
                        .append("\n");
            }
        }
        return sb.toString();
    }
}
