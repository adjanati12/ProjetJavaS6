package ui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Etudiant;
import service.EtudiantService;

public class AccueilController {

    @FXML
    private ListView<String> listeEtudiants;

    private EtudiantService service = new EtudiantService();

    @FXML
    public void initialize() {
        service.ajouterEtudiant(new Etudiant("12345", "Bakhoum", "Habybatou"));
        service.ajouterEtudiant(new Etudiant("67890", "Djanati", "Aya"));
        service.ajouterEtudiant(new Etudiant("11111", "Abdelkerim", "Safia"));
        rafraichirListe();
    }

    @FXML
    public void ouvrirFormulaireAjout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ajouterEtudiant.fxml"));
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Ajouter un étudiant");
            stage.setScene(new Scene(loader.load()));
            AjouterEtudiantController ctrl = loader.getController();
            ctrl.setService(service);
            ctrl.setAccueilController(this);
            stage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void rafraichirListe() {
        listeEtudiants.getItems().clear();
        for (Etudiant e : service.getEtudiants()) {
            listeEtudiants.getItems().add(e.getNumero() + " - " + e.getNomComplet());
        }
    }
}
