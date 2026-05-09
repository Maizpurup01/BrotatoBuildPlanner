package BrotatoBuildPlanner.Modelo;

import BrotatoBuildPlanner.Modelo.Effects.StartEffect;
import BrotatoBuildPlanner.Modelo.Item.Items;
import BrotatoBuildPlanner.Modelo.Modifier.Modifier;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;

/**
 * En esta clase se convierte un registro de la table character en BD
 *
 * @author Manuel
 */
public class Character extends Items {

    private List<Modifier> modifiers;
    private List<StartEffect> startEffects;
    private int maxWeaponSlots;
    
    //personaje base para evitar nulos
    public Character() {
        super("", "", new ImageIcon(), 0);
        this.startEffects = new ArrayList<>();
        this.modifiers = new ArrayList<>();
        this.maxWeaponSlots = 6;
    }

    public Character(String name, String description, ImageIcon image, int cuantity) {
        this(name, description, image, cuantity, 6);
    }

    public Character(String name, String description, ImageIcon image, int cuantity, int maxWeaponSlots) {
        super(name, description, image, cuantity);
        this.startEffects = new ArrayList<>();
        this.modifiers = new ArrayList<>();
        this.maxWeaponSlots = maxWeaponSlots;
    }

    public List<StartEffect> getStartEffects() {
        return startEffects;
    }

    public void addStartEffect(StartEffect effect) {
        startEffects.add(effect);
    }

    public void addModifier(Modifier modifier) {
        modifiers.add(modifier);
    }

    public List<Modifier> getModifiers() {
        return modifiers;
    }

    public int getMaxWeaponSlots() {
        return maxWeaponSlots;
    }
}
