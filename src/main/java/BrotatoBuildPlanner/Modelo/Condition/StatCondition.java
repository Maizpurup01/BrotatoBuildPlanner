package BrotatoBuildPlanner.Modelo.Condition;

import BrotatoBuildPlanner.Modelo.BuildContext;
import BrotatoBuildPlanner.Modelo.Modifier.Modifier;
import BrotatoBuildPlanner.Modelo.Stats.Stat;
import BrotatoBuildPlanner.Modelo.Stats.Stats;

/**
 * Esta clase gestiona las condiciones de que un aumento o decenso de estadisticas se aplique
 * (ej:`+ 1 de RANGED_DAMAGE por cada 2 de MELEE_DAMAGE que se posea)
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
