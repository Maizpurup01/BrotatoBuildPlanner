package BrotatoBuildPlanner.Controlador.Database.DAO;

import BrotatoBuildPlanner.Modelo.Character;
import BrotatoBuildPlanner.Modelo.Effects.StartWeaponEffect;
import BrotatoBuildPlanner.Modelo.Modifier.Modifier;
import BrotatoBuildPlanner.Modelo.Weapon.Weapon;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.swing.ImageIcon;

/**
 * DAO para leer personajes desde la BD, incluyendo sus modificadores y
 * armas de inicio.
 *
 * Nota: el esquema actual de character no almacena maxWeaponSlots;
 * se usa 6 por defecto hasta que el esquema se extienda.
 */
public class CharacterDAO {

    private static final int DEFAULT_MAX_WEAPON_SLOTS = 6;

    /**
     * @param weaponByName mapa nombre→Weapon ya cargado por WeaponDAO,
     *                     necesario para enlazar las armas de inicio.
     */
    public static List<Character> findAll(Connection conn, Map<String, Weapon> weaponByName)
            throws SQLException {
        List<Character> characters = new ArrayList<>();

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM character")) {
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String description = rs.getString("description");
                int cuantity = rs.getInt("cuantity");

                Character character = new Character(name, description, new ImageIcon(),
                        cuantity, DEFAULT_MAX_WEAPON_SLOTS);

                for (Modifier m : ModifierDAO.findByCharacter(conn, id)) {
                    character.addModifier(m);
                }

                addStartWeapons(conn, id, character, weaponByName);

                characters.add(character);
            }
        }
        return characters;
    }

    private static void addStartWeapons(Connection conn, int characterId,
            Character character, Map<String, Weapon> weaponByName) throws SQLException {
        String sql = "SELECT w.name, csw.amount FROM character_start_weapon csw "
                + "JOIN weapon w ON w.id = csw.weapon_id "
                + "WHERE csw.character_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, characterId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String weaponName = rs.getString("name");
                    int amount = rs.getInt("amount");
                    Weapon weapon = weaponByName.get(weaponName);
                    if (weapon != null) {
                        character.addStartEffect(new StartWeaponEffect(weapon, amount));
                    }
                }
            }
        }
    }
}
