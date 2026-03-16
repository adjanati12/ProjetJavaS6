package ui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Etudiant;

public class ModifierEtudiantController {

    @FXML private Label labelNumero;
    @FXML private TextField champNom;
    @FXML private TextField champPrenom;
    @FXML private Label messageErreur;

    private Etudiant etudiant;
    private AccueilController accueilController;

    public void setEtudiant(Etudiant etudiant, AccueilController accueilController) {
        this.etudiant = etudiant;
        this.accueilController = accueilController;
        labelNumero.setText(etudiant.getNumero());
        champNom.setText(etudiant.getNom());
        champPrenom.setText(etudiant.getPrenom());
    }

    @FXML
    public void enregistrer() {
        String nom = champNom.getText().trim();
        String prenom = champPrenom.getText().trim();
        if (nom.isEmpty() || prenom.isEmpty()) {
            messageErreur.setText("Tous les champs sont obligatoires.");
            return;
        }
        etudiant.setNom(nom);
        etudiant.setPrenom(prenom);
        accueilController.rafraichirListe();
        fermer();
    }

    @FXML
    public void annuler() {
        fermer();
    }

    private void fermer() {
        ((Stage) champNom.getScene().getWindow()).close();
    }
}
