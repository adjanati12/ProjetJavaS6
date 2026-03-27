package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Classe représentant un étudiant.
 */
public class Etudiant {

    private String numero;
    private String nom;
    private String prenom;
    private Parcours parcours;
    private List<Inscription> inscriptions = new ArrayList<>();

    /**
     * Constructeur Etudiant avec parcours
     * @param numero numéro étudiant
     * @param nom nom de l'étudiant
     * @param prenom prénom de l'étudiant
     * @param parcours parcours dans lequel est inscrit l'étudiant
     */
    public Etudiant(String numero, String nom, String prenom, Parcours parcours) {
        this.numero = numero;
        this.nom = nom;
        this.prenom = prenom;
        this.parcours = parcours;
    }

    /**
     * Constructeur sans parcours (parcours non encore défini)
     * @param numero numéro étudiant
     * @param nom nom de l'étudiant
     * @param prenom prénom de l'étudiant
     */
    public Etudiant(String numero, String nom, String prenom) {
        this(numero, nom, prenom, null);
    }

    /** Ajoute une inscription à la liste
     * @param inscription inscription à ajouter */
    public void ajouterInscription(Inscription inscription) {
        inscriptions.add(inscription);
    }

    /** @return la liste des inscriptions */
    public List<Inscription> getInscriptions() { return inscriptions; }

    /** @return le numéro étudiant */
    public String getNumero() { return numero; }

    /** @return le nom */
    public String getNom() { return nom; }

    /** @return le prénom */
    public String getPrenom() { return prenom; }

    /** @return le parcours de l'étudiant, peut être null */
    public Parcours getParcours() { return parcours; }

    /** @param nom le nouveau nom */
    public void setNom(String nom) { this.nom = nom; }

    /** @param prenom le nouveau prénom */
    public void setPrenom(String prenom) { this.prenom = prenom; }

    /** @param parcours le nouveau parcours */
    public void setParcours(Parcours parcours) { this.parcours = parcours; }

    /** @return le nom complet */
    public String getNomComplet() { return prenom + " " + nom; }

    /** @return true si l'étudiant a validé l'UE */
    // Dans Etudiant.java — version avec équivalences
    public boolean aValide(UE ue) {
        for (Inscription inscription : inscriptions) {
            // UE directement validée
            if (inscription.getUe().equals(ue) && inscription.isValide()) {
                return true;
            }
            // UE équivalente validée
            for (UE equiv : ue.getEquivalences()) {
                if (inscription.getUe().equals(equiv) && inscription.isValide()) {
                    return true;
                }
            }
        }
        return false;
    }

    /** @return le total des ECTS validés */
    public int calculerECTSValides() {
        int total = 0;
        for (Inscription inscription : inscriptions) {
            if (inscription.isValide()) {
                total += inscription.getUe().getEcts();
            }
        }
        return total;
    }

    /**
     * Vérifie si l'étudiant a obtenu son diplôme :
     * 180 ECTS validés ET toutes les UE obligatoires du parcours validées.
     * @return true si le diplôme est obtenu
     */
    public boolean diplomeObtenu() {
        if (parcours == null) return false;
        return calculerECTSValides() >= 180 && parcours.parcourValide(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Etudiant)) return false;
        Etudiant etudiant = (Etudiant) o;
        return Objects.equals(numero, etudiant.numero);
    }

    @Override
    public int hashCode() { return Objects.hash(numero); }
}

