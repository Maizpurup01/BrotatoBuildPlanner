package BrotatoBuildPlanner.Modelo.Catalog;

import BrotatoBuildPlanner.Modelo.Character;
import BrotatoBuildPlanner.Modelo.Effects.StartWeaponEffect;
import BrotatoBuildPlanner.Modelo.Item.Item;
import BrotatoBuildPlanner.Modelo.Item.ItemCategory;
import BrotatoBuildPlanner.Modelo.Item.ItemTier;
import BrotatoBuildPlanner.Modelo.Modifier.Modifier;
import BrotatoBuildPlanner.Modelo.Modifier.ModifierPriority;
import BrotatoBuildPlanner.Modelo.Modifier.ModifierType;
import BrotatoBuildPlanner.Modelo.Stats.Stat;
import BrotatoBuildPlanner.Modelo.Weapon.Weapon;
import BrotatoBuildPlanner.Modelo.Weapon.WeaponSet;
import BrotatoBuildPlanner.Modelo.Weapon.WeaponSetBonus;
import BrotatoBuildPlanner.Modelo.Weapon.WeaponType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.ImageIcon;

/**
 * Datos de ejemplo para poder usar la app sin depender de carga desde BD.
 */
public final class SampleCatalogFactory {
    private SampleCatalogFactory() {
    }

    public static GameCatalog create() {
        List<Character> characters = new ArrayList<>();
        List<Item> items = new ArrayList<>();
        List<Weapon> weapons = new ArrayList<>();
        List<WeaponSetBonus> bonuses = new ArrayList<>();

        Weapon stick = new Weapon("Stick", "Arma base sencilla de melee", new ImageIcon(), 6,
                WeaponSet.PRIMITIVE, WeaponSet.NOTYPE, WeaponType.MELEE, 1,
                5.0, 1.0, 80, 0);
        Weapon pistol = new Weapon("Pistol", "Arma a distancia equilibrada", new ImageIcon(), 6,
                WeaponSet.GUN, WeaponSet.PRECISE, WeaponType.RANGED, 1,
                6.0, 1.1, 350, 0);
        Weapon wand = new Weapon("Wand", "Arma elemental", new ImageIcon(), 6,
                WeaponSet.ELEMENTAL, WeaponSet.MEDICAL, WeaponType.ELEMENTAL, 1,
                7.0, 0.9, 260, 0);

        weapons.add(stick);
        weapons.add(pistol);
        weapons.add(wand);

        Character wellRounded = new Character("Well Rounded", "+5 Max HP y +5% Damage", new ImageIcon(), 1, 6);
        wellRounded.addModifier(new Modifier(Stat.MAX_HP, 5, ModifierType.FLAT, ModifierPriority.FLAT));
        wellRounded.addModifier(new Modifier(Stat.DAMAGE, 5, ModifierType.PERCENTAGE, ModifierPriority.PERCENTAGE));

        Character oneArmed = new Character("One Armed", "Solo puede usar 1 arma", new ImageIcon(), 1, 1);
        oneArmed.addModifier(new Modifier(Stat.ATTACK_SPEED, 200, ModifierType.PERCENTAGE, ModifierPriority.PERCENTAGE));

        Character brawler = new Character("Brawler", "Empieza con Stick", new ImageIcon(), 1, 6);
        brawler.addStartEffect(new StartWeaponEffect(stick, 1));
        brawler.addModifier(new Modifier(Stat.MELEE_DAMAGE, 5, ModifierType.FLAT, ModifierPriority.FLAT));

        characters.add(wellRounded);
        characters.add(oneArmed);
        characters.add(brawler);

        Item alienWorm = new Item("Alien Worm", "+3 Max HP, +2 HP Regen", new ImageIcon(), 999,
                ItemTier.COMMON, ItemCategory.ITEM, 999);
        alienWorm.addModifier(new Modifier(Stat.MAX_HP, 3, ModifierType.FLAT, ModifierPriority.FLAT));
        alienWorm.addModifier(new Modifier(Stat.HP_REGEN, 2, ModifierType.FLAT, ModifierPriority.FLAT));

        Item spicySauce = new Item("Spicy Sauce", "Limited (4): +3 Max HP", new ImageIcon(), 4,
                ItemTier.UNCOMMON, ItemCategory.LIMITED, 4);
        spicySauce.addModifier(new Modifier(Stat.MAX_HP, 3, ModifierType.FLAT, ModifierPriority.FLAT));

        Item warriorHelmet = new Item("Warrior Helmet", "+3 Armor, +5 Max HP, -5% Speed", new ImageIcon(), 999,
                ItemTier.RARE, ItemCategory.ITEM, 999);
        warriorHelmet.addModifier(new Modifier(Stat.ARMOR, 3, ModifierType.FLAT, ModifierPriority.FLAT));
        warriorHelmet.addModifier(new Modifier(Stat.MAX_HP, 5, ModifierType.FLAT, ModifierPriority.FLAT));
        warriorHelmet.addModifier(new Modifier(Stat.SPEED, -5, ModifierType.PERCENTAGE, ModifierPriority.PERCENTAGE));

        Item gentleAlien = new Item("Gentle Alien", "+2 Max HP, +5% Damage", new ImageIcon(), 10,
                ItemTier.COMMON, ItemCategory.LIMITED, 10);
        gentleAlien.addModifier(new Modifier(Stat.MAX_HP, 2, ModifierType.FLAT, ModifierPriority.FLAT));
        gentleAlien.addModifier(new Modifier(Stat.DAMAGE, 5, ModifierType.PERCENTAGE, ModifierPriority.PERCENTAGE));

        Item attackTraining = new Item("Attack Training", "Los aumentos de Damage se mejoran en 25%", new ImageIcon(), 999,
                ItemTier.EPIC, ItemCategory.ITEM, 999);
        attackTraining.addModifier(new Modifier(Stat.DAMAGE, 0.25, ModifierType.MULTIPLIER, ModifierPriority.BASE));

        items.add(alienWorm);
        items.add(spicySauce);
        items.add(warriorHelmet);
        items.add(gentleAlien);
        items.add(attackTraining);

        bonuses.add(new WeaponSetBonus(WeaponSet.PRIMITIVE, 2,
                new Modifier(Stat.MAX_HP, 5, ModifierType.FLAT, ModifierPriority.FLAT)));
        bonuses.add(new WeaponSetBonus(WeaponSet.PRIMITIVE, 4,
                new Modifier(Stat.ATTACK_SPEED, 15, ModifierType.PERCENTAGE, ModifierPriority.PERCENTAGE)));
        bonuses.add(new WeaponSetBonus(WeaponSet.GUN, 2,
                new Modifier(Stat.RANGED_DAMAGE, 2, ModifierType.FLAT, ModifierPriority.FLAT)));

        Map<String, Character> charMap = new HashMap<>();
        for (Character character : characters) {
            charMap.put(character.getName(), character);
        }

        Map<String, Item> itemMap = new HashMap<>();
        for (Item item : items) {
            itemMap.put(item.getName(), item);
        }

        Map<String, Weapon> weaponMap = new HashMap<>();
        for (Weapon weapon : weapons) {
            weaponMap.put(weapon.getName(), weapon);
        }

        return new GameCatalog(characters, items, weapons, bonuses, charMap, itemMap, weaponMap);
    }
}
