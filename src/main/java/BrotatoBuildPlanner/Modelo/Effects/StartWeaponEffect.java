package BrotatoBuildPlanner.Modelo.Effects;

import BrotatoBuildPlanner.Modelo.BuildContext;
import BrotatoBuildPlanner.Modelo.Weapon.Weapon;

/**
 *
 * @author Manuel
 */
public class StartWeaponEffect implements StartEffect{
    
    private Weapon weapon;
    private int amount;

    public StartWeaponEffect(Weapon weapon, int amount) {
        this.weapon = weapon;
        this.amount = amount;
    }

    @Override
    public void apply(BuildContext context) {
        for (int i = 0; i < amount; i++) {
            context.addWeapon(weapon);
        }
    }
    
}
