package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Représente une mention universitaire (ex: MIASHS).
 */
public class Mention {

    private String code;
    private String nom;
    private List<Parcours> parcours = new ArrayList<>();

    /**
     * @param code code de la mention (ex: MIASHS)
     * @param nom nom complet de la mention
     */
    public Mention(String code, String nom) {
        this.code = code;
        this.nom = nom;
    }

    /** @return le code de la mention */
    public String getCode() { return code; }

    /** @return le nom de la mention */
    public String getNom() { return nom; }

    /** @return la liste des parcours de cette mention */
    public List<Parcours> getParcours() { return parcours; }

    /**
     * Ajoute un parcours à la mention.
     * @param p le parcours à ajouter
     */
    public void ajouterParcours(Parcours p) {
        parcours.add(p);
    }

    @Override
    public String toString() { return code + " - " + nom; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Mention)) return false;
        Mention mention = (Mention) o;
        return Objects.equals(code, mention.code);
    }

    @Override
    public int hashCode() { return Objects.hash(code); }
}