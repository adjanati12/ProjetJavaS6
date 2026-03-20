package ui;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.Mention;
import model.Parcours;
import model.UE;
import service.EtudiantService;

/**
 * Controleur de l'ecran UE et Formations (lecture seule).
 */
public class UEFormationsController {

    @FXML private ComboBox<Mention> comboMention;
    @FXML private ComboBox<Parcours> comboParcours;
    @FXML private TableView<UE> tableUE;
    @FXML private TableColumn<UE, String> colCode;
    @FXML private TableColumn<UE, String> colNom;
    @FXML private TableColumn<UE, String> colEcts;
    @FXML private TableColumn<UE, String> colPrerequis;
    @FXML private TableColumn<UE, String> colObligatoire;

    private EtudiantService service;

    /**
     * Initialise le controleur avec le service.
     * @param service le service etudiant
     */
    public void setService(EtudiantService service) {
        this.service = service;
        configurerColonnes();
        chargerMentions();
        rafraichirTableau();
    }

    /** Configure les colonnes du tableau */
    private void configurerColonnes() {
        colCode.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getCode()));
        colNom.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getNom()));
        colEcts.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getEcts() + " ECTS"));
        colPrerequis.setCellValueFactory(data -> {
            if (data.getValue().getPrerequis().isEmpty()) {
                return new javafx.beans.property.SimpleStringProperty("-");
            }
            StringBuilder sb = new StringBuilder();
            for (UE pre : data.getValue().getPrerequis()) {
                if (sb.length() > 0) sb.append(", ");
                sb.append(pre.getCode());
            }
            return new javafx.beans.property.SimpleStringProperty(sb.toString());
        });
        colObligatoire.setCellValueFactory(data -> {
            Parcours p = comboParcours.getValue();
            if (p == null) return new javafx.beans.property.SimpleStringProperty("-");
            boolean obligatoire = p.getUesObligatoires().contains(data.getValue());
            return new javafx.beans.property.SimpleStringProperty(obligatoire ? "Oui" : "Non");
        });
    }

    /** Charge les mentions dans le ComboBox */
    private void chargerMentions() {
        comboMention.getItems().addAll(service.getMentions());
        comboMention.getItems().add(0, null);
        comboMention.setPromptText("Toutes les mentions");

        comboMention.setOnAction(e -> {
            Mention m = comboMention.getValue();
            comboParcours.getItems().clear();
            comboParcours.getItems().add(null);
            if (m != null) {
                comboParcours.getItems().addAll(m.getParcours());
            } else {
                comboParcours.getItems().addAll(service.getParcours());
            }
            comboParcours.getSelectionModel().selectFirst();
            rafraichirTableau();
        });

        comboParcours.getItems().add(null);
        comboParcours.getItems().addAll(service.getParcours());
        comboParcours.setPromptText("Tous les parcours");
        comboParcours.setOnAction(e -> rafraichirTableau());
    }

    /** Rafraichit le tableau selon les filtres */
    @FXML
    public void rafraichirTableau() {
        tableUE.getItems().clear();
        Mention mention = comboMention.getValue();
        Parcours parcours = comboParcours.getValue();

        for (UE ue : service.getUes()) {
            // Filtre mention
            if (mention != null && !mention.equals(ue.getMention())) continue;
            // Filtre parcours : montrer les UE de la mention du parcours
            if (parcours != null && ue.getMention() != null
                    && !ue.getMention().equals(parcours.getMention())) continue;
            tableUE.getItems().add(ue);
        }

        // Rafraichir la colonne obligatoire
        tableUE.refresh();
    }

    /** Ferme la fenetre */
    @FXML
    public void fermer() {
        ((Stage) tableUE.getScene().getWindow()).close();
    }
}