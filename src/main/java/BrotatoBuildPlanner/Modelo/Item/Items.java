package BrotatoBuildPlanner.Modelo.Item;

import javax.swing.ImageIcon;

/**
 * Clase padre que engloba todos los campos que tienen en comun todos los tipos en BD(character, weapon, item, etc)
 *
 * @author Manuel
 */
public class Items {
    private String name; // nombre del objeto, arma...
    private String description; // descripcion para el renderer
    private ImageIcon image; // imagen para UI
    private int cuantity; // cantidad maxima que se puede poseer de ese objeto en la build.
    private ItemTier tier;

    public Items(String name, String description, ImageIcon image, int cuantity) {
        this(name, description, image, cuantity, ItemTier.COMMON);
    }

    public Items(String name, String description, ImageIcon image, int cuantity, ItemTier tier) {
        this.name = name;
        this.description = description;
        this.image = image;
        this.cuantity = cuantity;
        this.tier = tier;
    }

    public String getName() {
        return name;
    }

    public ImageIcon getImage() {
        return image;
    }
    
    public int getCuantity(){
        return cuantity;
    }

    public String getDescription() {
        return description;
    }

    public ItemTier getTier() {
        return tier;
    }

    @Override
    public String toString() {
        return name;
    }
}
