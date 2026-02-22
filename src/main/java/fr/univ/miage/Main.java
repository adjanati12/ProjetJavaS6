package fr.univ.miage;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/accueil.fxml"));
        Scene scene = new Scene(loader.load(), 600, 400);
        stage.setTitle("Suivi des étudiants");
        stage.setScene(scene);
        stage.show();
        stage.toFront();
        stage.requestFocus();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
