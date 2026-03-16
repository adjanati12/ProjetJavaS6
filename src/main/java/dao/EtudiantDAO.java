package dao;

import model.Etudiant;
import model.Inscription;
import model.Semestre;
import model.UE;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/*
 * DAO pour les étudiants.
 Gère la lecture, l'ajout, la modification et la suppression,
 dans les tables ETUDIANT et INSCRIPTION.
 */
public class EtudiantDAO {

    private final Connection connection;

    /**
     * @param connection connexion JDBC active
     */
    public EtudiantDAO(Connection connection) {
        this.connection = connection;
    }

    /**
     * Charge tous les étudiants avec leurs inscriptions.
     * @param ues liste complète des UE (déjà chargées) pour résoudre les FK
     * @return liste de tous les étudiants
     * @throws SQLException en cas d'erreur base de données
     */
    public List<Etudiant> findAll(List<UE> ues) throws SQLException {
        List<Etudiant> etudiants = new ArrayList<>();

        //Charger les étudiants
        String sql = "SELECT numero, nom, prenom FROM ETUDIANT ORDER BY nom, prenom";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                etudiants.add(new Etudiant(
                        rs.getString("numero"),
                        rs.getString("nom"),
                        rs.getString("prenom")
                ));
            }
        }

        //Charger les inscriptions pour chaque étudiant
        String sqlInsc =
                "SELECT numero_etudiant, code_ue, annee_universitaire, semestre, valide " +
                        "FROM INSCRIPTION " +
                        "ORDER BY numero_etudiant, annee_universitaire, semestre";

        try (PreparedStatement stmt = connection.prepareStatement(sqlInsc);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                String  numEtu  = rs.getString("numero_etudiant");
                String  codeUE  = rs.getString("code_ue");
                String  annee   = rs.getString("annee_universitaire");
                Semestre sem    = Semestre.valueOf(rs.getString("semestre"));
                boolean valide  = rs.getInt("valide") == 1;

                // Retrouver l'étudiant et l'UE dans les listes en mémoire
                Etudiant etudiant = findInList(etudiants, numEtu);
                UE ue = findUE(ues, codeUE);

                if (etudiant != null && ue != null) {
                    Inscription insc = new Inscription(ue, annee, sem);
                    insc.setValide(valide);
                    etudiant.ajouterInscription(insc);
                }
            }
        }

        return etudiants;
    }

    /**
     * Insère un nouvel étudiant en base.
     *
     * @param etudiant l'étudiant à insérer
     * @throws SQLException en cas d'erreur ou si le numéro existe déjà
     */
    public void insert(Etudiant etudiant) throws SQLException {
        String sql = "INSERT INTO ETUDIANT (numero, nom, prenom) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, etudiant.getNumero());
            stmt.setString(2, etudiant.getNom());
            stmt.setString(3, etudiant.getPrenom());
            stmt.executeUpdate();
        }
    }

    /*
     * Met à jour le nom et prénom d'un étudiant existant
     * @param etudiant l'étudiant avec les nouvelles valeurs
     * @throws SQLException en cas d'erreur base de données
     */
    public void update(Etudiant etudiant) throws SQLException {
        String sql = "UPDATE ETUDIANT SET nom = ?, prenom = ? WHERE numero = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, etudiant.getNom());
            stmt.setString(2, etudiant.getPrenom());
            stmt.setString(3, etudiant.getNumero());
            stmt.executeUpdate();
        }
    }

    /*
     * Supprime un étudiant et toutes ses inscriptions (CASCADE).
     * @param numero le numéro étudiant à supprimer
     * @throws SQLException en cas d'erreur base de données
     */
    public void delete(String numero) throws SQLException {
        // Les inscriptions sont supprimées automatiquement par CASCADE CONSTRAINTS
        String sql = "DELETE FROM ETUDIANT WHERE numero = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, numero);
            stmt.executeUpdate();
        }
    }


    //  Méthodes utilitaires privées
    private Etudiant findInList(List<Etudiant> etudiants, String numero) {
        for (Etudiant e : etudiants) {
            if (e.getNumero().equals(numero)) return e;
        }
        return null;
    }

    private UE findUE(List<UE> ues, String code) {
        for (UE ue : ues) {
            if (ue.getCode().equals(code)) return ue;
        }
        return null;
    }
}
