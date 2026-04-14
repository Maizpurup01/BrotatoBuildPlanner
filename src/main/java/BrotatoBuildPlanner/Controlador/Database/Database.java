package BrotatoBuildPlanner.Controlador.Database;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Clase que gestiona la conexion con la base de datos en local
 *
 * @author Manuel
 */
public class Database {

    private static final String URL = "jdbc:sqlite:data/brotato.db";

    public static Connection connect() {
        File f = new File("data");
        if (!f.exists()) {
            f.mkdir();
        }
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(URL);
            System.out.println("Conectado a SQLite");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return conn;
    }

    public static Connection release(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }
        return conn;
    }
}
