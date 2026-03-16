package service;

import dao.DatabaseConnection;
import model.*;
import dao.EtudiantDAO;
import dao.InscriptionDAO;
import dao.UEDAO;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Service de gestion des étudiants et des inscriptions.
 */
public class EtudiantService {

    private String anneeCourante = "2025-2026";
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

    public List<Etudiant> getEtudiants() { return etudiants; }

    public boolean numeroExisteDeja(String numero) {
        if (numero == null) return false;
        for (Etudiant e : etudiants) {
            if (e.getNumero().equals(numero.trim())) return true;
        }
        return false;
    }

    // VERSION CORRIGÉE : Utilise les paramètres et gère la base de données
    public Etudiant ajouterEtudiant(String numero, String nom, String prenom) throws SQLException {
        if (numero == null || numero.isBlank()) throw new IllegalArgumentException("Numéro étudiant obligatoire");
        if (nom == null || nom.isBlank()) throw new IllegalArgumentException("Nom obligatoire");
        if (prenom == null || prenom.isBlank()) throw new IllegalArgumentException("Prénom obligatoire");

        String num = numero.trim();
        if (numeroExisteDeja(num)) throw new IllegalArgumentException("Numéro étudiant déjà utilisé : " + num);

        Etudiant e = new Etudiant(num, nom.trim(), prenom.trim());

        // 1. Persister en base de données
        Connection conn = DatabaseConnection.getInstance().getConnection();
        new EtudiantDAO(conn).insert(e);

        // 2. Ajouter en mémoire pour l'affichage
        etudiants.add(e);
        return e;
    }

    public boolean supprimerEtudiant(String numero) throws SQLException {
        Connection conn = DatabaseConnection.getInstance().getConnection();
        new EtudiantDAO(conn).delete(numero);
        return etudiants.removeIf(e -> e.getNumero().equals(numero));
    }

    public boolean peutSInscrire(Etudiant etudiant, UE ue) {
        for (UE prerequis : ue.getPrerequis()) {
            if (!etudiant.aValide(prerequis)) return false;
        }
        return true;
    }

    public boolean inscrire(Etudiant etudiant, UE ue, String annee, Semestre semestre) throws SQLException {
        if (!peutSInscrire(etudiant, ue)) {
            throw new IllegalArgumentException("Inscription impossible : prérequis non validés.");
        }

        // --- TA LOGIQUE DE LIMITE ECTS ---
        int ectsActuels = etudiant.calculerECTSParSemestre(annee, semestre);
        if (ectsActuels + ue.getEcts() > 30) {
            throw new IllegalArgumentException("Limite d'ECTS dépassée (Maximum 30 ECTS).");
        }

        // 1. Persister en base de données
        Connection conn = DatabaseConnection.getInstance().getConnection();
        new InscriptionDAO(conn).inscrire(etudiant, ue, annee, semestre);

        // 2. Mettre à jour en mémoire
        etudiant.ajouterInscription(new Inscription(ue, annee, semestre));
        return true;
    }

    public String getAnneeCourante() { return anneeCourante; }

    public Semestre getSemestreCourant() { return semestreCourant; }

    public void passerAuSemestreSuivant() {
        if (semestreCourant == Semestre.IMPAIR) {
            semestreCourant = Semestre.PAIR;
        } else {
            semestreCourant = Semestre.IMPAIR;
            String[] parts = anneeCourante.split("-");
            int debut = Integer.parseInt(parts[0]) + 1;
            int fin = Integer.parseInt(parts[1]) + 1;
            anneeCourante = debut + "-" + fin;
        }
        // Note: Ici, il faudra plus tard recharger les données depuis la DB au lieu du CSV
    }

    public boolean marquerResultat(Etudiant etudiant, UE ue, boolean valide) throws SQLException {
        for (Inscription ins : etudiant.getInscriptions()) {
            if (ins.getUe().equals(ue)
                    && ins.getAnneeUniversitaire().equals(anneeCourante)
                    && ins.getSemestre() == semestreCourant) {

                // 1. Persister en base de données
                Connection conn = DatabaseConnection.getInstance().getConnection();
                new InscriptionDAO(conn).mettreAJourResultat(
                        etudiant, ue, anneeCourante, semestreCourant, valide);

                // 2. Mettre à jour en mémoire
                ins.setValide(valide);
                return true;
            }
        }
        return false;
    }
}