package BrotatoBuildPlanner.Controlador.Database.DAO;

import BrotatoBuildPlanner.Modelo.Condition.StatCondition;
import BrotatoBuildPlanner.Modelo.Condition.WeaponTypeCondition;
import BrotatoBuildPlanner.Modelo.Modifier.Modifier;
import BrotatoBuildPlanner.Modelo.Modifier.ModifierPriority;
import BrotatoBuildPlanner.Modelo.Modifier.ModifierType;
import BrotatoBuildPlanner.Modelo.Stats.Stat;
import BrotatoBuildPlanner.Modelo.Weapon.WeaponType;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para leer modificadores desde la BD y reconstruirlos como objetos Modifier.
 */
public class ModifierDAO {

    public static List<Modifier> findByCharacter(Connection conn, int characterId) throws SQLException {
        String sql = "SELECT m.* FROM modifier m "
                + "JOIN character_modifier cm ON cm.modifier_id = m.id "
                + "WHERE cm.character_id = ?";
        return query(conn, sql, characterId);
    }

    public static List<Modifier> findByItem(Connection conn, int itemId) throws SQLException {
        String sql = "SELECT m.* FROM modifier m "
                + "JOIN item_modifier im ON im.modifier_id = m.id "
                + "WHERE im.item_id = ?";
        return query(conn, sql, itemId);
    }

    public static List<Modifier> findByWeapon(Connection conn, int weaponId) throws SQLException {
        String sql = "SELECT m.* FROM modifier m "
                + "JOIN weapon_modifier wm ON wm.modifier_id = m.id "
                + "WHERE wm.weapon_id = ?";
        return query(conn, sql, weaponId);
    }

    public static Modifier findById(Connection conn, int modifierId) throws SQLException {
        String sql = "SELECT * FROM modifier WHERE id = ?";
        List<Modifier> list = query(conn, sql, modifierId);
        return list.isEmpty() ? null : list.get(0);
    }

    private static List<Modifier> query(Connection conn, String sql, int id) throws SQLException {
        List<Modifier> result = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(mapRow(rs));
                }
            }
        }
        return result;
    }

    static Modifier mapRow(ResultSet rs) throws SQLException {
        Stat stat = Stat.valueOf(rs.getString("stat"));
        double value = rs.getDouble("value");
        ModifierType type = ModifierType.valueOf(rs.getString("type"));
        ModifierPriority priority = ModifierPriority.valueOf(rs.getString("priority"));

        Modifier modifier = new Modifier(stat, value, type, priority);

        String conditionType = rs.getString("condition_type");
        if (conditionType != null) {
            switch (conditionType) {
                case "STAT" -> {
                    Stat condStat = Stat.valueOf(rs.getString("condition_stat"));
                    int condQty = rs.getInt("condition_quantity");
                    modifier.setCondition(new StatCondition(condStat, condQty));
                }
                case "WEAPON_TYPE" -> {
                    WeaponType condWeaponType = WeaponType.valueOf(rs.getString("condition_weapon_type"));
                    int condQty = rs.getInt("condition_quantity");
                    modifier.setCondition(new WeaponTypeCondition(condWeaponType, condQty));
                }
            }
        }

        return modifier;
    }
}
