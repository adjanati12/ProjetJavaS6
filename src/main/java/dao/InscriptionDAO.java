package dao;

import model.Etudiant;
import model.Inscription;
import model.Semestre;
import model.UE;

import java.sql.*;

/*
 * DAO pour les inscriptions.
 * Permet d'inscrire un étudiant à une UE et de mettre à jour les résultats.
 */
public class InscriptionDAO {

    private final Connection connection;


     // @param connection connexion JDBC active
    public InscriptionDAO(Connection connection) {
        this.connection = connection;
    }

    /**
     * Insère une nouvelle inscription en base
     * @param etudiant l'étudiant concerné
     * @param ue       l'UE à laquelle il s'inscrit
     * @param annee    l'année universitaire (ex: "2025-2026")
     * @param semestre IMPAIR ou PAIR
     * @throws SQLException si l'inscription existe déjà ou erreur BDD
     */
    public void inscrire(Etudiant etudiant, UE ue, String annee, Semestre semestre)
            throws SQLException {
        String sql =
                "INSERT INTO INSCRIPTION " +
                        "  (numero_etudiant, code_ue, annee_universitaire, semestre, valide) " +
                        "VALUES (?, ?, ?, ?, 0)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, etudiant.getNumero());
            stmt.setString(2, ue.getCode());
            stmt.setString(3, annee);
            stmt.setString(4, semestre.name());
            stmt.executeUpdate();
        }
    }

    /**
     * Met à jour le résultat (validé ou échoué) d'une inscription.
     * @param etudiant l'étudiant
     * @param ue       l'UE concernée
     * @param annee    l'année universitaire
     * @param semestre le semestre
     * @param valide   true = validé, false = échoué
     * @throws SQLException en cas d'erreur base de données
     */
    public void mettreAJourResultat(Etudiant etudiant, UE ue,
                                    String annee, Semestre semestre,
                                    boolean valide) throws SQLException {
        String sql =
                "UPDATE INSCRIPTION " +
                        "SET valide = ? " +
                        "WHERE numero_etudiant = ? " +
                        "  AND code_ue = ? " +
                        "  AND annee_universitaire = ? " +
                        "  AND semestre = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt   (1, valide ? 1 : 0);
            stmt.setString(2, etudiant.getNumero());
            stmt.setString(3, ue.getCode());
            stmt.setString(4, annee);
            stmt.setString(5, semestre.name());
            stmt.executeUpdate();
        }
    }

    /**
     * Supprime une inscription (ex : si l'étudiant se désinscrit).
     * @param etudiant l'étudiant
     * @param ue       l'UE
     * @param annee    l'année universitaire
     * @param semestre le semestre
     * @throws SQLException en cas d'erreur base de données
     */
    public void supprimer(Etudiant etudiant, UE ue,
                          String annee, Semestre semestre) throws SQLException {
        String sql =
                "DELETE FROM INSCRIPTION " +
                        "WHERE numero_etudiant = ? " +
                        "  AND code_ue = ? " +
                        "  AND annee_universitaire = ? " +
                        "  AND semestre = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, etudiant.getNumero());
            stmt.setString(2, ue.getCode());
            stmt.setString(3, annee);
            stmt.setString(4, semestre.name());
            stmt.executeUpdate();
        }
    }
}
