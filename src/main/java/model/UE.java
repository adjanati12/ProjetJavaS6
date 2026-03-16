package model;
import java.util.Objects;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe représentant une UE (Unité d'Enseignement).
 */
public class UE {

    private String code;
    private String nom;
    private int ects;
    private List<UE> prerequis = new ArrayList<>();

    /**
     * Constructeur UE
     * @param code code de l'UE
     * @param nom nom de l'UE
     * @param ects crédits ECTS de l'UE
     */
    public UE(String code, String nom, int ects) {
        this.code = code;
        this.nom = nom;
        this.ects = ects;
    }

    /** @return le code */
    public String getCode() { return code; }

    /** @return le nom */
    public String getNom() { return nom; }

    /** @return les crédits ECTS */
    public int getEcts() { return ects; }

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
    public String toString() {
        return code + " - " + nom;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UE)) return false;
        UE ue = (UE) o;
        return Objects.equals(code, ue.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code);
    }

}