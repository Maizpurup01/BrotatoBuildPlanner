package BrotatoBuildPlanner.Controlador;

import BrotatoBuildPlanner.Controlador.Database.CreateTables;
import BrotatoBuildPlanner.Controlador.Database.Database;
import BrotatoBuildPlanner.Modelo.Item.Items;
import BrotatoBuildPlanner.Vista.Window;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import javax.swing.JList;

/**
 * Controlador de la aplicacion
 *
 * @author Manuel
 */
public class BrotatoController {
    static private Connection conn;
    
    static public Character selectedCharacter;
    static public Window window = new Window();
    
    public static void iniciarBD() {
        CreateTables.createTables();
    }
    
    public static void iniciar() {
        iniciarBD();
        window.setVisible(true);
        window.setLocationRelativeTo(null);
    }
    
    //Metodo para invocar un cuadro de texto con el nombre y la descripcion de un objeto al pasar el raton sobre el en la lista
    public static void invokeTooltip(JList<Items> characterList, MouseEvent evt) {
        int index = characterList.locationToIndex(evt.getPoint());
        
        if (index >= 0) {
            Items item = characterList.getModel().getElementAt(index);
            
            characterList.setToolTipText("<html><b>" + item.getName() + "</b><br>" + item.getDescription() + "</html>");
        }
    }
}
