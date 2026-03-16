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

    public Etudiant ajouterEtudiant(Etudiant e) throws SQLException {
        if (e.getNumero() == null || e.getNumero().isBlank())
            throw new IllegalArgumentException("Numéro étudiant obligatoire");

        if (e.getNom() == null || e.getNom().isBlank())
            throw new IllegalArgumentException("Nom obligatoire");

        if (e.getPrenom() == null || e.getPrenom().isBlank())
            throw new IllegalArgumentException("Prénom obligatoire");

        String num = e.getNumero().trim();
        if (numeroExisteDeja(num))
            throw new IllegalArgumentException("Numéro étudiant déjà utilisé : " + num);

        // Persister en base
        Connection conn = DatabaseConnection.getInstance().getConnection();
        new EtudiantDAO(conn).insert(e);

        // Ajouter en mémoire
        etudiants.add(e);
        return e;
    }

    public List<Etudiant> getEtudiants() { return etudiants; }

    public boolean numeroExisteDeja(String numero) {
        if (numero == null) return false;
        for (Etudiant e : etudiants) {
            if (e.getNumero().equals(numero.trim())) return true;
        }
        return false;
    }

    public Etudiant ajouterEtudiant(String numero, String nom, String prenom) {
        if (numero == null || numero.isBlank()) throw new IllegalArgumentException("Numero etudiant obligatoire");
        if (nom == null || nom.isBlank()) throw new IllegalArgumentException("Nom obligatoire");
        if (prenom == null || prenom.isBlank()) throw new IllegalArgumentException("Prenom obligatoire");
        if (numeroExisteDeja(numero)) throw new IllegalArgumentException("Numero etudiant deja utilise : " + numero);
        Etudiant e = new Etudiant(numero.trim(), nom.trim(), prenom.trim());
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
        if (!peutSInscrire(etudiant, ue)) return false;

        // Persister en base
        Connection conn = DatabaseConnection.getInstance().getConnection();
        new InscriptionDAO(conn).inscrire(etudiant, ue, annee, semestre);

        // Mettre à jour en mémoire
        etudiant.ajouterInscription(new Inscription(ue, annee, semestre));
        return true;
    }

    public String getAnneeCourante() { return anneeCourante; }

    public Semestre getSemestreCourant() { return semestreCourant; }

    public void passerAuSemestreSuivant() {
        if (semestreCourant == Semestre.IMPAIR) {
            semestreCourant = Semestre.PAIR;
            // Recharge les inscriptions du semestre PAIR
            for (Etudiant e : etudiants) {
                e.getInscriptions().clear();
            }
            CsvLoader.chargerInscriptionsFichier(this, "/data/inscriptions_pair.csv");
        } else {
            semestreCourant = Semestre.IMPAIR;
            String[] parts = anneeCourante.split("-");
            int debut = Integer.parseInt(parts[0]) + 1;
            int fin = Integer.parseInt(parts[1]) + 1;
            anneeCourante = debut + "-" + fin;
            // Recharge les inscriptions du semestre IMPAIR
            for (Etudiant e : etudiants) {
                e.getInscriptions().clear();
            }
            CsvLoader.chargerInscriptionsFichier(this, "/data/inscriptions.csv");
        }
    }

    public boolean marquerResultat(Etudiant etudiant, UE ue, boolean valide) throws SQLException {
        // Trouver l'inscription en mémoire (sur le semestre courant)
        for (Inscription ins : etudiant.getInscriptions()) {
            if (ins.getUe().equals(ue)
                    && ins.getAnneeUniversitaire().equals(anneeCourante)
                    && ins.getSemestre() == semestreCourant) {

                // Persister en base
                Connection conn = DatabaseConnection.getInstance().getConnection();
                new InscriptionDAO(conn).mettreAJourResultat(
                        etudiant, ue, anneeCourante, semestreCourant, valide);

                // Mettre à jour en mémoire
                ins.setValide(valide);
                return true;
            }
        }
        return false;
    }

    public void chargerDepuisBDD() throws SQLException {
            Connection conn = DatabaseConnection.getInstance().getConnection();

            // 1. Charger les UE (avec prérequis)
            UEDAO uedao = new UEDAO(conn);
            ues.clear();
            ues.addAll(uedao.findAll());

            // 2. Charger les étudiants (avec inscriptions)
            EtudiantDAO etudiantDAO = new EtudiantDAO(conn);
            etudiants.clear();
            etudiants.addAll(etudiantDAO.findAll(ues));
        }
public void modifierEtudiant(Etudiant etudiant) throws SQLException {
    Connection conn = DatabaseConnection.getInstance().getConnection();
    new EtudiantDAO(conn).update(etudiant);
    // L'objet en mémoire a déjà été modifié via ses setters
}
    }

