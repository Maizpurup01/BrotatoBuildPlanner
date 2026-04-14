package BrotatoBuildPlanner.Modelo.Weapon;

import BrotatoBuildPlanner.Modelo.Item.Items;
import BrotatoBuildPlanner.Modelo.Modifier.Modifier;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;

/**
 * Clase de las diferentes armas del juego(cada elemento de la tabla 
 * weapon en BD se convertira a un objeto weapon)
 *
 * @author Manuel
 */
public class Weapon extends Items {

    private WeaponSet set1; // cada arma puede tener dos sets a los que pertenece(vea WeaponSet para referencia)
    private WeaponSet set2; // algunas armas no tienen segundo set, si eso pasa se les asigna NOTYPE para evitar nulos
    private WeaponType type; // Tipo de arma(MELEE, RANGED, ELEMENTA, SUPPORT)
    private int tier; // 4 tiers principales: 1. comun(gris), 2. poco comun(azul), 3. raro(morado), 4. epico(rojo)
    private double damage; // daño base del arma
    private double attackSpeed; // velocidad base de ataque del arma
    private int range; // rango base de ataque del arma
    private int lifesteal; // robo de vida base del arma, si no tiene, por defecto = 0
    private List<Modifier> modifiers; // Las armas no tienen modificadores por si mismas, pero si lo tienen sus sets

    

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
