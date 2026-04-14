package BrotatoBuildPlanner.Vista.Renderer;

import BrotatoBuildPlanner.Modelo.Item.Items;
import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

/**
 * Esta clase se encarga de invocar un elemento visual que permita ver un 
 * objeto con mas detalle al pasar el raton por encima en la lista
 *
 * @author Manuel
 */
public class Renderer extends JLabel implements ListCellRenderer<Items>{

    @Override
    public Component getListCellRendererComponent(
            JList<? extends Items> list, 
            Items value, 
            int index, 
            boolean isSelected, 
            boolean cellHasFocus) {
        
        setIcon(value.getImage());
        setText("");
        
        if(isSelected){
            setBackground(list.getSelectionBackground());
            setOpaque(true);
        }else{
            setOpaque(false);
        }
        
        return this;
    }
    
    
}
