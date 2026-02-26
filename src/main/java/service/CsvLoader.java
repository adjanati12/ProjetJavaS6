package service;

import model.*;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

/**
 * Service de chargement des données depuis les fichiers CSV.
 */
public class CsvLoader {

    /**
     * Charge les UE depuis le fichier ues.csv dans le service.
     */
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
                    // Remplace l'UE existante si elle existe déjà
                    if (service.getUeParCode(code) == null) {
                        service.getUes().add(new UE(code, nom, credits));
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Erreur chargement UEs : " + e.getMessage());
        }
    }

    /**
     * Charge les étudiants depuis le fichier etudiants.csv dans le service.
     */
    public static void chargerEtudiants(EtudiantService service) {
        try (InputStream is = CsvLoader.class.getResourceAsStream("/data/etudiants.csv");
             BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
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
            System.err.println("Erreur chargement étudiants : " + e.getMessage());
        }
    }

    /**
     * Charge les inscriptions depuis le fichier inscriptions.csv dans le service.
     */
    public static void chargerInscriptions(EtudiantService service) {
        try (InputStream is = CsvLoader.class.getResourceAsStream("/data/inscriptions.csv");
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

                    Etudiant etudiant = service.getEtudiantParNumero(numero);
                    UE ue = service.getUeParCode(codeUE);
                    if (etudiant != null && ue != null) {
                        service.inscrire(etudiant, ue, annee, semestre);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Erreur chargement inscriptions : " + e.getMessage());
        }
    }
}
