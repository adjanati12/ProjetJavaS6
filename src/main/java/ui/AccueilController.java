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

import java.util.Optional;

public class AccueilController {

    @FXML private ListView<String> listeEtudiants;
    @FXML private Label labelSemestre;

    private EtudiantService service = new EtudiantService();

    @FXML
    public void initialize() {
        // Chargement des données depuis les fichiers CSV
        CsvLoader.chargerUEs(service);
        CsvLoader.chargerEtudiants(service);
        CsvLoader.chargerInscriptions(service);

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
    public void supprimerEtudiant() {
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

    public void rafraichirListe() {
        listeEtudiants.getItems().clear();
        for (Etudiant e : service.getEtudiants()) {
            listeEtudiants.getItems().add(e.getNumero() + " - " + e.getNomComplet());
        }
    }
}
