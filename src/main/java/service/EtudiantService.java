package service;

import model.*;
import java.util.ArrayList;
import java.util.List;

public class EtudiantService {

    private final String anneeCourante = "2025-2026";
    private Semestre semestreCourant = Semestre.IMPAIR;
    private final List<Etudiant> etudiants = new ArrayList<>();

    public void ajouterEtudiant(Etudiant e) {
        etudiants.add(e);
    }

    public List<Etudiant> getEtudiants() {
        return etudiants;
    }
    public boolean numeroExisteDeja(String numero) {
        if (numero == null) return false;
        String n = numero.trim();
        for (Etudiant e : etudiants) {
            if (e.getNumero().equals(n)) return true;
        }
        return false;
    }

    public Etudiant ajouterEtudiant(String numero, String nom, String prenom) {
        if (numero == null || numero.isBlank()) throw new IllegalArgumentException("Numéro étudiant obligatoire");
        if (nom == null || nom.isBlank()) throw new IllegalArgumentException("Nom obligatoire");
        if (prenom == null || prenom.isBlank()) throw new IllegalArgumentException("Prénom obligatoire");

        String num = numero.trim();
        String n = nom.trim();
        String p = prenom.trim();

        if (numeroExisteDeja(num)) {
            throw new IllegalArgumentException("Numéro étudiant déjà utilisé : " + num);
        }

        Etudiant e = new Etudiant(num, n, p);
        etudiants.add(e);
        return e;
    }


    public boolean peutSInscrire(Etudiant etudiant, UE ue) {
        for (UE prerequis : ue.getPrerequis()) {
            if (!etudiant.aValide(prerequis)) {
                return false;
            }
        }
        return true;
    }

    public void inscrire(Etudiant etudiant, UE ue, String annee, Semestre semestre) {
        if (peutSInscrire(etudiant, ue)) {
            etudiant.ajouterInscription(new Inscription(ue, annee, semestre));
            javax.swing.JOptionPane.showMessageDialog(null, "Inscription réalisée avec succès ! Pour l'étudiant : " + etudiant.getNomComplet());
        } else {
            javax.swing.JOptionPane.showMessageDialog(null, "Prérequis non validés ! Pour l'étudiant : " + etudiant.getNomComplet());
        }
    }

    public String getAnneeCourante() { return anneeCourante; }

    public Semestre getSemestreCourant() { return semestreCourant; }

    public void passerAuSemestreSuivant() {
        if (semestreCourant == Semestre.IMPAIR) {
            semestreCourant = Semestre.PAIR;
        } else {
            semestreCourant = Semestre.IMPAIR;
        }
    }

    public boolean marquerResultat(Etudiant etudiant, UE ue, boolean valide) {
        for (Inscription ins : etudiant.getInscriptions()) {
            if (ins.getUe().equals(ue)) {
                ins.setValide(valide);
                return true;
            }
        }
        return false;
    }
}