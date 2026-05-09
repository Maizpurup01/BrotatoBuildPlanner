package BrotatoBuildPlanner.APP;

import BrotatoBuildPlanner.Controlador.Database.CreateTables;
import BrotatoBuildPlanner.Vista.Window;

/**
 *
 * @author Manuel
 */
public class BrotatoBuildPlanner {
    public static void main(String[] args) {
        CreateTables.createTables();
        Window.launchApp(args);
    }
}
