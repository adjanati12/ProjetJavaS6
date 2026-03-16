package ui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Etudiant;
import model.Inscription;
import model.UE;
import service.EtudiantService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DetailEtudiantController {

    @FXML private Label labelNom;
    @FXML private Label labelNumero;
    @FXML private Label labelEcts;
    @FXML private ListView<String> listeInscriptions;
    @FXML private ListView<String> listeUEDisponibles;

    private Etudiant etudiant;
    private EtudiantService service;

    public void setEtudiant(Etudiant etudiant, EtudiantService service) {
        this.etudiant = etudiant;
        this.service = service;
        labelNom.setText(etudiant.getNomComplet());
        labelNumero.setText("Numero : " + etudiant.getNumero());
        rafraichirInscriptions();
        rafraichirUEDisponibles();
    }

    public void rafraichirInscriptions() {
        listeInscriptions.getItems().clear();
        labelEcts.setText("ECTS valides : " + etudiant.calculerECTSValides());
        for (Inscription ins : etudiant.getInscriptions()) {
            String statut;
            if (ins.isValide()) {
                statut = "[Validee]";
            } else if (ins.isEchouee()) {
                statut = "[Echouee]";
            } else {
                statut = "[En cours]";
            }
            listeInscriptions.getItems().add(
                ins.getUe().getNom() + " (" + ins.getUe().getCode() + ") - " + ins.getSemestre() + " - " + statut
            );
        }
        if (etudiant.getInscriptions().isEmpty()) {
            listeInscriptions.getItems().add("Aucune inscription");
        }
    }

    private void rafraichirUEDisponibles() {
        listeUEDisponibles.getItems().clear();
        List<UE> dejaInscrit = new ArrayList<>();
        for (Inscription ins : etudiant.getInscriptions()) {
            dejaInscrit.add(ins.getUe());
        }
        boolean aucune = true;
        for (UE ue : service.getUes()) {
            if (!dejaInscrit.contains(ue) && service.peutSInscrire(etudiant, ue)) {
                listeUEDisponibles.getItems().add(
                    ue.getNom() + " (" + ue.getCode() + ") - " + ue.getEcts() + " ECTS"
                );
                aucune = false;
            }
        }
        if (aucune) {
            listeUEDisponibles.getItems().add("Aucune UE disponible");
        }
    }

    @FXML
    public void ajouterInscription() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/inscrireEtudiant.fxml"));
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Inscrire - " + etudiant.getNomComplet());
            stage.setScene(new Scene(loader.load()));
            InscriptionController ctrl = loader.getController();
            ctrl.setDonnees(etudiant, service, this);
            stage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @FXML
    public void validerUE() {
        int index = listeInscriptions.getSelectionModel().getSelectedIndex();
        if (index < 0) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.initOwner(labelNom.getScene().getWindow());
            alert.setContentText("Selectionnez une UE dans la liste.");
            alert.showAndWait();
            return;
        }
        Inscription ins = etudiant.getInscriptions().get(index);
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.initModality(Modality.APPLICATION_MODAL);
        confirm.initOwner(labelNom.getScene().getWindow());
        confirm.setTitle("Resultat UE");
        confirm.setHeaderText(ins.getUe().getNom());
        confirm.setContentText("L'etudiant a-t-il valide cette UE ?");
        ButtonType btnValider = new ButtonType("Validee");
        ButtonType btnEchouer = new ButtonType("Echouee");
        ButtonType btnAnnuler = new ButtonType("Annuler", ButtonBar.ButtonData.CANCEL_CLOSE);
        confirm.getButtonTypes().setAll(btnValider, btnEchouer, btnAnnuler);
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == btnValider) {
            ins.setValide(true);
            rafraichirInscriptions();
            rafraichirUEDisponibles();
        } else if (result.isPresent() && result.get() == btnEchouer) {
            ins.setEchouee(true);
            rafraichirInscriptions();
            rafraichirUEDisponibles();
        }
    }

    @FXML
    public void fermer() {
        ((Stage) labelNom.getScene().getWindow()).close();
    }
}
