package model;

import java.util.ArrayList;

public class UE {

    private String code;
    private String nom;
    private int ects;
    private ArrayList<UE> prerequis = new ArrayList<>();

    public UE(String code, String nom, int ects) {
        this.code = code;
        this.nom = nom;
        this.ects = ects;
    }

    public String getCode() {
        return code;
    }

    public String getNom() {
        return nom;
    }

    public int getEcts() {
        return ects;
    }

    public ArrayList<UE> getPrerequis() {
        return prerequis;
    }

    public void ajouterPrerequis(UE ue) {
        prerequis.add(ue);
    }

    @Override
    public String toString() {
        return code + " - " + nom;
    }
}