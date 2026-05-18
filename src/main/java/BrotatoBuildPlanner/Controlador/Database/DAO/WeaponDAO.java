package BrotatoBuildPlanner.Controlador.Database.DAO;

import BrotatoBuildPlanner.Modelo.Modifier.Modifier;
import BrotatoBuildPlanner.Modelo.Weapon.Weapon;
import BrotatoBuildPlanner.Modelo.Weapon.WeaponSet;
import BrotatoBuildPlanner.Modelo.Weapon.WeaponType;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;

/**
 * DAO para leer armas desde la BD.
 */
public class WeaponDAO {

    public static List<Weapon> findAll(Connection conn) throws SQLException {
        List<Weapon> weapons = new ArrayList<>();
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM weapon")) {
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String description = rs.getString("description");
                int cuantity = rs.getInt("cuantity");
                WeaponSet set1 = parseWeaponSet(rs.getString("set1"));
                WeaponSet set2 = parseWeaponSet(rs.getString("set2"));
                WeaponType type = parseWeaponType(rs.getString("weapon_type"));
                int tier = rs.getInt("tier");
                double damage = rs.getDouble("damage");
                double attackSpeed = rs.getDouble("attack_speed");
                int range = rs.getInt("range");
                int lifesteal = rs.getInt("lifesteal");

                Weapon weapon = new Weapon(name, description, new ImageIcon(), cuantity,
                        set1, set2, type, tier, damage, attackSpeed, range, lifesteal);

                for (Modifier m : ModifierDAO.findByWeapon(conn, id)) {
                    weapon.addModifier(m);
                }

                weapons.add(weapon);
            }
        }
        return weapons;
    }

    private static WeaponSet parseWeaponSet(String value) {
        if (value == null || value.isBlank()) {
            return WeaponSet.NOTYPE;
        }
        try {
            return WeaponSet.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return WeaponSet.NOTYPE;
        }
    }

    private static WeaponType parseWeaponType(String value) {
        if (value == null || value.isBlank()) {
            return WeaponType.MELEE;
        }
        try {
            return WeaponType.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return WeaponType.MELEE;
        }
    }
}
