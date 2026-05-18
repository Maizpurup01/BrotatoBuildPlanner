package BrotatoBuildPlanner.Controlador.Database.DAO;

import BrotatoBuildPlanner.Modelo.Item.Item;
import BrotatoBuildPlanner.Modelo.Item.ItemCategory;
import BrotatoBuildPlanner.Modelo.Item.ItemTier;
import BrotatoBuildPlanner.Modelo.Modifier.Modifier;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;

/**
 * DAO para leer items desde la BD.
 *
 * Nota: el esquema actual de la tabla item no almacena tier ni category;
 * se usan los valores por defecto COMMON / ITEM hasta que el esquema se extienda.
 */
public class ItemDAO {

    public static List<Item> findAll(Connection conn) throws SQLException {
        List<Item> items = new ArrayList<>();
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM item")) {
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String description = rs.getString("description");
                int cuantity = rs.getInt("cuantity");

                Item item = new Item(name, description, new ImageIcon(), cuantity,
                        ItemTier.COMMON, ItemCategory.ITEM, cuantity);

                for (Modifier m : ModifierDAO.findByItem(conn, id)) {
                    item.addModifier(m);
                }

                items.add(item);
            }
        }
        return items;
    }
}
