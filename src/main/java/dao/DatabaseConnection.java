package dao;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static DatabaseConnection instance;
    private Connection connection;

    private static final String URL      = "jdbc:oracle:thin:@telline.univ-tlse3.fr:1521:etupre";
    private static final String USER     = "DJW5221A";
    private static final String PASSWORD = "Beomgyu2005.";

<<<<<<< HEAD
    public static DatabaseMetaData getInstance() {
        return null;
=======
    private DatabaseConnection() throws SQLException {
        connection = DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static DatabaseConnection getInstance() throws SQLException {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
>>>>>>> 2c3b101d9db5ee65f261e2625f82b0a4a2d8b349
    }
}
