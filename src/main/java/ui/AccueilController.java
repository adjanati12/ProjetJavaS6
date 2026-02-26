package ui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Etudiant;
import model.Semestre;
import model.UE;
import service.EtudiantService;

import java.util.Optional;

public class AccueilController {

    @FXML
    private ListView<String> listeEtudiants;

    private EtudiantService service = new EtudiantService();

    @FXML
    public void initialize() {
        UE algo = service.getUeParCode("INF101");
        UE progObj = service.getUeParCode("INF102");
        UE bdd = service.getUeParCode("INF103");
        UE reseau = service.getUeParCode("INF104");
        UE gestion = service.getUeParCode("MGT101");

        Etudiant e1 = new Etudiant("23001", "Bakhoum", "Habybatou");
        Etudiant e2 = new Etudiant("23002", "Djanati", "Aya");
        Etudiant e3 = new Etudiant("23003", "Abdelkerim", "Safia");
        Etudiant e4 = new Etudiant("23004", "Martin", "Lucas");
        Etudiant e5 = new Etudiant("23005", "Bernard", "Camille");
        Etudiant e6 = new Etudiant("23006", "Diallo", "Mamadou");
        Etudiant e7 = new Etudiant("23007", "Chen", "Wei");
        Etudiant e8 = new Etudiant("23008", "Lefebvre", "Sophie");
        Etudiant e9 = new Etudiant("23009", "Traoré", "Aminata");
        Etudiant e10 = new Etudiant("23010", "Dupont", "Pierre");

        service.inscrire(e1, algo, "2025-2026", Semestre.IMPAIR);
        service.inscrire(e1, progObj, "2025-2026", Semestre.IMPAIR);
        service.inscrire(e2, algo, "2025-2026", Semestre.IMPAIR);
        service.inscrire(e2, bdd, "2025-2026", Semestre.PAIR);
        service.inscrire(e3, progObj, "2025-2026", Semestre.IMPAIR);
        service.inscrire(e3, reseau, "2025-2026", Semestre.PAIR);
        service.inscrire(e4, algo, "2025-2026", Semestre.IMPAIR);
        service.inscrire(e4, gestion, "2025-2026", Semestre.PAIR);
        service.inscrire(e5, bdd, "2025-2026", Semestre.PAIR);
        service.inscrire(e6, algo, "2025-2026", Semestre.IMPAIR);
        service.inscrire(e6, progObj, "2025-2026", Semestre.IMPAIR);
        service.inscrire(e7, bdd, "2025-2026", Semestre.PAIR);
        service.inscrire(e7, reseau, "2025-2026", Semestre.PAIR);
        service.inscrire(e8, gestion, "2025-2026", Semestre.PAIR);
        service.inscrire(e9, algo, "2025-2026", Semestre.IMPAIR);
        service.inscrire(e10, progObj, "2025-2026", Semestre.IMPAIR);

        service.ajouterEtudiant(e1);
        service.ajouterEtudiant(e2);
        service.ajouterEtudiant(e3);
        service.ajouterEtudiant(e4);
        service.ajouterEtudiant(e5);
        service.ajouterEtudiant(e6);
        service.ajouterEtudiant(e7);
        service.ajouterEtudiant(e8);
        service.ajouterEtudiant(e9);
        service.ajouterEtudiant(e10);

        rafraichirListe();

        listeEtudiants.setOnMouseClicked((MouseEvent event) -> {
            if (event.getClickCount() == 2) {
                ouvrirDetails();
            }
        });
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
    public void supprimerEtudiant() {
        int index = listeEtudiants.getSelectionModel().getSelectedIndex();
        if (index < 0) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Aucune sélection");
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
