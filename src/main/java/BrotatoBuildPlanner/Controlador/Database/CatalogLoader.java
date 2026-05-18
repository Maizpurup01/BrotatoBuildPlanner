package BrotatoBuildPlanner.Controlador.Database;

import BrotatoBuildPlanner.Controlador.Database.DAO.CharacterDAO;
import BrotatoBuildPlanner.Controlador.Database.DAO.ItemDAO;
import BrotatoBuildPlanner.Controlador.Database.DAO.WeaponDAO;
import BrotatoBuildPlanner.Controlador.Database.DAO.WeaponSetBonusDAO;
import BrotatoBuildPlanner.Modelo.Catalog.GameCatalog;
import BrotatoBuildPlanner.Modelo.Catalog.SampleCatalogFactory;
import BrotatoBuildPlanner.Modelo.Character;
import BrotatoBuildPlanner.Modelo.Item.Item;
import BrotatoBuildPlanner.Modelo.Weapon.Weapon;
import BrotatoBuildPlanner.Modelo.Weapon.WeaponSetBonus;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Carga el catalogo de juego desde la BD.
 * Si la conexion falla o la BD no contiene datos, utiliza los datos de
 * muestra de SampleCatalogFactory como fallback.
 */
public class CatalogLoader {

    /**
     * Intenta cargar el catalogo desde la BD SQLite.
     * Cae en datos de muestra si:
     *   - No se puede establecer conexion.
     *   - La tabla character esta vacia (seeder no ejecutado).
     *   - Ocurre cualquier error durante la lectura.
     *
     * @return GameCatalog listo para usar.
     */
    public static GameCatalog load() {
        try (Connection conn = Database.connect()) {
            if (conn == null) {
                System.out.println("[CatalogLoader] Sin conexion a BD, usando datos de muestra.");
                return SampleCatalogFactory.create();
            }

            if (!hasData(conn)) {
                System.out.println("[CatalogLoader] BD vacia, usando datos de muestra.");
                return SampleCatalogFactory.create();
            }

            return loadFromDatabase(conn);

        } catch (Exception e) {
            System.out.println("[CatalogLoader] Error al leer BD, usando datos de muestra: " + e.getMessage());
            return SampleCatalogFactory.create();
        }
    }

    private static boolean hasData(Connection conn) {
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM character")) {
            return rs.next() && rs.getInt(1) > 0;
        } catch (Exception e) {
            return false;
        }
    }

    private static GameCatalog loadFromDatabase(Connection conn) throws Exception {
        List<Weapon> weapons = WeaponDAO.findAll(conn);

        Map<String, Weapon> weaponByName = new HashMap<>();
        for (Weapon w : weapons) {
            weaponByName.put(w.getName(), w);
        }

        List<Item> items = ItemDAO.findAll(conn);

        List<Character> characters = CharacterDAO.findAll(conn, weaponByName);

        List<WeaponSetBonus> bonuses = WeaponSetBonusDAO.findAll(conn);

        Map<String, Character> characterByName = new HashMap<>();
        for (Character c : characters) {
            characterByName.put(c.getName(), c);
        }

        Map<String, Item> itemByName = new HashMap<>();
        for (Item i : items) {
            itemByName.put(i.getName(), i);
        }

        System.out.println("[CatalogLoader] Catalogo cargado desde BD: "
                + characters.size() + " personajes, "
                + items.size() + " items, "
                + weapons.size() + " armas, "
                + bonuses.size() + " bonos de set.");

        return new GameCatalog(characters, items, weapons, bonuses,
                characterByName, itemByName, weaponByName);
    }
}
