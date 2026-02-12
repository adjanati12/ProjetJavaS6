package model;

import java.util.ArrayList;
import java.util.List;

public class Etudiant {

    private String numero;
    private String nom;
    private String prenom;
    private List<Inscription> inscriptions = new ArrayList<>();

    public Etudiant(String numero, String nom, String prenom) {
        this.numero = numero;
        this.nom = nom;
        this.prenom = prenom;
    }

    public void ajouterInscription(Inscription inscription) {
        inscriptions.add(inscription);
    }

    public List<Inscription> getInscriptions() {
        return inscriptions;
    }

    public String getNumero() {
        return numero;
    }

    public String getNomComplet() {
        return prenom + " " + nom;
    }

    public boolean aValide(UE ue) {
        for (Inscription inscription : inscriptions) {
            if (inscription.getUe().equals(ue) && inscription.isValide()) {
                return true;
            }
        }
        return false;
    }
}
public int calculerECTSValides() {
    int total = 0;
    for (Inscription inscription : inscriptions) {
        if (inscription.isValide()) {
            total += inscription.getUe().getEcts();
        }
    }
    return total;
}
