package BrotatoBuildPlanner.Modelo.Item;

import javax.swing.ImageIcon;

/**
 *
 * @author Manuel
 */
public class Items {
    private String name;
    private String description;
    private ImageIcon image;
    private int cuantity;

    public Items(String name, String description, ImageIcon image, int cuantity) {
        this.name = name;
        this.description = description;
        this.image = image;
        this.cuantity = cuantity;
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
}
