package dao;

import java.sql.DatabaseMetaData;

public class DatabaseConnection {
    private static final String URL      = "jdbc:oracle:thin:@telline.univ-tlse3.fr:1521:etupre";
    private static final String USER     = "DJW5221A";
    private static final String PASSWORD = "Beomgyu2005.";

    public static DatabaseMetaData getInstance() {
    }
}
