package BrotatoBuildPlanner.Modelo;

import BrotatoBuildPlanner.Modelo.Item.Item;
import BrotatoBuildPlanner.Modelo.Modifier.Modifier;
import BrotatoBuildPlanner.Modelo.Modifier.ModifierContext;
import BrotatoBuildPlanner.Modelo.Stats.Stats;
import BrotatoBuildPlanner.Modelo.Weapon.Weapon;
import BrotatoBuildPlanner.Modelo.Weapon.WeaponSetBonus;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * @author Manuel
 * 
 * Clase que se encarga de calcular las estadisticas de la build utilizando 
 * el metodo calculate que calcula las estadisticas mediante el metodo apply de 
 * la clase Modifier.
 * Tambien utiliza el metodo collectModifiers para almacenar todos los 
 * modificadores y ordenarlos por prioridad antes de realizar el calculo.
 * 
 */
public class BuildCalculator {
    /**
     * Calcula las estadisticas que se mostraran en la UI
     * @param context contexto de la build, contiene el personaje y los objetos
     * para realizar el calculo
     * @param weaponSetBonuses bonus de set de las armas en la lista dentro de
     * context
     * @return estadisticas calculadas
     */
    public Stats calculate(BuildContext context, List<WeaponSetBonus> weaponSetBonuses) {
        Stats stats = new Stats();
        ModifierContext modifierContext = new ModifierContext();
        
        List<Modifier> modifiers = collectModifiers(context,weaponSetBonuses);

        //calcular stats previamente almacenadas y ordenadas por prioridad
        for(Modifier modifier: modifiers){
            modifier.apply(stats, context, modifierContext);
        }

        //devolver stats calculadas
        return stats;
    }
    
    /**
     * Recolecta todos los modificadores uno por uno
     * se deben ordenar los modificadores con este comando
     * para garantizar que se calculen bien las estadisticas
     * modifiers.sort(Comparator.comparing(Modifier::getPriority));
     * @param context contexto de la build, contiene el personaje y los objetos
     * para recolectar y ordenar
     * @param weaponSetBonuses bonus de set de las armas en la lista dentro de
     * context
    */
    public List<Modifier> collectModifiers(BuildContext context, List<WeaponSetBonus> weaponSetBonuses){
        List<Modifier> modifiers = new ArrayList();
        // personaje
        if(context.getCharacter() != null){
            modifiers.addAll(context.getCharacter().getModifiers());
        }
        // objetos
        for(Item item: context.getItems()){
            modifiers.addAll(item.getModifiers());
        }
        // armas
        for(Weapon weapon: context.getWeapons()){
            modifiers.addAll(weapon.getModifiers());
        }
        // bonus de set
        if(weaponSetBonuses != null){
            for(WeaponSetBonus bonus: weaponSetBonuses){
                if(bonus.applies(context)){
                    modifiers.add(bonus.getModifier());
                }
            }
        }
        //se ordenan por prioridad
        modifiers.sort(Comparator.comparing(Modifier::getPriority));
        // y se devuelven al metodo calculate
        return modifiers;
    }
}
