package BrotatoBuildPlanner.Controlador.Database;

import java.sql.Connection;
import java.sql.Statement;

/**
 * Clase que gestiona las migraciones de la BD en local
 *
 * @author Manuel
 */
public class CreateTables {

    public static void createTables() {

        String[] tables = {
            "CREATE TABLE IF NOT EXISTS character (id INTEGER PRIMARY KEY, name TEXT, description TEXT, cuantity INTEGER);",
            "CREATE TABLE IF NOT EXISTS item (id INTEGER PRIMARY KEY, name TEXT, description TEXT, cuantity INTEGER);",
            "CREATE TABLE IF NOT EXISTS weapon (id INTEGER PRIMARY KEY, name TEXT, description TEXT, cuantity INTEGER, type1 TEXT, type2 TEXT, damage INTEGER, attack_speed REAL, range INTEGER, lifesteal INTEGER);",
            "CREATE TABLE IF NOT EXISTS modifier (id INTEGER PRIMARY KEY, stat TEXT, value REAL, type TEXT, priority TEXT);",
            "CREATE TABLE IF NOT EXISTS character_modifier (character_id INTEGER, modifier_id INTEGER, PRIMARY KEY (character_id, modifier_id), FOREIGN KEY (character_id) REFERENCES character(id) ON DELETE CASCADE, FOREIGN KEY (modifier_id) REFERENCES modifier(id) ON DELETE CASCADE);",
            "CREATE TABLE IF NOT EXISTS item_modifier (item_id INTEGER, modifier_id INTEGER, PRIMARY KEY (item_id, modifier_id), FOREIGN KEY (item_id) REFERENCES item(id) ON DELETE CASCADE, FOREIGN KEY (modifier_id) REFERENCES modifier(id) ON DELETE CASCADE);",
            "CREATE TABLE IF NOT EXISTS weapon_modifier (weapon_id INTEGER, modifier_id INTEGER, PRIMARY KEY (weapon_id, modifier_id), FOREIGN KEY (weapon_id) REFERENCES weapon(id) ON DELETE CASCADE, FOREIGN KEY (modifier_id) REFERENCES modifier(id) ON DELETE CASCADE);"
        };

        try (Connection conn = Database.connect(); Statement stmt = conn.createStatement()) {

            // para activar foreign keys
            stmt.execute("PRAGMA foreign_keys = ON;");

            for (String tableSql : tables) {
                stmt.execute(tableSql);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
