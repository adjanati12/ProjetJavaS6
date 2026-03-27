package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class UE {

    private String code;
    private String nom;
    private int ects;
    private Mention mention;
    private List<UE> prerequis    = new ArrayList<>();
    private List<UE> equivalences = new ArrayList<>();

    public UE(String code, String nom, int ects, Mention mention) {
        this.code    = code;
        this.nom     = nom;
        this.ects    = ects;
        this.mention = mention;
    }

    public String getCode()            { return code; }
    public String getNom()             { return nom; }
    public int getEcts()               { return ects; }
    public Mention getMention()        { return mention; }
    public boolean isUeOuverture()     { return mention == null; }
    public List<UE> getPrerequis()     { return prerequis; }
    public List<UE> getEquivalences()  { return equivalences; }

    public void ajouterPrerequis(UE ue) {
        prerequis.add(ue);
    }

    public void ajouterEquivalence(UE ue) {
        if (!equivalences.contains(ue)) equivalences.add(ue);
    }

    @Override public String toString() { return code + " - " + nom; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UE)) return false;
        return Objects.equals(code, ((UE) o).code);
    }

    @Override public int hashCode() { return Objects.hash(code); }
}