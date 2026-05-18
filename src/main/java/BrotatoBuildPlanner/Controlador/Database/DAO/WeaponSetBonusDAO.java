package BrotatoBuildPlanner.Controlador.Database.DAO;

import BrotatoBuildPlanner.Modelo.Modifier.Modifier;
import BrotatoBuildPlanner.Modelo.Weapon.WeaponSet;
import BrotatoBuildPlanner.Modelo.Weapon.WeaponSetBonus;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para leer los bonos de set de armas desde la BD.
 */
public class WeaponSetBonusDAO {

    public static List<WeaponSetBonus> findAll(Connection conn) throws SQLException {
        List<WeaponSetBonus> bonuses = new ArrayList<>();
        String sql = "SELECT wsb.set_type, wsb.required_amount, wsb.modifier_id "
                + "FROM weapon_set_bonus wsb";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String setTypeStr = rs.getString("set_type");
                int requiredAmount = rs.getInt("required_amount");
                int modifierId = rs.getInt("modifier_id");

                WeaponSet setType = parseWeaponSet(setTypeStr);
                Modifier modifier = ModifierDAO.findById(conn, modifierId);

                if (modifier != null) {
                    bonuses.add(new WeaponSetBonus(setType, requiredAmount, modifier));
                }
            }
        }
        return bonuses;
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
}
