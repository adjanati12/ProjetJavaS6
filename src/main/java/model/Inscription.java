package model;

public class Inscription {
    private UE ue;
    private String anneeUniversitaire;
    private Semestre semestre;
    private boolean valide;

    public Inscription(UE ue, String anneeUniversitaire, Semestre semestre) {
        this.ue = ue;
        this.anneeUniversitaire = anneeUniversitaire;
        this.semestre = semestre;
        this.valide = false;
    }

    public UE getUe() {
        return ue;
    }

    public boolean isValide() {
        return valide;
    }

    public void setValide(boolean valide) {
        this.valide = valide;
    }
}


