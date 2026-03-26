import dao.DatabaseConnection;
import java.sql.Connection;

public class TestConnectionJava {
    public static void main(String[] args) throws Exception {
        Connection conn = DatabaseConnection.getInstance().getConnection();
        System.out.println("Connexion OK : " + conn.getMetaData().getURL());
        conn.close();
    }
}