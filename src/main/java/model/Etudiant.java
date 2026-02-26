package model;
import java.util.Objects;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe représentant un étudiant.
 */
public class Etudiant {
    private String numero;
    private String nom;
    private String prenom;
    private List<Inscription> inscriptions = new ArrayList<>();

    /**
     * Constructeur Etudiant
     * @param numero numéro étudiant
     * @param nom nom de l'étudiant
     * @param prenom prénom de l'étudiant
     */
    public Etudiant(String numero, String nom, String prenom) {
        this.numero = numero;
        this.nom = nom;
        this.prenom = prenom;
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

    /** @param nom le nouveau nom */
    public void setNom(String nom) { this.nom = nom; }

    /** @param prenom le nouveau prénom */
    public void setPrenom(String prenom) { this.prenom = prenom; }

    /** @return le nom complet */
    public String getNomComplet() { return prenom + " " + nom; }

    /** @return true si l'étudiant a validé l'UE */
    public boolean aValide(UE ue) {
        for (Inscription inscription : inscriptions) {
            if (inscription.getUe().equals(ue) && inscription.isValide()) {
                return true;
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

    /** @return true si 180 ECTS validés */
    public boolean diplomeObtenu() {
        return calculerECTSValides() >= 180;
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
