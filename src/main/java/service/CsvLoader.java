package service;

import model.*;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Service de chargement des données depuis les fichiers CSV.
 */
public class CsvLoader {

    /**
     * Charge les mentions depuis le fichier mentions.csv dans le service.
     * @param service le service étudiant
     */
    public static void chargerMentions(EtudiantService service) {
        try (InputStream is = CsvLoader.class.getResourceAsStream("/data/mentions.csv");
             BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            String ligne;
            boolean premiere = true;
            while ((ligne = br.readLine()) != null) {
                if (premiere) { premiere = false; continue; }
                String[] parts = ligne.split(",");
                if (parts.length >= 2) {
                    String code = parts[0].trim();
                    String nom = parts[1].trim();
                    if (service.getMentionParCode(code) == null) {
                        service.getMentions().add(new Mention(code, nom));
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Erreur chargement mentions : " + e.getMessage());
        }
    }

    /**
     * Charge les parcours depuis le fichier parcours.csv dans le service.
     * @param service le service étudiant
     */
    public static void chargerParcours(EtudiantService service) {
        try (InputStream is = CsvLoader.class.getResourceAsStream("/data/parcours.csv");
             BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            String ligne;
            boolean premiere = true;
            while ((ligne = br.readLine()) != null) {
                if (premiere) { premiere = false; continue; }
                String[] parts = ligne.split(",");
                if (parts.length >= 3) {
                    String code = parts[0].trim();
                    String nom = parts[1].trim();
                    String codeMention = parts[2].trim();
                    Mention mention = service.getMentionParCode(codeMention);
                    if (mention != null && service.getParcoursParCode(code) == null) {
                        Parcours p = new Parcours(code, nom, mention);
                        // Charger les UE obligatoires si présentes
                        if (parts.length >= 4 && !parts[3].trim().isEmpty()) {
                            String[] codesUE = parts[3].trim().split(";");
                            for (String codeUE : codesUE) {
                                UE ue = service.getUeParCode(codeUE.trim());
                                if (ue != null) p.ajouterUeObligatoire(ue);
                            }
                        }
                        mention.ajouterParcours(p);
                        service.getParcours().add(p);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Erreur chargement parcours : " + e.getMessage());
        }
    }

    /**
     * Charge les UE depuis le fichier ues.csv dans le service.
     * @param service le service étudiant
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
                    // La mention est optionnelle (UE d'ouverture si absente)
                    Mention mention = null;
                    if (parts.length >= 4 && !parts[3].trim().isEmpty()) {
                        mention = service.getMentionParCode(parts[3].trim());
                    }
                    if (service.getUeParCode(code) == null) {
                        service.getUes().add(new UE(code, nom, credits, mention));
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Erreur chargement UEs : " + e.getMessage());
        }
    }

    /**
     * Charge les étudiants depuis le fichier etudiants.csv dans le service.
     * @param service le service étudiant
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
                    // Le parcours est optionnel
                    Parcours parcours = null;
                    if (parts.length >= 4 && !parts[3].trim().isEmpty()) {
                        parcours = service.getParcoursParCode(parts[3].trim());
                    }
                    if (!service.numeroExisteDeja(numero)) {
                        service.ajouterEtudiant(new Etudiant(numero, nom, prenom, parcours));
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Erreur chargement étudiants : " + e.getMessage());
        }
    }

    /**
     * Charge les inscriptions depuis le fichier inscriptions.csv dans le service.
     * @param service le service étudiant
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