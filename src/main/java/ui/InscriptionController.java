package ui;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import model.Etudiant;
import model.UE;
import service.EtudiantService;

/**
 * Contrôleur de la fenêtre d'inscription d'un étudiant à une UE.
 */
public class InscriptionController {

    @FXML private ComboBox<UE> comboUE;
    @FXML private Label messageErreur;

    private Etudiant etudiant;
    private EtudiantService service;
    private DetailEtudiantController detailController;

    /**
     * Initialise la fenêtre avec l'étudiant, le service et le contrôleur parent.
     * @param etudiant l'étudiant à inscrire
     * @param service le service de gestion des étudiants
     * @param detailController le contrôleur de la vue détail pour rafraîchir la liste
     */
    public void setDonnees(Etudiant etudiant, EtudiantService service, DetailEtudiantController detailController) {
        this.etudiant = etudiant;
        this.service = service;
        this.detailController = detailController;

        comboUE.getItems().addAll(service.getUes());
        if (!comboUE.getItems().isEmpty()) {
            comboUE.getSelectionModel().selectFirst();
        }
    }

    /**
     * Inscrit l'étudiant à l'UE sélectionnée.
     */
    @FXML
    public void inscrire() {
        UE ue = comboUE.getSelectionModel().getSelectedItem();
        if (ue == null) {
            messageErreur.setText("Veuillez sélectionner une UE.");
            return;
        }
        boolean ok = service.inscrire(etudiant, ue, service.getAnneeCourante(), service.getSemestreCourant());
        if (ok) {
            detailController.rafraichirInscriptions();
            fermer();
        } else {
            messageErreur.setText("Inscription impossible : prérequis non validés.");
        }
    }

    /**
     * Ferme la fenêtre sans inscrire.
     */
    @FXML
    public void annuler() {
        fermer();
    }

    private void fermer() {
        ((Stage) comboUE.getScene().getWindow()).close();
    }
}
