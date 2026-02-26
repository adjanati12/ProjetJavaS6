package ui;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.Etudiant;
import model.Inscription;
import model.Semestre;
import model.UE;
import service.EtudiantService;

import java.util.Optional;

public class DetailEtudiantController {

    @FXML private Label labelNom;
    @FXML private Label labelNumero;
    @FXML private ListView<String> listeInscriptions;

    private Etudiant etudiant;
    private EtudiantService service;

    public void setEtudiant(Etudiant etudiant, EtudiantService service) {
        this.etudiant = etudiant;
        this.service = service;
        labelNom.setText(etudiant.getNomComplet());
        labelNumero.setText("Numéro : " + etudiant.getNumero());
        rafraichirInscriptions();
    }

    private void rafraichirInscriptions() {
        listeInscriptions.getItems().clear();
        for (Inscription ins : etudiant.getInscriptions()) {
            String statut = ins.isValide() ? "✅ Validée" : "⏳ En cours";
            listeInscriptions.getItems().add(
                ins.getUe().getNom() + " (" + ins.getUe().getCode() + ") - " + ins.getSemestre() + " - " + statut
            );
        }
        if (etudiant.getInscriptions().isEmpty()) {
            listeInscriptions.getItems().add("Aucune inscription");
        }
    }

    @FXML
    public void ajouterInscription() {
        ChoiceDialog<UE> dialog = new ChoiceDialog<>(service.getUes().get(0), service.getUes());
        dialog.setTitle("Inscription");
        dialog.setHeaderText("Inscrire " + etudiant.getNomComplet());
        dialog.setContentText("Choisir une UE :");
        Optional<UE> result = dialog.showAndWait();
        result.ifPresent(ue -> {
            boolean ok = service.inscrire(etudiant, ue, service.getAnneeCourante(), service.getSemestreCourant());
            if (ok) {
                rafraichirInscriptions();
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Inscription impossible");
                alert.setContentText("Prérequis non validés pour cette UE.");
                alert.showAndWait();
            }
        });
    }

    @FXML
    public void validerUE() {
        int index = listeInscriptions.getSelectionModel().getSelectedIndex();
        if (index < 0) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText("Sélectionnez une UE dans la liste.");
            alert.showAndWait();
            return;
        }
        Inscription ins = etudiant.getInscriptions().get(index);
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Résultat UE");
        confirm.setHeaderText(ins.getUe().getNom());
        confirm.setContentText("L'étudiant a-t-il validé cette UE ?");
        ButtonType btnValider = new ButtonType("✅ Validée");
        ButtonType btnEchouer = new ButtonType("❌ Échouée");
        ButtonType btnAnnuler = new ButtonType("Annuler", ButtonBar.ButtonData.CANCEL_CLOSE);
        confirm.getButtonTypes().setAll(btnValider, btnEchouer, btnAnnuler);
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == btnValider) {
            service.marquerResultat(etudiant, ins.getUe(), true);
            rafraichirInscriptions();
        } else if (result.isPresent() && result.get() == btnEchouer) {
            service.marquerResultat(etudiant, ins.getUe(), false);
            rafraichirInscriptions();
        }
    }

    @FXML
    public void fermer() {
        ((Stage) labelNom.getScene().getWindow()).close();
    }
}
