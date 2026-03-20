package ui;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import java.util.ArrayList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import model.Etudiant;
import model.Inscription;
import model.Parcours;
import model.UE;
import service.EtudiantService;

import java.util.ArrayList;
import java.util.List;

/**
 * Controleur de l'ecran de saisie des resultats et inscription en masse.
 */
public class SaisieResultatController {

    @FXML private ComboBox<String> filtreUE;
    @FXML private ComboBox<String> filtreParcours;
    @FXML private ComboBox<String> filtreResultat;
    @FXML private TableView<LigneResultat> tableau;
    @FXML private TableColumn<LigneResultat, String> colEtudiant;
    @FXML private TableColumn<LigneResultat, String> colParcours;
    @FXML private TableColumn<LigneResultat, String> colUE;
    @FXML private TableColumn<LigneResultat, String> colEcts;
    @FXML private TableColumn<LigneResultat, Void> colResultat;
    @FXML private Label labelWarning;

    private EtudiantService service;
    private boolean modifNonSauvegardee = false;

    /**
     * Classe interne representant une ligne du tableau.
     */
    public static class LigneResultat {
        private Etudiant etudiant;
        private Inscription inscription;

        public LigneResultat(Etudiant etudiant, Inscription inscription) {
            this.etudiant = etudiant;
            this.inscription = inscription;
        }

        public Etudiant getEtudiant() { return etudiant; }
        public Inscription getInscription() { return inscription; }
        public String getNomEtudiant() { return etudiant.getNomComplet(); }
        public String getParcours() {
            return etudiant.getParcours() != null ? etudiant.getParcours().getCode() : "-";
        }
        public String getUE() { return inscription.getUe().getNom() + " (" + inscription.getUe().getCode() + ")"; }
        public String getEcts() { return inscription.getUe().getEcts() + " ECTS"; }
    }

    /**
     * Initialise le controleur avec le service.
     * @param service le service etudiant
     */
    public void setService(EtudiantService service) {
        this.service = service;
        configurerFiltres();
        configurerColonnes();
        rafraichirTableau();
    }

    /** Configure les filtres */
    private void configurerFiltres() {
        // Filtre UE
        filtreUE.getItems().add("Toutes les UE");
        for (UE ue : service.getUes()) {
            filtreUE.getItems().add(ue.getCode() + " - " + ue.getNom());
        }
        filtreUE.getSelectionModel().selectFirst();

        // Filtre Parcours
        filtreParcours.getItems().add("Tous les parcours");
        for (Parcours p : service.getParcours()) {
            filtreParcours.getItems().add(p.getCode());
        }
        filtreParcours.getSelectionModel().selectFirst();

        // Filtre Resultat
        filtreResultat.getItems().addAll("Tous", "En attente", "Valide", "Echoue");
        filtreResultat.getSelectionModel().selectFirst();
    }

    /** Configure les colonnes du tableau */
    private void configurerColonnes() {
        colEtudiant.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getNomEtudiant()));
        colParcours.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getParcours()));
        colUE.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getUE()));
        colEcts.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getEcts()));

        // Colonne avec boutons Valider / Echouer
        colResultat.setCellFactory(col -> new TableCell<>() {
            private final Button btnValider = new Button("Valider");
            private final Button btnEchouer = new Button("Echouer");
            private final HBox box = new HBox(6, btnValider, btnEchouer);

            {
                btnValider.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; " +
                        "-fx-font-size: 11; -fx-background-radius: 4; -fx-padding: 3 8;");
                btnEchouer.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; " +
                        "-fx-font-size: 11; -fx-background-radius: 4; -fx-padding: 3 8;");

                btnValider.setOnAction(e -> {
                    LigneResultat ligne = getTableView().getItems().get(getIndex());
                    ligne.getInscription().setValide(true);
                    modifNonSauvegardee = true;
                    afficherWarning();
                    rafraichirTableau();
                });

                btnEchouer.setOnAction(e -> {
                    LigneResultat ligne = getTableView().getItems().get(getIndex());
                    ligne.getInscription().setEchouee(true);
                    modifNonSauvegardee = true;
                    afficherWarning();
                    rafraichirTableau();
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    LigneResultat ligne = getTableView().getItems().get(getIndex());
                    Inscription ins = ligne.getInscription();
                    if (ins.isValide()) {
                        btnValider.setDisable(true);
                        btnEchouer.setDisable(true);
                        btnValider.setText("Valide");
                    } else if (ins.isEchouee()) {
                        btnValider.setDisable(true);
                        btnEchouer.setDisable(true);
                        btnEchouer.setText("Echoue");
                    } else {
                        btnValider.setDisable(false);
                        btnEchouer.setDisable(false);
                        btnValider.setText("Valider");
                        btnEchouer.setText("Echouer");
                    }
                    setGraphic(box);
                }
            }
        });
    }

    /** Rafraichit le tableau selon les filtres */
    @FXML
    public void rafraichirTableau() {
        tableau.getItems().clear();
        String filtreUEVal = filtreUE.getValue();
        String filtreParcoursVal = filtreParcours.getValue();
        String filtreResultatVal = filtreResultat.getValue();

        for (Etudiant etudiant : service.getEtudiants()) {
            // Filtre parcours
            if (filtreParcoursVal != null && !filtreParcoursVal.equals("Tous les parcours")) {
                if (etudiant.getParcours() == null ||
                        !etudiant.getParcours().getCode().equals(filtreParcoursVal)) continue;
            }

            for (Inscription ins : etudiant.getInscriptions()) {
                // Seulement les inscriptions du semestre courant
                if (!ins.getAnneeUniversitaire().equals(service.getAnneeCourante()) ||
                        ins.getSemestre() != service.getSemestreCourant()) continue;

                // Filtre UE
                if (filtreUEVal != null && !filtreUEVal.equals("Toutes les UE")) {
                    String codeUE = filtreUEVal.split(" - ")[0];
                    if (!ins.getUe().getCode().equals(codeUE)) continue;
                }

                // Filtre resultat
                if (filtreResultatVal != null && !filtreResultatVal.equals("Tous")) {
                    if (filtreResultatVal.equals("Valide") && !ins.isValide()) continue;
                    if (filtreResultatVal.equals("Echoue") && !ins.isEchouee()) continue;
                    if (filtreResultatVal.equals("En attente") && (ins.isValide() || ins.isEchouee())) continue;
                }

                tableau.getItems().add(new LigneResultat(etudiant, ins));
            }
        }
    }

    /** Valide toutes les UE affichees */
    @FXML
    public void toutValider() {
        for (LigneResultat ligne : tableau.getItems()) {
            ligne.getInscription().setValide(true);
        }
        modifNonSauvegardee = true;
        afficherWarning();
        rafraichirTableau();
    }

    /** Echoue toutes les UE affichees */
    @FXML
    public void toutEchouer() {
        for (LigneResultat ligne : tableau.getItems()) {
            ligne.getInscription().setEchouee(true);
        }
        modifNonSauvegardee = true;
        afficherWarning();
        rafraichirTableau();
    }

    /** Affiche le warning de modifications non sauvegardees */
    private void afficherWarning() {
        if (modifNonSauvegardee) {
            labelWarning.setText("Resultats non enregistres. N'oubliez pas de sauvegarder.");
            labelWarning.setStyle("-fx-background-color: #fff3e0; -fx-text-fill: #e65100; " +
                    "-fx-font-weight: bold; -fx-padding: 6 12; -fx-background-radius: 5;");
            labelWarning.setVisible(true);
        }
    }

    /** Sauvegarde (pour l'instant juste masque le warning) */
    @FXML
    public void sauvegarder() {
        modifNonSauvegardee = false;
        labelWarning.setVisible(false);
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Sauvegarde");
        alert.setContentText("Resultats sauvegardes avec succes !");
        alert.showAndWait();
    }

    /** Ferme la fenetre */
    @FXML
    public void fermer() {
        ((Stage) tableau.getScene().getWindow()).close();
    }
    /** Ouvre la fenetre d'inscription en masse a une UE */
    @FXML
    public void ouvrirInscriptionMasse() {
        // Choisir une UE
        ChoiceDialog<UE> dialogUE = new ChoiceDialog<>(service.getUes().get(0), service.getUes());
        dialogUE.setTitle("Inscription en masse");
        dialogUE.setHeaderText("Choisir une UE");
        dialogUE.setContentText("UE :");
        dialogUE.showAndWait().ifPresent(ue -> {
            // Choisir les etudiants
            List<Etudiant> etudiantsDisponibles = new ArrayList<>();
            for (Etudiant e : service.getEtudiants()) {
                if (service.peutSInscrire(e, ue) && !service.estDejaInscrit(e, ue)) {
                    etudiantsDisponibles.add(e);
                }
            }

            if (etudiantsDisponibles.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Inscription en masse");
                alert.setContentText("Aucun etudiant disponible pour cette UE.");
                alert.showAndWait();
                return;
            }

            // Afficher une liste avec cases a cocher
            Dialog<List<Etudiant>> dialog = new Dialog<>();
            dialog.setTitle("Inscription en masse - " + ue.getNom());
            dialog.setHeaderText("Selectionnez les etudiants a inscrire :");

            ButtonType btnConfirmer = new ButtonType("Confirmer", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(btnConfirmer, ButtonType.CANCEL);

            VBox vbox = new VBox(8);
            vbox.setStyle("-fx-padding: 15;");
            List<CheckBox> checkBoxes = new ArrayList<>();
            for (Etudiant e : etudiantsDisponibles) {
                CheckBox cb = new CheckBox(e.getNomComplet() + " (" + e.getNumero() + ")");
                checkBoxes.add(cb);
                vbox.getChildren().add(cb);
            }

            ScrollPane scroll = new ScrollPane(vbox);
            scroll.setPrefHeight(300);
            dialog.getDialogPane().setContent(scroll);

            dialog.setResultConverter(btn -> {
                if (btn == btnConfirmer) {
                    List<Etudiant> selectionnes = new ArrayList<>();
                    for (int i = 0; i < checkBoxes.size(); i++) {
                        if (checkBoxes.get(i).isSelected()) {
                            selectionnes.add(etudiantsDisponibles.get(i));
                        }
                    }
                    return selectionnes;
                }
                return null;
            });

            dialog.showAndWait().ifPresent(selectionnes -> {
                int nb = 0;
                for (Etudiant e : selectionnes) {
                    service.inscrire(e, ue, service.getAnneeCourante(), service.getSemestreCourant());
                    nb++;
                }
                if (nb > 0) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Inscription en masse");
                    alert.setContentText(nb + " etudiant(s) inscrits a " + ue.getNom() + " !");
                    alert.showAndWait();
                    rafraichirTableau();
                }
            });
        });
    }
}