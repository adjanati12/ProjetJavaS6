package model;
/**
 * Inscription d'un etudiant a une UE.
 */
public class Inscription {
    private UE ue;
    private String anneeUniversitaire;
    private Semestre semestre;
    private boolean valide;
    private boolean echouee;

    /**
     * @param ue l'UE
     * @param anneeUniversitaire ex: 2025-2026
     * @param semestre pair ou impair
     */
    public Inscription(UE ue, String anneeUniversitaire, Semestre semestre) {
        this.ue = ue;
        this.anneeUniversitaire = anneeUniversitaire;
        this.semestre = semestre;
        this.valide = false;
        this.echouee = false;
    }

    public UE getUe() { return ue; }
    public String getAnneeUniversitaire() { return anneeUniversitaire; }
    public Semestre getSemestre() { return semestre; }
    public boolean isValide() { return valide; }
    public boolean isEchouee() { return echouee; }

    public void setValide(boolean valide) {
        this.valide = valide;
        if (valide) this.echouee = false;
    }

    public void setEchouee(boolean echouee) {
        this.echouee = echouee;
        if (echouee) this.valide = false;
    }

    @Override
    public String toString() {
        return ue + " - " + anneeUniversitaire + " - " + semestre;
    }
}
