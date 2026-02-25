package ui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Etudiant;
import service.EtudiantService;

public class AjouterEtudiantController {

    @FXML private TextField champNumero;
    @FXML private TextField champNom;
    @FXML private TextField champPrenom;
    @FXML private Label messageErreur;

    private EtudiantService service;
    private AccueilController accueilController;

    public void setService(EtudiantService service) {
        this.service = service;
    }

    public void setAccueilController(AccueilController accueilController) {
        this.accueilController = accueilController;
    }

    @FXML
    public void ajouter() {
        String numero = champNumero.getText().trim();
        String nom = champNom.getText().trim();
        String prenom = champPrenom.getText().trim();

        if (numero.isEmpty() || nom.isEmpty() || prenom.isEmpty()) {
            messageErreur.setText("Tous les champs sont obligatoires.");
            return;
        }

        Etudiant e = new Etudiant(numero, nom, prenom);
        service.ajouterEtudiant(e);
        accueilController.rafraichirListe();
        fermer();
    }

    @FXML
    public void annuler() {
        fermer();
    }

    private void fermer() {
        ((Stage) champNumero.getScene().getWindow()).close();
    }
}
