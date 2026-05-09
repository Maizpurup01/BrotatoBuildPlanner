package BrotatoBuildPlanner.Modelo.Catalog;

import BrotatoBuildPlanner.Modelo.Character;
import BrotatoBuildPlanner.Modelo.Item.Item;
import BrotatoBuildPlanner.Modelo.Weapon.Weapon;
import BrotatoBuildPlanner.Modelo.Weapon.WeaponSetBonus;
import java.util.List;
import java.util.Map;

/**
 * Catalogo de entidades disponibles para construir la build.
 */
public class GameCatalog {
    private final List<Character> characters;
    private final List<Item> items;
    private final List<Weapon> weapons;
    private final List<WeaponSetBonus> weaponSetBonuses;
    private final Map<String, Character> characterByName;
    private final Map<String, Item> itemByName;
    private final Map<String, Weapon> weaponByName;

    public GameCatalog(
            List<Character> characters,
            List<Item> items,
            List<Weapon> weapons,
            List<WeaponSetBonus> weaponSetBonuses,
            Map<String, Character> characterByName,
            Map<String, Item> itemByName,
            Map<String, Weapon> weaponByName) {
        this.characters = characters;
        this.items = items;
        this.weapons = weapons;
        this.weaponSetBonuses = weaponSetBonuses;
        this.characterByName = characterByName;
        this.itemByName = itemByName;
        this.weaponByName = weaponByName;
    }

    public List<Character> getCharacters() {
        return characters;
    }

    public List<Item> getItems() {
        return items;
    }

    public List<Weapon> getWeapons() {
        return weapons;
    }

    public List<WeaponSetBonus> getWeaponSetBonuses() {
        return weaponSetBonuses;
    }

    public Character findCharacter(String name) {
        return characterByName.get(name);
    }

    public Item findItem(String name) {
        return itemByName.get(name);
    }

    public Weapon findWeapon(String name) {
        return weaponByName.get(name);
    }
}
