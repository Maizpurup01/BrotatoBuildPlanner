package BrotatoBuildPlanner.Modelo;

import BrotatoBuildPlanner.Modelo.Item.Item;
import BrotatoBuildPlanner.Modelo.Weapon.Weapon;
import java.util.List;

/**
 *
 * @author Manuel
 * 
 */
public class BuildContext {
    
    private Character character;
    private List<Item> items;
    private List<Weapon> weapons;

    public BuildContext(Character character, List<Item> items, List<Weapon> weapons) {
        this.character = character;
        this.items = items;
        this.weapons = weapons;
    }

    public void setCharacter(Character character) {
        this.character = character;
    }

    public void addItem(Item item) {
        items.add(item);
    }

    public void addWeapon(Weapon weapon) {
        weapons.add(weapon);
    }

    public List<Weapon> getWeapons() {
        return weapons;
    }

    public List<Item> getItems() {
        return items;
    }

    public Character getCharacter() {
        return character;
    }
}
