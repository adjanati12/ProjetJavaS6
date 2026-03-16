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

    // ... (le reste de tes méthodes ouvrirDetails, supprimerEtudiant, etc. sont correctes)

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