package BrotatoBuildPlanner.Modelo.Condition;

import BrotatoBuildPlanner.Modelo.BuildContext;
import BrotatoBuildPlanner.Modelo.Stats.Stats;

/**
 * interfaz de diferentes condiciones
 *
 * @author Manuel
 */
public interface Condition {
    int getMultiplier(BuildContext context, Stats stats);
}