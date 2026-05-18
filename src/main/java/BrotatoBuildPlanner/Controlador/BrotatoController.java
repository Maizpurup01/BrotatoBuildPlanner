package BrotatoBuildPlanner.Controlador;

import BrotatoBuildPlanner.Controlador.Database.CatalogLoader;
import BrotatoBuildPlanner.Controlador.Database.CreateTables;
import BrotatoBuildPlanner.Modelo.Catalog.GameCatalog;
import BrotatoBuildPlanner.Vista.Window;

/**
 * Controlador de la aplicacion
 *
 * @author Manuel
 */
public class BrotatoController {

    public static void iniciarBD() {
        CreateTables.createTables();
    }

    /**
     * Carga el catalogo de juego.
     * Intenta leer desde la BD; si no es posible usa datos de muestra.
     */
    public static GameCatalog loadCatalog() {
        return CatalogLoader.load();
    }

    public static void iniciar() {
        iniciarBD();
        Window.launchApp(new String[0]);
    }
}
