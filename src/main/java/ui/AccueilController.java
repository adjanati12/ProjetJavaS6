package ui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Etudiant;
import service.CsvLoader;
import service.EtudiantService;

import java.sql.SQLException;
import java.util.Optional;

public class AccueilController {

    @FXML private ListView<String> listeEtudiants;
    @FXML private Label labelSemestre;

    private EtudiantService service = new EtudiantService();
    private void ouvrirDetails() {
        // 1. On récupère l'index sélectionné dans la liste
        int index = listeEtudiants.getSelectionModel().getSelectedIndex();

        // 2. Si rien n'est sélectionné, on ne fait rien
        if (index < 0) return;

        // 3. On récupère l'objet Etudiant correspondant
        Etudiant etudiant = service.getEtudiants().get(index);

        try {
            // 4. On charge la nouvelle fenêtre (le fichier FXML)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/detailEtudiant.fxml"));
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Détails - " + etudiant.getNomComplet());
            stage.setScene(new Scene(loader.load(), 400, 400));

            // 5. On passe l'étudiant au contrôleur de la fenêtre de détails
            DetailEtudiantController ctrl = loader.getController();
            ctrl.setEtudiant(etudiant, service);

            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Impossible d'ouvrir la fenêtre de détails : " + e.getMessage());
            alert.showAndWait();
        }
    }
    @FXML
    public void initialize() {
        try {
            // 1. Charger les UEs depuis le CSV
            CsvLoader.chargerUEs(service);

            // 2. Charger les étudiants (Importation CSV -> Base de données)
            // C'est ici que Bakhoum, Djanati, etc. sont récupérés
            CsvLoader.chargerEtudiants(service);

            // 3. Charger les inscriptions
            CsvLoader.chargerInscriptions(service);

        } catch (Exception e) {
            System.err.println("Erreur lors du chargement initial : " + e.getMessage());
            // Si tu as une erreur "Table ETUDIANT not found", c'est ici qu'elle s'affichera
        }

        // 4. Rafraîchir l'affichage
        rafraichirListe();
        mettreAJourLabelSemestre();

        listeEtudiants.setOnMouseClicked((MouseEvent event) -> {
            if (event.getClickCount() == 2) {
                ouvrirDetails();
            }
        });
    }



    private void mettreAJourLabelSemestre() {
        labelSemestre.setText(service.getAnneeCourante() + " - Semestre " + service.getSemestreCourant());
    }

<<<<<<< HEAD
    // ... (le reste de tes méthodes ouvrirDetails, supprimerEtudiant, etc. sont correctes)
=======
    private void ouvrirDetails() {
        int index = listeEtudiants.getSelectionModel().getSelectedIndex();
        if (index < 0) return;
        Etudiant etudiant = service.getEtudiants().get(index);
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/detailEtudiant.fxml"));
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Détails - " + etudiant.getNomComplet());
            stage.setScene(new Scene(loader.load(), 400, 400));
            DetailEtudiantController ctrl = loader.getController();
            ctrl.setEtudiant(etudiant, service);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void ouvrirFormulaireModification() {
        int index = listeEtudiants.getSelectionModel().getSelectedIndex();
        if (index < 0) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText("Veuillez sélectionner un étudiant à modifier.");
            alert.showAndWait();
            return;
        }
        Etudiant etudiant = service.getEtudiants().get(index);
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

    @FXML
    public void supprimerEtudiant() throws SQLException {
        int index = listeEtudiants.getSelectionModel().getSelectedIndex();
        if (index < 0) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText("Veuillez sélectionner un étudiant à supprimer.");
            alert.showAndWait();
            return;
        }
        Etudiant etudiant = service.getEtudiants().get(index);
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setContentText("Supprimer " + etudiant.getNomComplet() + " ?");
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            service.supprimerEtudiant(etudiant.getNumero());
            rafraichirListe();
        }
    }

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
>>>>>>> 2c3b101d9db5ee65f261e2625f82b0a4a2d8b349

    public void rafraichirListe() {
        listeEtudiants.getItems().clear();
        if (service.getEtudiants().isEmpty()) {
            System.out.println("La liste des étudiants est vide dans le service.");
        }
        for (Etudiant e : service.getEtudiants()) {
            listeEtudiants.getItems().add(e.getNumero() + " - " + e.getNomComplet());
        }
    }

    public EtudiantService getService() {
        return service;
    }
}