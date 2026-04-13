package BrotatoBuildPlanner.Vista.Renderer;

import BrotatoBuildPlanner.Modelo.Item.Items;
import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

/**
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
