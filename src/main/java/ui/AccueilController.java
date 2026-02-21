package ui;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import service.EtudiantService;
import model.Etudiant;

public class AccueilController {

    @FXML
    private ListView<String> listeEtudiants;

    private EtudiantService service = new EtudiantService();

    @FXML
    public void initialize() {
        rafraichirListe();
    }

    private void rafraichirListe() {
        listeEtudiants.getItems().clear();
        for (Etudiant e : service.getEtudiants()) {
            listeEtudiants.getItems().add(e.getNumero() + " - " + e.getNomComplet());
        }
    }
}