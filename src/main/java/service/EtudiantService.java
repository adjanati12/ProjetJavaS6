package service;

import model.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Service de gestion des étudiants et des inscriptions.
 */
public class EtudiantService {

    private final String anneeCourante = "2025-2026";
    private Semestre semestreCourant = Semestre.IMPAIR;
    private final List<Etudiant> etudiants = new ArrayList<>();
    private final List<UE> ues = new ArrayList<>();

    public List<UE> getUes() { return ues; }

    public UE getUeParCode(String code) {
        for (UE ue : ues) {
            if (ue.getCode().equals(code)) return ue;
        }
        return null;
    }

    public Etudiant getEtudiantParNumero(String numero) {
        for (Etudiant e : etudiants) {
            if (e.getNumero().equals(numero)) return e;
        }
        return null;
    }

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

    public boolean supprimerEtudiant(String numero) {
        return etudiants.removeIf(e -> e.getNumero().equals(numero));
    }

    public boolean peutSInscrire(Etudiant etudiant, UE ue) {
        for (UE prerequis : ue.getPrerequis()) {
            if (!etudiant.aValide(prerequis)) return false;
        }
        return true;
    }

    public boolean inscrire(Etudiant etudiant, UE ue, String annee, Semestre semestre) {
        if (peutSInscrire(etudiant, ue)) {
            etudiant.ajouterInscription(new Inscription(ue, annee, semestre));
            return true;
        }
        return false;
    }

    public String getAnneeCourante() { return anneeCourante; }

    public Semestre getSemestreCourant() { return semestreCourant; }

    public void passerAuSemestreSuivant() {
        semestreCourant = (semestreCourant == Semestre.IMPAIR) ? Semestre.PAIR : Semestre.IMPAIR;
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
