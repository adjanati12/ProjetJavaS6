package ui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import service.EtudiantService;

/**
 * Contrôleur du formulaire d'ajout d'un étudiant.
 */
public class AjouterEtudiantController {

    @FXML private TextField champNumero;
    @FXML private TextField champNom;
    @FXML private TextField champPrenom;
    @FXML private Label messageErreur;

    private EtudiantService service;
    private AccueilController accueilController;

    /** @param service le service étudiant */
    public void setService(EtudiantService service) {
        this.service = service;
    }

    /** @param accueilController le contrôleur de l'écran d'accueil */
    public void setAccueilController(AccueilController accueilController) {
        this.accueilController = accueilController;
    }

    /**
     * Valide et ajoute l'étudiant via le service (vérifie les doublons et champs vides).
     */
    @FXML
    public void ajouter() {
        String numero = champNumero.getText().trim();
        String nom = champNom.getText().trim();
        String prenom = champPrenom.getText().trim();

        try {
            service.ajouterEtudiant(numero, nom, prenom);
            accueilController.rafraichirListe();
            fermer();
        } catch (IllegalArgumentException e) {
            messageErreur.setText(e.getMessage());
        }
    }

    @FXML
    public void annuler() {
        fermer();
    }

    private void fermer() {
        ((Stage) champNumero.getScene().getWindow()).close();
    }
}