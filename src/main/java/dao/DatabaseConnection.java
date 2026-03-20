package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Connexion à la base de données Oracle (non utilisée pour l'instant).
 */
public class DatabaseConnection {

    private static DatabaseConnection instance;
    private Connection connection;

    private static final String URL      = "jdbc:oracle:thin:@telline.univ-tlse3.fr:1521:etupre";
    private static final String USER     = "DJW5221A";
    private static final String PASSWORD = "Beomgyu2005.";

    private DatabaseConnection() throws SQLException {
        connection = DriverManager.getConnection(URL, USER, PASSWORD);
    }

    /**
     * @return l'instance unique de la connexion
     * @throws SQLException si la connexion échoue
     */
    public static DatabaseConnection getInstance() throws SQLException {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    /** @return la connexion active */
    public Connection getConnection() {
        return connection;
    }
}