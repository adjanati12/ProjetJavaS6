package ui;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.Etudiant;
import model.Mention;
import model.Parcours;
import service.EtudiantService;

/**
 * Controleur du formulaire de modification d'un etudiant.
 */
public class ModifierEtudiantController {

    @FXML private Label labelNumero;
    @FXML private TextField champNom;
    @FXML private TextField champPrenom;
    @FXML private ComboBox<Mention> comboMention;
    @FXML private ComboBox<Parcours> comboParcours;
    @FXML private Label messageErreur;

    private Etudiant etudiant;
    private AccueilController accueilController;
    private EtudiantService service;

    /**
     * Initialise le formulaire avec l'etudiant a modifier.
     * @param etudiant l'etudiant a modifier
     * @param accueilController le controleur d'accueil pour rafraichir la liste
     */
    public void setEtudiant(Etudiant etudiant, AccueilController accueilController) {
        this.etudiant = etudiant;
        this.accueilController = accueilController;
        if (accueilController != null) {
            this.service = accueilController.getService();
            chargerMentions();
        }
        remplirFormulaire();
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

    /** Remplit le formulaire avec les donnees de l'etudiant */
    private void remplirFormulaire() {
        labelNumero.setText(etudiant.getNumero());
        champNom.setText(etudiant.getNom());
        champPrenom.setText(etudiant.getPrenom());

        // Pre-selectionner la mention et le parcours actuels
        Parcours p = etudiant.getParcours();
        if (p != null && service != null) {
            Mention m = p.getMention();
            comboMention.setValue(m);
            // Charger les parcours de cette mention
            comboParcours.getItems().clear();
            if (m != null) {
                comboParcours.getItems().addAll(m.getParcours());
            }
            comboParcours.setValue(p);
        }
    }

    /** Enregistre les modifications */
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

        Parcours parcours = comboParcours.getValue();
        if (parcours != null) {
            etudiant.setParcours(parcours);
        }

        if (accueilController != null) {
            accueilController.rafraichirListe();
        }
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