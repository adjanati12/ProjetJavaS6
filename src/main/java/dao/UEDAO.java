package dao;

import model.Mention;
import model.UE;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO pour les Unités d'Enseignement.
 * Fournit les opérations CRUD sur la table UE et UE_PREREQUIS.
 */
public class UEDAO {

    private final Connection connection;

    /**
     * @param connection connexion JDBC active
     */
    public UEDAO(Connection connection) {
        this.connection = connection;
    }

    /**
     * Charge toutes les UE depuis la base, avec leurs prérequis.
     * @return liste de toutes les UE
     * @throws SQLException en cas d'erreur base de données
     */
    public List<UE> findAll() throws SQLException {
        List<UE> ues = new ArrayList<>();

        // Charger toutes les UE (mention null pour l'instant = UE d'ouverture par défaut)
        String sql = "SELECT code, nom, ects FROM UE ORDER BY code";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                ues.add(new UE(
                        rs.getString("code"),
                        rs.getString("nom"),
                        rs.getInt("ects"),
                        null // mention chargée séparément si besoin
                ));
            }
        }

        // Charger les prérequis pour chaque UE
        String sqlPre = "SELECT code_ue, code_prerequis FROM UE_PREREQUIS";
        try (PreparedStatement stmt = connection.prepareStatement(sqlPre);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                String codeUE  = rs.getString("code_ue");
                String codePre = rs.getString("code_prerequis");

                UE ue  = findInList(ues, codeUE);
                UE pre = findInList(ues, codePre);
                if (ue != null && pre != null) {
                    ue.ajouterPrerequis(pre);
                }
            }
        }

        return ues;
    }

    /**
     * Trouve une UE par son code dans une liste.
     * @param ues liste de UE
     * @param code code recherché
     * @return la UE trouvée, ou null
     */
    private UE findInList(List<UE> ues, String code) {
        for (UE ue : ues) {
            if (ue.getCode().equals(code)) return ue;
        }
        return null;
    }
}