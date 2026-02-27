package service;

import model.*;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Service de chargement des données depuis les fichiers CSV.
 */
public class CsvLoader {

    public static void chargerUEs(EtudiantService service) {
        try (InputStream is = CsvLoader.class.getResourceAsStream("/data/ues.csv");
             BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            String ligne;
            boolean premiere = true;
            while ((ligne = br.readLine()) != null) {
                if (premiere) { premiere = false; continue; }
                String[] parts = ligne.split(",");
                if (parts.length >= 3) {
                    String code = parts[0].trim();
                    String nom = parts[1].trim();
                    int credits = Integer.parseInt(parts[2].trim());
                    if (service.getUeParCode(code) == null) {
                        service.getUes().add(new UE(code, nom, credits));
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Erreur chargement UEs : " + e.getMessage());
        }
    }

    public static void chargerEtudiants(EtudiantService service) {
        try (InputStream is = CsvLoader.class.getResourceAsStream("/data/etudiants.csv");
             BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"))) {
            String ligne;
            boolean premiere = true;
            while ((ligne = br.readLine()) != null) {
                if (premiere) { premiere = false; continue; }
                String[] parts = ligne.split(",");
                if (parts.length >= 3) {
                    String numero = parts[0].trim();
                    String nom = parts[1].trim();
                    String prenom = parts[2].trim();
                    if (!service.numeroExisteDeja(numero)) {
                        service.ajouterEtudiant(new Etudiant(numero, nom, prenom));
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Erreur chargement etudiants : " + e.getMessage());
        }
    }

    public static void chargerInscriptions(EtudiantService service) {
        chargerInscriptionsFichier(service, "/data/inscriptions.csv");
    }

    public static void chargerInscriptionsFichier(EtudiantService service, String fichier) {
        try (InputStream is = CsvLoader.class.getResourceAsStream(fichier);
             BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            String ligne;
            boolean premiere = true;
            while ((ligne = br.readLine()) != null) {
                if (premiere) { premiere = false; continue; }
                String[] parts = ligne.split(",");
                if (parts.length >= 4) {
                    String numero = parts[0].trim();
                    String codeUE = parts[1].trim();
                    String annee = parts[2].trim();
                    Semestre semestre = Semestre.valueOf(parts[3].trim());
                    boolean valide = parts.length > 4 && parts[4].trim().equals("true");
                    boolean echouee = parts.length > 5 && parts[5].trim().equals("true");

                    Etudiant etudiant = service.getEtudiantParNumero(numero);
                    UE ue = service.getUeParCode(codeUE);
                    if (etudiant != null && ue != null) {
                        Inscription ins = new Inscription(ue, annee, semestre);
                        ins.setValide(valide);
                        if (!valide) ins.setEchouee(echouee);
                        etudiant.ajouterInscription(ins);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Erreur chargement inscriptions : " + e.getMessage());
        }
    }
}
