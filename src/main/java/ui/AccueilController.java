package ui;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import service.EtudiantService;
import model.Etudiant;

public class AccueilController {

    @FXML
    private ListView<String> listeEtudiants;

    private EtudiantService service = new EtudiantService();

    @FXML
    public void initialize() {
        Etudiant e1 = new Etudiant("12345", "Bakhoum", "Habybatou");
        Etudiant e2 = new Etudiant("67890", "Djanati", "Aya");
        Etudiant e3 = new Etudiant("11111", "Abdelkerim", "Safia");

        service.ajouterEtudiant(e1);
        service.ajouterEtudiant(e2);
        service.ajouterEtudiant(e3);

        for (Etudiant e : service.getEtudiants()) {
            listeEtudiants.getItems().add(e.getNumero() + " - " + e.getNomComplet());
        }
    }
}
