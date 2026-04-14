package BrotatoBuildPlanner.Modelo.Effects;

import BrotatoBuildPlanner.Modelo.BuildContext;
import BrotatoBuildPlanner.Modelo.Weapon.Weapon;

/**
 * clase que gestionara los efectos iniciales de los personajes(ej: comienzas con un cuchillo)
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
