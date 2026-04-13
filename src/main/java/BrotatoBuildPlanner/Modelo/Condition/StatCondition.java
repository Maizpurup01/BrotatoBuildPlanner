package BrotatoBuildPlanner.Modelo.Condition;

import BrotatoBuildPlanner.Modelo.BuildContext;
import BrotatoBuildPlanner.Modelo.Modifier.Modifier;
import BrotatoBuildPlanner.Modelo.Stats.Stat;
import BrotatoBuildPlanner.Modelo.Stats.Stats;

/**
 *
 * @author Manuel
 */
public class StatCondition implements Condition {

    private Stat stat;
    private int quantity;

    public StatCondition(Stat stat, int quantity) {
        this.stat = stat;
        this.quantity = quantity;
    }
    
    @Override
    public int getMultiplier(BuildContext context, Stats stats) {
        double statValue = stats.getStat(stat);
        
        return (int)(statValue / quantity);
    }
}
