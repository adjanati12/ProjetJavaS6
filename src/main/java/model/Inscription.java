package model;

/**
 * Inscription d'un étudiant à une UE.
 */
public class Inscription {

    private UE ue;
    private String anneeUniversitaire;
    private Semestre semestre;
    private boolean valide;

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
    }

    public UE getUe() { return ue; }

    public String getAnneeUniversitaire() { return anneeUniversitaire; }

    public Semestre getSemestre() { return semestre; }

    public boolean isValide() { return valide; }

    public void setValide(boolean valide) { this.valide = valide; }

    @Override
    public String toString() {
        return ue + " - " + anneeUniversitaire + " - " + semestre;
    }
}