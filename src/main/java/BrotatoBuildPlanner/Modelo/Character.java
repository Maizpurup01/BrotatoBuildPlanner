package BrotatoBuildPlanner.Modelo;

import BrotatoBuildPlanner.Modelo.Effects.StartEffect;
import BrotatoBuildPlanner.Modelo.Item.Items;
import BrotatoBuildPlanner.Modelo.Modifier.Modifier;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;

/**
 *
 * @author Manuel
 */
public class Character extends Items {

    private List<Modifier> modifiers;
    List<StartEffect> startEffects;

    public Character() {
        super("", "",new ImageIcon(), 0);
    }

    public Character(String name, String description, ImageIcon image, int cuantity) {
        super(name, description, image, cuantity);
        this.startEffects = new ArrayList();
        this.modifiers = this.modifiers = new ArrayList();;
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
}
