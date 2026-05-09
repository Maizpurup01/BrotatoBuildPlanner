package BrotatoBuildPlanner.Controlador;

import BrotatoBuildPlanner.Controlador.Database.CreateTables;
import BrotatoBuildPlanner.Vista.Window;
import java.sql.Connection;

/**
 * Controlador de la aplicacion
 *
 * @author Manuel
 */
public class BrotatoController {
    static private Connection conn;
    
    public static void iniciarBD() {
        CreateTables.createTables();
    }
    
    public static void iniciar() {
        iniciarBD();
        Window.launchApp(new String[0]);
    }
}
