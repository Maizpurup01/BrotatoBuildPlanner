package BrotatoBuildPlanner.Modelo.Item;

import BrotatoBuildPlanner.Modelo.Modifier.Modifier;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;

/**
 *
 * @author Manuel
 */
public class Item extends Items {

    private List<Modifier> modifiers;

    public Item(String name, String descripcion, ImageIcon imagen, int cantidad) {
        super(name, descripcion, imagen, cantidad);
        this.modifiers = new ArrayList();
    }

    public void addModifier(Modifier modifier) {
        modifiers.add(modifier);
    }

    public List<Modifier> getModifiers() {
        return modifiers;
    }
}
