package ui;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import service.EtudiantService;
import model.Etudiant;
import model.UE;
import model.Semestre;

public class AccueilController {

    @FXML
    private ListView<String> listeEtudiants;

    private EtudiantService service = new EtudiantService();

    @FXML
    public void initialize() {
        UE algo = new UE("INF101", "Algorithmique", 6);
        UE progObj1 = new UE("INF102", "Prog Objet 1", 6);

        Etudiant e1 = new Etudiant("12345", "Bakhoum", "Habybatou");
        Etudiant e2 = new Etudiant("67890", "Djanati", "Aya");
        Etudiant e3 = new Etudiant("11111", "Abdelkerim", "Safia");

        service.inscrire(e1, algo, "2025-2026", Semestre.IMPAIR);
        service.inscrire(e2, progObj1, "2025-2026", Semestre.IMPAIR);

        service.ajouterEtudiant(e1);
        service.ajouterEtudiant(e2);
        service.ajouterEtudiant(e3);

        rafraichirListe();
    }

    private void rafraichirListe() {
        listeEtudiants.getItems().clear();
        for (Etudiant e : service.getEtudiants()) {
            listeEtudiants.getItems().add(e.getNumero() + " - " + e.getNomComplet());
        }
    }
}