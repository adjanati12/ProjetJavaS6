package ui;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.Mention;
import model.Parcours;
import service.EtudiantService;

/**
 * Controleur du formulaire d'ajout d'un etudiant.
 */
public class AjouterEtudiantController {

    @FXML private TextField champNumero;
    @FXML private TextField champNom;
    @FXML private TextField champPrenom;
    @FXML private ComboBox<Mention> comboMention;
    @FXML private ComboBox<Parcours> comboParcours;
    @FXML private Label messageErreur;

    private EtudiantService service;
    private AccueilController accueilController;

    /** @param service le service etudiant */
    public void setService(EtudiantService service) {
        this.service = service;
        chargerMentions();
    }

    /** @param accueilController le controleur de l'ecran d'accueil */
    public void setAccueilController(AccueilController accueilController) {
        this.accueilController = accueilController;
    }

    /** Charge les mentions dans le ComboBox */
    private void chargerMentions() {
        comboMention.getItems().addAll(service.getMentions());

        // Quand on change de mention, on met a jour les parcours
        comboMention.setOnAction(e -> {
            Mention mentionChoisie = comboMention.getValue();
            comboParcours.getItems().clear();
            if (mentionChoisie != null) {
                comboParcours.getItems().addAll(mentionChoisie.getParcours());
            }
            comboParcours.getSelectionModel().selectFirst();
        });
    }

    /**
     * Valide et ajoute l'etudiant via le service.
     */
    @FXML
    public void ajouter() {
        String numero = champNumero.getText().trim();
        String nom = champNom.getText().trim();
        String prenom = champPrenom.getText().trim();
        Parcours parcours = comboParcours.getValue();

        if (parcours == null) {
            messageErreur.setText("Veuillez choisir un parcours.");
            return;
        }

        try {
            service.ajouterEtudiant(numero, nom, prenom, parcours);
            accueilController.rafraichirListe();
            fermer();
        } catch (IllegalArgumentException ex) {
            messageErreur.setText(ex.getMessage());
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