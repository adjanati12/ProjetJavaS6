package ui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Etudiant;
import model.Inscription;
import model.Parcours;
import model.UE;
import service.EtudiantService;

import java.util.List;
import java.util.Optional;

/**
 * Controleur de la fiche detail d'un etudiant.
 */
public class DetailEtudiantController {

    @FXML private Label labelNom;
    @FXML private Label labelNumero;
    @FXML private Label labelMention;
    @FXML private Label labelParcours;
    @FXML private Label labelSemestre;
    @FXML private Label labelEcts;
    @FXML private Label labelPourcentage;
    @FXML private Label labelEctsSemestre;
    @FXML private ProgressBar progressDiplome;

    @FXML private ListView<String> listeUeEnCours;
    @FXML private ListView<String> listeUeValidees;
    @FXML private ListView<String> listeUEDisponibles;
    @FXML private ListView<String> listeHistorique;

    private Etudiant etudiant;
    private EtudiantService service;

    /**
     * Initialise la fiche avec l'etudiant et le service.
     * @param etudiant l'etudiant a afficher
     * @param service le service de gestion
     */
    public void setEtudiant(Etudiant etudiant, EtudiantService service) {
        this.etudiant = etudiant;
        this.service = service;
        afficherInfos();
        rafraichirInscriptions();
    }

    /** Affiche les informations generales de l'etudiant */
    private void afficherInfos() {
        labelNom.setText(etudiant.getNomComplet());
        labelNumero.setText(etudiant.getNumero());
        labelSemestre.setText(service.getSemestreCourant() + " " + service.getAnneeCourante());

        Parcours p = etudiant.getParcours();
        if (p != null) {
            labelParcours.setText(p.getCode());
            labelMention.setText(p.getMention() != null ? p.getMention().getCode() : "-");
        } else {
            labelParcours.setText("-");
            labelMention.setText("-");
        }

        int ects = etudiant.calculerECTSValides();
        labelEcts.setText(ects + " / 180");
        double pourcentage = Math.min((double) ects / 180.0, 1.0);
        progressDiplome.setProgress(pourcentage);
        labelPourcentage.setText((int)(pourcentage * 100) + "% du diplome");
    }

    /** Rafraichit toutes les listes d'UE */
    public void rafraichirInscriptions() {
        listeUeEnCours.getItems().clear();
        listeUeValidees.getItems().clear();
        listeHistorique.getItems().clear();
        listeUEDisponibles.getItems().clear();

        int ectsSemestre = 0;

        for (Inscription ins : etudiant.getInscriptions()) {
            boolean estCourant = ins.getAnneeUniversitaire().equals(service.getAnneeCourante())
                    && ins.getSemestre() == service.getSemestreCourant();

            String ligne = ins.getUe().getNom() + " (" + ins.getUe().getCode() + ") - "
                    + ins.getUe().getEcts() + " ECTS";

            if (estCourant && !ins.isValide() && !ins.isEchouee()) {
                // UE en cours ce semestre
                listeUeEnCours.getItems().add(ligne + " - En cours");
                ectsSemestre += ins.getUe().getEcts();
            } else if (ins.isValide()) {
                // UE validee
                listeUeValidees.getItems().add(ligne + " - Validee");
            }

            // Historique : toutes les inscriptions
            String statut = ins.isValide() ? "Validee" : ins.isEchouee() ? "Echouee" : "En cours";
            listeHistorique.getItems().add(
                    ins.getAnneeUniversitaire() + " " + ins.getSemestre() + " - " + ligne + " - " + statut
            );
        }

        // UE disponibles
        List<UE> disponibles = service.getUesDisponibles(etudiant);
        for (UE ue : disponibles) {
            listeUEDisponibles.getItems().add(ue.getNom() + " (" + ue.getCode() + ") - " + ue.getEcts() + " ECTS");
        }
        if (disponibles.isEmpty()) {
            listeUEDisponibles.getItems().add("Aucune UE disponible");
        }

        // Label ECTS semestre avec warning
        String msgEcts = ectsSemestre + " ECTS inscrits ce semestre (max : 30, derogation : 39)";
        if (ectsSemestre > 39) {
            labelEctsSemestre.setStyle("-fx-background-color: #ffebee; -fx-padding: 6 12; " +
                    "-fx-background-radius: 5; -fx-font-size: 12; " +
                    "-fx-text-fill: #c62828; -fx-font-weight: bold;");
        } else if (ectsSemestre > 30) {
            labelEctsSemestre.setStyle("-fx-background-color: #fff3e0; -fx-padding: 6 12; " +
                    "-fx-background-radius: 5; -fx-font-size: 12; " +
                    "-fx-text-fill: #e65100; -fx-font-weight: bold;");
        } else {
            labelEctsSemestre.setStyle("-fx-background-color: #e3f2fd; -fx-padding: 6 12; " +
                    "-fx-background-radius: 5; -fx-font-size: 12; " +
                    "-fx-text-fill: #1565c0; -fx-font-weight: bold;");
        }
        labelEctsSemestre.setText(msgEcts);

        // Mettre a jour la progression
        afficherInfos();
    }

    /** Ouvre la fenetre d'inscription a une UE */
    @FXML
    public void ajouterInscription() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/inscrireEtudiant.fxml"));
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Inscrire - " + etudiant.getNomComplet());
            stage.setScene(new Scene(loader.load()));
            InscriptionController ctrl = loader.getController();
            ctrl.setDonnees(etudiant, service, this);
            stage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** Valide ou echoue une UE selectionnee dans la liste en cours */
    @FXML
    public void validerUE() {
        int index = listeUeEnCours.getSelectionModel().getSelectedIndex();
        if (index < 0) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText("Selectionnez une UE en cours dans la liste.");
            alert.showAndWait();
            return;
        }

        // Trouver l'inscription correspondante
        int i = 0;
        Inscription insSelectionnee = null;
        for (Inscription ins : etudiant.getInscriptions()) {
            boolean estCourant = ins.getAnneeUniversitaire().equals(service.getAnneeCourante())
                    && ins.getSemestre() == service.getSemestreCourant()
                    && !ins.isValide() && !ins.isEchouee();
            if (estCourant) {
                if (i == index) {
                    insSelectionnee = ins;
                    break;
                }
                i++;
            }
        }

        if (insSelectionnee == null) return;

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Resultat UE");
        confirm.setHeaderText(insSelectionnee.getUe().getNom());
        confirm.setContentText("Quel est le resultat pour cette UE ?");
        ButtonType btnValider = new ButtonType("Validee");
        ButtonType btnEchouer = new ButtonType("Echouee");
        ButtonType btnAnnuler = new ButtonType("Annuler", ButtonBar.ButtonData.CANCEL_CLOSE);
        confirm.getButtonTypes().setAll(btnValider, btnEchouer, btnAnnuler);

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == btnValider) {
            insSelectionnee.setValide(true);
            rafraichirInscriptions();
        } else if (result.isPresent() && result.get() == btnEchouer) {
            insSelectionnee.setEchouee(true);
            rafraichirInscriptions();
        }
    }

    /** Ouvre le formulaire de modification de l'etudiant */
    @FXML
    public void ouvrirModification() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/modifierEtudiant.fxml"));
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Modifier - " + etudiant.getNomComplet());
            stage.setScene(new Scene(loader.load()));
            ModifierEtudiantController ctrl = loader.getController();
            ctrl.setEtudiant(etudiant, null);
            stage.showAndWait();
            afficherInfos();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** Ferme la fenetre */
    @FXML
    public void fermer() {
        ((Stage) labelNom.getScene().getWindow()).close();
    }
}