package service;

import dao.EtudiantDAO;
import dao.InscriptionDAO;
import dao.UEDAO;
import model.*;
import java.util.ArrayList;
import java.util.List;

import dao.DatabaseConnection;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Service de gestion des étudiants, mentions, parcours et inscriptions.
 */
public class EtudiantService {

    private String anneeCourante = "2025-2026"; // ✅ "final" retiré pour permettre l'incrémentation
    private Semestre semestreCourant = Semestre.IMPAIR;
    private final List<Etudiant> etudiants = new ArrayList<>();
    private final List<UE> ues = new ArrayList<>();
    private final List<Mention> mentions = new ArrayList<>();
    private final List<Parcours> parcours = new ArrayList<>();

    /** @return la liste des UE */
    public List<UE> getUes() { return ues; }

    /** @return la liste des mentions */
    public List<Mention> getMentions() { return mentions; }

    /** @return la liste des parcours */
    public List<Parcours> getParcours() { return parcours; }

    /** @return l'UE correspondant au code, ou null */
    public UE getUeParCode(String code) {
        for (UE ue : ues) {
            if (ue.getCode().equals(code)) return ue;
        }
        return null;
    }

    /** @return la mention correspondant au code, ou null */
    public Mention getMentionParCode(String code) {
        for (Mention m : mentions) {
            if (m.getCode().equals(code)) return m;
        }
        return null;
    }

    /** @return le parcours correspondant au code, ou null */
    public Parcours getParcoursParCode(String code) {
        for (Parcours p : parcours) {
            if (p.getCode().equals(code)) return p;
        }
        return null;
    }

    /** @return l'étudiant correspondant au numéro, ou null */
    public Etudiant getEtudiantParNumero(String numero) {
        for (Etudiant e : etudiants) {
            if (e.getNumero().equals(numero)) return e;
        }
        return null;
    }

    /** Ajoute un étudiant déjà construit (utilisé par le CsvLoader) */
    public void ajouterEtudiant(Etudiant e) {
        etudiants.add(e);
    }

    /** @return la liste des étudiants */
    public List<Etudiant> getEtudiants() { return etudiants; }

    /**
     * @return la liste des étudiants filtrée par parcours
     * @param parcours le parcours à filtrer, null pour tous
     */
    public List<Etudiant> getEtudiantsParParcours(Parcours parcours) {
        if (parcours == null) return etudiants;
        List<Etudiant> result = new ArrayList<>();
        for (Etudiant e : etudiants) {
            if (parcours.equals(e.getParcours())) result.add(e);
        }
        return result;
    }

    /**
     * @return la liste des étudiants filtrée par mention
     * @param mention la mention à filtrer, null pour tous
     */
    public List<Etudiant> getEtudiantsParMention(Mention mention) {
        if (mention == null) return etudiants;
        List<Etudiant> result = new ArrayList<>();
        for (Etudiant e : etudiants) {
            if (e.getParcours() != null && mention.equals(e.getParcours().getMention())) {
                result.add(e);
            }
        }
        return result;
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

    /** @return true si le numéro est déjà utilisé */
    public boolean numeroExisteDeja(String numero) {
        if (numero == null) return false;
        String n = numero.trim();
        for (Etudiant e : etudiants) {
            if (e.getNumero().equals(n)) return true;
        }
        return false;
    }

    /**
     * Crée et ajoute un étudiant après validation des champs et du doublon.
     * @param numero numéro étudiant
     * @param nom nom
     * @param prenom prénom
     * @param parcours parcours de l'étudiant, peut être null
     * @return l'étudiant créé
     * @throws IllegalArgumentException si un champ est vide ou le numéro déjà utilisé
     */
    public Etudiant ajouterEtudiant(String numero, String nom, String prenom, Parcours parcours) {
        if (numero == null || numero.isBlank()) throw new IllegalArgumentException("Numéro étudiant obligatoire");
        if (nom == null || nom.isBlank()) throw new IllegalArgumentException("Nom obligatoire");
        if (prenom == null || prenom.isBlank()) throw new IllegalArgumentException("Prénom obligatoire");

        String num = numero.trim();
        if (numeroExisteDeja(num)) {
            throw new IllegalArgumentException("Numéro étudiant déjà utilisé : " + num);
        }

        Etudiant e = new Etudiant(num, nom.trim(), prenom.trim(), parcours);
        etudiants.add(e);
        return e;
    }

    /**
     * Crée et ajoute un étudiant sans parcours.
     * @param numero numéro étudiant
     * @param nom nom
     * @param prenom prénom
     * @return l'étudiant créé
     * @throws IllegalArgumentException si un champ est vide ou le numéro déjà utilisé
     */
    public Etudiant ajouterEtudiant(String numero, String nom, String prenom) {
        return ajouterEtudiant(numero, nom, prenom, null);
    }
    public int calculerEctsSemestre(Etudiant etudiant) {
        int total = 0;
        for (Inscription ins : etudiant.getInscriptions()) {
            if (ins.getAnneeUniversitaire().equals(anneeCourante)
                    && ins.getSemestre() == semestreCourant
                    && !ins.isValide()
                    && !ins.isEchouee()) {
                total += ins.getUe().getEcts();
            }
        }
        return total;
    }

    public ResultatInscription verifierInscription(Etudiant etudiant, UE ue) {
        // 1. Déjà inscrit ?
        if (estDejaInscrit(etudiant, ue)) {
            return ResultatInscription.DEJA_INSCRIT;
        }

        // 2. Prérequis manquants ?
        for (UE prerequis : ue.getPrerequis()) {
            if (!etudiant.aValide(prerequis)) {
                return ResultatInscription.PREREQUIS;
            }
        }

        // 3. Vérification ECTS
        int ectsSemestre = calculerEctsSemestre(etudiant);
        int apresAjout   = ectsSemestre + ue.getEcts();

        if (apresAjout > 39) {
            return ResultatInscription.BLOQUE_ECTS;
        }
        if (apresAjout > 30) {
            return ResultatInscription.DEROGATION;
        }

        return ResultatInscription.OK;
    }

    public void inscrireAvecControle(Etudiant etudiant, UE ue,
                                     String annee, Semestre semestre)
            throws InscriptionException, SQLException {

        ResultatInscription resultat = verifierInscription(etudiant, ue);

        switch (resultat) {
            case PREREQUIS:
                throw new InscriptionException(
                        "Inscription impossible : les prérequis de l'UE \""
                                + ue.getNom() + "\" ne sont pas tous validés.");

            case DEJA_INSCRIT:
                throw new InscriptionException(
                        "L'étudiant " + etudiant.getNomComplet()
                                + " est déjà inscrit à \"" + ue.getNom()
                                + "\" ce semestre.");

            case BLOQUE_ECTS:
                int actuel = calculerEctsSemestre(etudiant);
                throw new InscriptionException(
                        "Inscription impossible : ajouter " + ue.getEcts()
                                + " ECTS dépasserait la limite absolue de 39 ECTS "
                                + "(total actuel : " + actuel + " ECTS).");

            case DEROGATION:
                // On laisse passer mais le contrôleur devra
                // afficher une alerte de dérogation à l'utilisateur
                break;

            case OK:
            default:
                break;
        }

        // Persister en base (le trigger Oracle bloquera aussi si > 39)
        try {
            Connection conn = DatabaseConnection.getInstance().getConnection();
            new InscriptionDAO(conn).inscrire(etudiant, ue, annee, semestre);
        } catch (SQLException e) {
            // Intercepter les erreurs Oracle des triggers
            if (e.getErrorCode() == 20001) {
                throw new InscriptionException(
                        "Bloqué par Oracle : " + e.getMessage());
            }
            if (e.getErrorCode() == 20002) {
                throw new InscriptionException(
                        "Bloqué par Oracle : double inscription détectée.");
            }
            throw e; // Autres erreurs SQL → on remonte
        }

        // Mettre à jour en mémoire
        etudiant.ajouterInscription(new Inscription(ue, annee, semestre));
    }
    /** @return true si l'étudiant a été supprimé */
    public boolean supprimerEtudiant(String numero) {
        return etudiants.removeIf(e -> e.getNumero().equals(numero));
    }

    /** @return true si l'étudiant a tous les prérequis de l'UE validés */
    public boolean peutSInscrire(Etudiant etudiant, UE ue) {
        for (UE prerequis : ue.getPrerequis()) {
            if (!etudiant.aValide(prerequis)) return false;
        }
        return true;
    }

    /**
     * @return la liste des UE auxquelles l'étudiant peut s'inscrire
     * (prérequis validés et pas encore inscrit ce semestre)
     */
    public List<UE> getUesDisponibles(Etudiant etudiant) {
        List<UE> disponibles = new ArrayList<>();
        for (UE ue : ues) {
            if (peutSInscrire(etudiant, ue) && !estDejaInscrit(etudiant, ue)) {
                disponibles.add(ue);
            }
        }
        return disponibles;
    }

    /**
     * @return true si l'étudiant est déjà inscrit à cette UE ce semestre
     */
    public boolean estDejaInscrit(Etudiant etudiant, UE ue) {
        for (Inscription ins : etudiant.getInscriptions()) {
            if (ins.getUe().equals(ue)
                    && ins.getAnneeUniversitaire().equals(anneeCourante)
                    && ins.getSemestre() == semestreCourant) {
                return true;
            }
        }
        return false;
    }

    /** Inscrit l'étudiant à l'UE si les prérequis sont satisfaits */
    public boolean inscrire(Etudiant etudiant, UE ue, String annee, Semestre semestre) {
        if (peutSInscrire(etudiant, ue)) {
            etudiant.ajouterInscription(new Inscription(ue, annee, semestre));
            return true;
        }
        return false;
    }

    /** @return l'année universitaire courante */
    public String getAnneeCourante() { return anneeCourante; }

    /** @return le semestre courant */
    public Semestre getSemestreCourant() { return semestreCourant; }

    /** Passe au semestre suivant (IMPAIR → PAIR → IMPAIR de l'année suivante) */
    public void passerAuSemestreSuivant() {
        if (semestreCourant == Semestre.PAIR) {
            // ✅ On repasse en IMPAIR → on incrémente l'année universitaire
            String[] parts = anneeCourante.split("-");
            int debut = Integer.parseInt(parts[0]) + 1;
            int fin   = Integer.parseInt(parts[1]) + 1;
            anneeCourante = debut + "-" + fin;
        }
        semestreCourant = (semestreCourant == Semestre.IMPAIR) ? Semestre.PAIR : Semestre.IMPAIR;
    }

    /**
     * Marque le résultat d'une UE pour l'inscription du semestre courant.
     * @param etudiant l'étudiant concerné
     * @param ue l'UE à marquer
     * @param valide true si validée, false si échouée
     * @return true si une inscription a été trouvée et mise à jour
     */
    public boolean marquerResultat(Etudiant etudiant, UE ue, boolean valide) {
        for (Inscription ins : etudiant.getInscriptions()) {
            if (ins.getUe().equals(ue)
                    && ins.getAnneeUniversitaire().equals(anneeCourante)
                    && ins.getSemestre() == semestreCourant) {
                ins.setValide(valide);
                return true;
            }
        }
        return false;
    }
}