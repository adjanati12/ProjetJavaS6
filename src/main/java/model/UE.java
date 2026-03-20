package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Classe représentant une UE (Unité d'Enseignement).
 */
public class UE {

    private String code;
    private String nom;
    private int ects;
    private Mention mention; // null si UE d'ouverture
    private List<UE> prerequis = new ArrayList<>();

    /**
     * Constructeur UE
     * @param code code de l'UE
     * @param nom nom de l'UE
     * @param ects crédits ECTS de l'UE
     * @param mention mention associée, null si UE d'ouverture
     */
    public UE(String code, String nom, int ects, Mention mention) {
        this.code = code;
        this.nom = nom;
        this.ects = ects;
        this.mention = mention;
    }

    /** @return le code */
    public String getCode() { return code; }

    /** @return le nom */
    public String getNom() { return nom; }

    /** @return les crédits ECTS */
    public int getEcts() { return ects; }

    /** @return la mention associée, null si UE d'ouverture */
    public Mention getMention() { return mention; }

    /** @return true si c'est une UE d'ouverture (sans mention) */
    public boolean isUeOuverture() { return mention == null; }

    /** @return la liste des prérequis */
    public List<UE> getPrerequis() { return prerequis; }

    /**
     * Ajoute un prérequis à la liste
     * @param ue prérequis à ajouter
     */
    public void ajouterPrerequis(UE ue) {
        prerequis.add(ue);
    }

    @Override
    public String toString() { return code + " - " + nom; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UE)) return false;
        UE ue = (UE) o;
        return Objects.equals(code, ue.code);
    }

    @Override
    public int hashCode() { return Objects.hash(code); }
}