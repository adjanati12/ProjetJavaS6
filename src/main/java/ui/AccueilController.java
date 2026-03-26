package ui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Etudiant;
import model.Mention;
import model.Parcours;
import service.CsvLoader;
import service.EtudiantService;
import dao.DatabaseConnection;
import java.sql.Connection;
import java.sql.SQLException;

import java.util.List;
import java.util.Optional;

/**
 * Contrôleur de l'écran d'accueil.
 */
public class AccueilController {

    @FXML private TableView<Etudiant> tableEtudiants;
    @FXML private TableColumn<Etudiant, String> colNumero;
    @FXML private TableColumn<Etudiant, String> colNom;
    @FXML private TableColumn<Etudiant, String> colPrenom;
    @FXML private TableColumn<Etudiant, String> colParcours;
    @FXML private TableColumn<Etudiant, String> colEcts;
    @FXML private TableColumn<Etudiant, String> colUeEnCours;
    @FXML private TableColumn<Etudiant, Void> colActions;

    @FXML private ComboBox<String> filtresMention;
    @FXML private ComboBox<String> filtresParcours;
    @FXML private TextField champRecherche;

    @FXML private Label labelSemestre;
    @FXML private Label labelTotalEtudiants;
    @FXML private Label labelStatMiage;
    @FXML private Label labelStatIO;
    @FXML private Label labelDiplomes;
    @FXML private Label labelNbResultats;

    private EtudiantService service = new EtudiantService();

    @FXML
    public void initialize() {
        try {
            service.chargerDepuisBDD();
            System.out.println("Donnees chargees depuis Oracle !");
        } catch (SQLException e) {
            System.err.println("Erreur chargement BDD : " + e.getMessage());
            // Repli sur CSV si la BDD est inaccessible
            CsvLoader.chargerMentions(service);
            CsvLoader.chargerUEs(service);
            CsvLoader.chargerParcours(service);
            CsvLoader.chargerEtudiants(service);
            CsvLoader.chargerInscriptions(service);
        }

        configurerColonnes();
        configurerFiltres();
        rafraichirListe();
        mettreAJourLabelSemestre();
    }

    /** Configure les colonnes du tableau */
    private void configurerColonnes() {
        colNumero.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getNumero()));
        colNom.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getNom()));
        colPrenom.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getPrenom()));
        colParcours.setCellValueFactory(data -> {
            Parcours p = data.getValue().getParcours();
            return new javafx.beans.property.SimpleStringProperty(p != null ? p.getCode() : "-");
        });
        colEcts.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().calculerECTSValides() + " ECTS"));
        colUeEnCours.setCellValueFactory(data -> {
            long nb = data.getValue().getInscriptions().stream()
                    .filter(i -> i.getAnneeUniversitaire().equals(service.getAnneeCourante())
                            && i.getSemestre() == service.getSemestreCourant()
                            && !i.isValide())
                    .count();
            return new javafx.beans.property.SimpleStringProperty(nb + " UE");
        });

        // Colonne Actions avec boutons
        colActions.setCellFactory(col -> new TableCell<>() {
            private final Button btnVoir = new Button("Voir");
            private final Button btnModifier = new Button("Modifier");
            private final HBox box = new HBox(6, btnVoir, btnModifier);

            {
                btnVoir.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; " +
                        "-fx-font-size: 11; -fx-background-radius: 4; -fx-padding: 3 8;");
                btnModifier.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white; " +
                        "-fx-font-size: 11; -fx-background-radius: 4; -fx-padding: 3 8;");

                btnVoir.setOnAction(e -> {
                    Etudiant etudiant = getTableView().getItems().get(getIndex());
                    ouvrirDetails(etudiant);
                });
                btnModifier.setOnAction(e -> {
                    Etudiant etudiant = getTableView().getItems().get(getIndex());
                    ouvrirModification(etudiant);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
            }
        });
    }

    /** Configure les filtres mention et parcours */
    private void configurerFiltres() {
        filtresMention.getItems().add("Toutes les mentions");
        for (Mention m : service.getMentions()) {
            filtresMention.getItems().add(m.getCode());
        }
        filtresMention.getSelectionModel().selectFirst();

        filtresParcours.getItems().add("Tous les parcours");
        for (Parcours p : service.getParcours()) {
            filtresParcours.getItems().add(p.getCode());
        }
        filtresParcours.getSelectionModel().selectFirst();

        // Mettre à jour les parcours selon la mention choisie
        filtresMention.setOnAction(e -> {
            String mentionChoisie = filtresMention.getValue();
            filtresParcours.getItems().clear();
            filtresParcours.getItems().add("Tous les parcours");
            if (mentionChoisie == null || mentionChoisie.equals("Toutes les mentions")) {
                for (Parcours p : service.getParcours()) {
                    filtresParcours.getItems().add(p.getCode());
                }
            } else {
                Mention m = service.getMentionParCode(mentionChoisie);
                if (m != null) {
                    for (Parcours p : m.getParcours()) {
                        filtresParcours.getItems().add(p.getCode());
                    }
                }
            }
            filtresParcours.getSelectionModel().selectFirst();
        });
    }

    /** Applique les filtres et met à jour le tableau */
    @FXML
    public void appliquerFiltres() {
        String mentionChoisie = filtresMention.getValue();
        String parcoursChoisi = filtresParcours.getValue();
        String recherche = champRecherche.getText().trim().toLowerCase();

        List<Etudiant> resultats = service.getEtudiants();

        // Filtre mention
        if (mentionChoisie != null && !mentionChoisie.equals("Toutes les mentions")) {
            Mention m = service.getMentionParCode(mentionChoisie);
            resultats = service.getEtudiantsParMention(m);
        }

        // Filtre parcours
        if (parcoursChoisi != null && !parcoursChoisi.equals("Tous les parcours")) {
            Parcours p = service.getParcoursParCode(parcoursChoisi);
            resultats = service.getEtudiantsParParcours(p);
        }

        // Filtre recherche texte
        if (!recherche.isEmpty()) {
            final List<Etudiant> base = resultats;
            resultats = base.stream()
                    .filter(e -> e.getNom().toLowerCase().contains(recherche)
                            || e.getPrenom().toLowerCase().contains(recherche)
                            || e.getNumero().toLowerCase().contains(recherche))
                    .toList();
        }

        tableEtudiants.getItems().setAll(resultats);
        labelNbResultats.setText(resultats.size() + " étudiant(s) trouvé(s)");
    }

    /** Réinitialise les filtres */
    @FXML
    public void reinitialiserFiltres() {
        filtresMention.getSelectionModel().selectFirst();
        filtresParcours.getSelectionModel().selectFirst();
        champRecherche.clear();
        rafraichirListe();
    }

    /** Met à jour le label semestre */
    private void mettreAJourLabelSemestre() {
        labelSemestre.setText(service.getAnneeCourante() + " - Semestre " + service.getSemestreCourant());
    }

    /** Rafraîchit le tableau et les statistiques */
    public void rafraichirListe() {
        tableEtudiants.getItems().setAll(service.getEtudiants());
        labelNbResultats.setText(service.getEtudiants().size() + " etudiant(s) trouve(s)");
        mettreAJourStats();
    }

    /** Met à jour les statistiques en haut */
    private void mettreAJourStats() {
        int total = service.getEtudiants().size();
        long diplomes = service.getEtudiants().stream().filter(Etudiant::diplomeObtenu).count();

        labelTotalEtudiants.setText("Total : " + total + " etudiants");
        labelDiplomes.setText("Diplomes : " + diplomes);
        // Stats par parcours
        for (Parcours p : service.getParcours()) {
            long nb = service.getEtudiantsParParcours(p).size();
            if (p.getCode().equals("MIAGE")) {
                labelStatMiage.setText("MIAGE : " + nb);
            } else if (p.getCode().equals("IO")) {
                labelStatIO.setText("IO : " + nb);
            }
        }
    }

    /** Ouvre la fenêtre de détails pour un étudiant */
    private void ouvrirDetails(Etudiant etudiant) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/detailEtudiant.fxml"));
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Détails - " + etudiant.getNomComplet());
            stage.setScene(new Scene(loader.load(), 700, 500));
            DetailEtudiantController ctrl = loader.getController();
            ctrl.setEtudiant(etudiant, service);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** Ouvre le formulaire de modification pour un étudiant */
    private void ouvrirModification(Etudiant etudiant) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/modifierEtudiant.fxml"));
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Modifier - " + etudiant.getNomComplet());
            stage.setScene(new Scene(loader.load()));
            ModifierEtudiantController ctrl = loader.getController();
            ctrl.setEtudiant(etudiant, this);
            stage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** Ouvre le formulaire de modification pour l'étudiant sélectionné */
    @FXML
    public void ouvrirFormulaireModification() {
        Etudiant etudiant = tableEtudiants.getSelectionModel().getSelectedItem();
        if (etudiant == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText("Veuillez sélectionner un étudiant à modifier.");
            alert.showAndWait();
            return;
        }
        ouvrirModification(etudiant);
    }

    /** Passe au semestre suivant après confirmation */
    @FXML
    public void passerSemestreSuivant() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Semestre suivant");
        confirm.setContentText("Passer au semestre suivant ? Cette action est irréversible.");
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            service.passerAuSemestreSuivant();
            mettreAJourLabelSemestre();
        }
    }

    /** Supprime l'étudiant sélectionné après confirmation */
    @FXML
    public void supprimerEtudiant() {
        Etudiant etudiant = tableEtudiants.getSelectionModel().getSelectedItem();
        if (etudiant == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText("Veuillez sélectionner un étudiant à supprimer.");
            alert.showAndWait();
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setContentText("Supprimer " + etudiant.getNomComplet() + " ?");
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            service.supprimerEtudiant(etudiant.getNumero());
            rafraichirListe();
        }
    }

    /** Ouvre le formulaire d'ajout d'un étudiant */
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
    /** Ouvre l'ecran de saisie des resultats */

    @FXML
    public void ouvrirSaisieResultats() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/saisieResultats.fxml"));
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Saisie des resultats");
            stage.setScene(new Scene(loader.load(), 900, 600));
            SaisieResultatController ctrl = loader.getController();
            ctrl.setService(service);
            stage.showAndWait();
            rafraichirListe();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("ERREUR : " + e.getMessage());
        }
    }
    /** Ouvre l'ecran UE et Formations */
    @FXML
    public void ouvrirUEFormations() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ueFormations.fxml"));
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("UE et Formations");
            stage.setScene(new Scene(loader.load(), 800, 500));
            UEFormationsController ctrl = loader.getController();
            ctrl.setService(service);
            stage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** @return le service étudiant */
    public EtudiantService getService() {
        return service;
    }
}