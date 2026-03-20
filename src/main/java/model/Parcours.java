package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Représente un parcours au sein d'une mention (ex: MIAGE dans MIASHS).
 */
public class Parcours {

    private String code;
    private String nom;
    private Mention mention;
    private List<UE> uesObligatoires = new ArrayList<>();

    /**
     * @param code code du parcours (ex: MIAGE)
     * @param nom nom complet du parcours
     * @param mention la mention à laquelle appartient ce parcours
     */
    public Parcours(String code, String nom, Mention mention) {
        this.code = code;
        this.nom = nom;
        this.mention = mention;
    }

    /** @return le code du parcours */
    public String getCode() { return code; }

    /** @return le nom du parcours */
    public String getNom() { return nom; }

    /** @return la mention associée */
    public Mention getMention() { return mention; }

    /** @return la liste des UE obligatoires pour valider ce parcours */
    public List<UE> getUesObligatoires() { return uesObligatoires; }

    /**
     * Ajoute une UE obligatoire au parcours.
     * @param ue l'UE à ajouter
     */
    public void ajouterUeObligatoire(UE ue) {
        uesObligatoires.add(ue);
    }

    /**
     * Vérifie si un étudiant a validé toutes les UE obligatoires du parcours.
     * @param etudiant l'étudiant à vérifier
     * @return true si toutes les UE obligatoires sont validées
     */
    public boolean parcourValide(Etudiant etudiant) {
        for (UE ue : uesObligatoires) {
            if (!etudiant.aValide(ue)) return false;
        }
        return true;
    }

    @Override
    public String toString() { return code + " - " + nom; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Parcours)) return false;
        Parcours parcours = (Parcours) o;
        return Objects.equals(code, parcours.code);
    }

    @Override
    public int hashCode() { return Objects.hash(code); }
}