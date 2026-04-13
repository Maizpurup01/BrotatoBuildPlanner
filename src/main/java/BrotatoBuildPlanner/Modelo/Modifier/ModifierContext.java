package BrotatoBuildPlanner.Modelo.Modifier;

import BrotatoBuildPlanner.Modelo.Stats.Stat;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Manuel
 */
public class ModifierContext {
    private Map<Stat,Double> modifierMultipliers = new HashMap();
    
    public double getMultiplier(Stat stat){
        return modifierMultipliers.getOrDefault(stat, 1.0);
    }
    
    public void increaseMultiplier(Stat stat, double value){
        double current = modifierMultipliers.getOrDefault(stat, 1.0);
        modifierMultipliers.put(stat, current + value);
    }
}
