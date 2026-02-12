package service;

import model.*;

import java.lang.reflect.Array;
import java.util.ArrayList;


public class EtudiantService {
    private String anneeCourante = "2025-2026";
    private Semestre semestreCourant = Semestre.IMPAIR;

    private ArrayList<Etudiant> etudiants = new ArrayList<>();

    public void ajouterEtudiant(Etudiant e) {
        etudiants.add(e);
    }

    public ArrayList<Etudiant> getEtudiants() {
        return etudiants;
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
            System.out.println("Inscription réalisée avec succées ! Pour l'étudiant : "+ etudiant.getNomComplet());
        } else {
            System.out.println("Prérequis non validés ! Pour l'étudiant : "+ etudiant.getNomComplet());
        }
    }
    public String getAnneeCourante() {
        return anneeCourante;
    }
    public Semestre getSemestreCourant() {
        return semestreCourant;
    }

}
