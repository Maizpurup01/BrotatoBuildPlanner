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
            "CREATE TABLE IF NOT EXISTS weapon (id INTEGER PRIMARY KEY, name TEXT, description TEXT, cuantity INTEGER, set1 TEXT, set2 TEXT, weapon_type TEXT, tier INTEGER, damage REAL, attack_speed REAL, range INTEGER, lifesteal INTEGER);",
            "CREATE TABLE IF NOT EXISTS modifier (id INTEGER PRIMARY KEY, stat TEXT, value REAL, type TEXT, priority TEXT, condition_type TEXT, condition_stat TEXT, condition_weapon_type TEXT, condition_quantity INTEGER);",
            "CREATE TABLE IF NOT EXISTS weapon_set_bonus (id INTEGER PRIMARY KEY, set_type TEXT, required_amount INTEGER, modifier_id INTEGER, FOREIGN KEY (modifier_id) REFERENCES modifier(id) ON DELETE CASCADE);",
            "CREATE TABLE IF NOT EXISTS character_modifier (character_id INTEGER, modifier_id INTEGER, PRIMARY KEY (character_id, modifier_id), FOREIGN KEY (character_id) REFERENCES character(id) ON DELETE CASCADE, FOREIGN KEY (modifier_id) REFERENCES modifier(id) ON DELETE CASCADE);",
            "CREATE TABLE IF NOT EXISTS item_modifier (item_id INTEGER, modifier_id INTEGER, PRIMARY KEY (item_id, modifier_id), FOREIGN KEY (item_id) REFERENCES item(id) ON DELETE CASCADE, FOREIGN KEY (modifier_id) REFERENCES modifier(id) ON DELETE CASCADE);",
            "CREATE TABLE IF NOT EXISTS weapon_modifier (weapon_id INTEGER, modifier_id INTEGER, PRIMARY KEY (weapon_id, modifier_id), FOREIGN KEY (weapon_id) REFERENCES weapon(id) ON DELETE CASCADE, FOREIGN KEY (modifier_id) REFERENCES modifier(id) ON DELETE CASCADE);",
            "CREATE TABLE IF NOT EXISTS character_start_weapon (character_id INTEGER, weapon_id INTEGER, amount INTEGER, PRIMARY KEY (character_id, weapon_id), FOREIGN KEY (character_id) REFERENCES character(id) ON DELETE CASCADE, FOREIGN KEY (weapon_id) REFERENCES weapon(id) ON DELETE CASCADE);"
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
