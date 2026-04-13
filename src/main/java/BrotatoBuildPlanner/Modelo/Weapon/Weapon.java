package BrotatoBuildPlanner.Modelo.Weapon;

import BrotatoBuildPlanner.Modelo.Item.Items;
import BrotatoBuildPlanner.Modelo.Modifier.Modifier;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;

/**
 *
 * @author Manuel
 */
public class Weapon extends Items {

    private WeaponSet set1;
    private WeaponSet set2;
    private WeaponType type;
    private int tier;
    private double damage;
    private double attackSpeed;
    private int range;
    private int lifesteal;
    private List<Modifier> modifiers;

    

    public Weapon(String name, String description, ImageIcon image, int cuantity, WeaponSet set1, WeaponSet set2, WeaponType type, int tier, double damage, double attackSpeed, int range, int lifesteal) {
        super(name, description, image, cuantity);
        this.set1 = set1;
        this.set2 = set2;
        this.type = type;
        this.tier = tier;
        this.damage = damage;
        this.attackSpeed = attackSpeed;
        this.range = range;
        this.lifesteal = lifesteal;
        this.modifiers = new ArrayList();
    }

    public void addModifier(Modifier modifier) {
        modifiers.add(modifier);
    }

    public WeaponSet getSet1() {
        return set1;
    }

    public WeaponSet getSet2() {
        return set2;
    }

    public WeaponType getType() {
        return type;
    }

    public int getTier() {
        return tier;
    }

    public double getDamage() {
        return damage;
    }

    public double getAttackSpeed() {
        return attackSpeed;
    }

    public int getRange() {
        return range;
    }

    public int getLifesteal() {
        return lifesteal;
    }

    public List<Modifier> getModifiers() {
        return modifiers;
    }
}
