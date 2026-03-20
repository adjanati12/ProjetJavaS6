package fr.univ.miage;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
public class Main extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/accueil.fxml"));
        Scene scene = new Scene(loader.load(), 800, 600);
        stage.setTitle("Suivi des etudiants");
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
        stage.toFront();
        stage.requestFocus();
    }
    public static void main(String[] args) {

        launch(args);
    }
}
