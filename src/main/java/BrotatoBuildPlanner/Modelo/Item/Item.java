package BrotatoBuildPlanner.Modelo.Item;

import BrotatoBuildPlanner.Modelo.Modifier.Modifier;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;

/**
 * Clase en la que se convertira un registro de la tabla item en BD
 *
 * @author Manuel
 */
public class Item extends Items {

    private List<Modifier> modifiers; // modificadores del objeto, para calcular la mejora de estadisticas que proporcionan(o disminuirla)
    private ItemCategory category;
    private int maxStack;

    public Item(String name, String descripcion, ImageIcon imagen, int cantidad) {
        this(name, descripcion, imagen, cantidad, ItemTier.COMMON, ItemCategory.ITEM, cantidad);
    }

    public Item(String name, String descripcion, ImageIcon imagen, int cantidad, ItemTier tier, ItemCategory category, int maxStack) {
        super(name, descripcion, imagen, cantidad, tier);
        this.modifiers = new ArrayList<>();
        this.category = category;
        this.maxStack = maxStack;
    }

    public void addModifier(Modifier modifier) {
        modifiers.add(modifier);
    }

    public List<Modifier> getModifiers() {
        return modifiers;
    }

    public ItemCategory getCategory() {
        return category;
    }

    public int getMaxStack() {
        return maxStack;
    }
}
