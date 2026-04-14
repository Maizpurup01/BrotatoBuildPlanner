package BrotatoBuildPlanner.Modelo.Modifier;

import BrotatoBuildPlanner.Modelo.Condition.Condition;
import BrotatoBuildPlanner.Modelo.BuildContext;
import BrotatoBuildPlanner.Modelo.Condition.StatCondition;
import BrotatoBuildPlanner.Modelo.Stats.Stat;
import BrotatoBuildPlanner.Modelo.Stats.Stats;

/**
 * Clase en la que se convierte un registro de la tabla modifier en BD
 *
 * @author Manuel
 */
public class Modifier {

    private Stat stat; // estadistica que modifica
    private double value; // valor de la estadistica que modifica(ej: 30.0)
    private ModifierType type; // tipo del modificador(ver enum ModificerType)
    private ModifierPriority priority; // prioridad del modificador(ver ModificerPriority)
    private Condition condition; // condicion de que el modificador se aplique

    public Modifier(Stat stat, double value, ModifierType type, ModifierPriority priority) {
        this.stat = stat;
        this.value = value;
        this.type = type;
        this.priority = priority;
    }

    public void setCondition(Condition condition) {
        this.condition = condition;
    }

    public ModifierPriority getPriority() {
        return priority;
    }

    public void apply(Stats stats, BuildContext context, ModifierContext modifierContext) {
        
        int times = 1;
        if (condition != null) {
            times = condition.getMultiplier(context, stats);
            if(times == 0){
                return;
            }
        }

        double multiplier = modifierContext.getMultiplier(stat);
        double modifiedValue = value * multiplier * times;

        switch (type) {
            case FLAT:
                stats.addStat(stat, modifiedValue);
                break;
            case PERCENTAGE:
                double current = stats.getStat(stat);
                stats.addStat(stat, current * (modifiedValue / 100));
                break;
            case MULTIPLIER:
                stats.multiplyStat(stat, modifiedValue);
                break;
        }
    }

    public Stat getStat() {
        return stat;
    }

    public double getValue() {
        return value;
    }

    public ModifierType getType() {
        return type;
    }
}
