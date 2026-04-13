package BrotatoBuildPlanner.Modelo.Weapon;

import BrotatoBuildPlanner.Modelo.BuildContext;
import BrotatoBuildPlanner.Modelo.Modifier.Modifier;

/**
 *
 * @author Manuel
 */
public class WeaponSetBonus {
    private WeaponSet type;
    private int requiredAmount;
    private Modifier modifier;
    
    public boolean applies(BuildContext context){
        int count = 0;
        for(Weapon w : context.getWeapons()){
            if(w.getSet1() == type || w.getSet2() == type){
                count++;
                if(count >= 6){
                    count = 6;
                }
            }
        }
        
        return count == requiredAmount;
    }
    
    public Modifier getModifier(){
        return modifier;
    }
}
