package BrotatoBuildPlanner.Modelo.Condition;

import BrotatoBuildPlanner.Modelo.BuildContext;
import BrotatoBuildPlanner.Modelo.Stats.Stats;
import BrotatoBuildPlanner.Modelo.Weapon.Weapon;
import BrotatoBuildPlanner.Modelo.Weapon.WeaponType;

/**
 * Esta clase gestiona las condiciones de que un modificador de set se aplique
 * (por numero de armas del mismo set que se tengan equipadas)
 *
 * @author Manuel
 */
public class WeaponTypeCondition implements Condition {

    private WeaponType type;
    private int required;

    public WeaponTypeCondition(WeaponType type, int required) {
        this.type = type;
        this.required = required;
    }

    @Override
    public int getMultiplier(BuildContext context, Stats stats) {
        int count = 0;
        
        for (Weapon weapon: context.getWeapons()) {
            if(weapon.getType() == type){
                count++;
            }
        }
        
        if(count >= required){
            return 1;
        }
        return 0;
    }
}
