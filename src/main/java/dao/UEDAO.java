package dao;

import model.Mention;
import model.UE;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO pour les Unités d'Enseignement.
 * Charge les UE avec leur mention (via JOIN) et leurs prérequis.
 */
public class UEDAO {

    private final Connection connection;

    public UEDAO(Connection connection) {
        this.connection = connection;
    }

    /**
     * Charge toutes les UE avec leur mention et leurs prérequis.
     * Utilise un JOIN pour récupérer le nom de la mention directement.
     *
     * @param mentions liste des mentions déjà chargées (code = nom)
     * @return liste de toutes les UE
     * @throws SQLException en cas d'erreur base de données
     */
    public List<UE> findAll(List<Mention> mentions) throws SQLException {
        List<UE> ues = new ArrayList<>();

        // JOIN avec MENTION pour récupérer le nom directement
        String sql =
                "SELECT u.code, u.nom, u.ects, m.nom AS mention_nom " +
                        "FROM UE u " +
                        "LEFT JOIN MENTION m ON u.id_mention = m.id_mention " +
                        "ORDER BY u.code";

        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                String mentionNom = rs.getString("mention_nom"); // null si UE d'ouverture
                Mention mention = null;
                if (mentionNom != null) {
                    mention = findMentionByNom(mentions, mentionNom);
                }
                ues.add(new UE(
                        rs.getString("code"),
                        rs.getString("nom"),
                        rs.getInt("ects"),
                        mention
                ));
                String sqlEquiv = "SELECT code_ue_source, code_ue_cible FROM UE_EQUIVALENCE";
                try (PreparedStatement stm = connection.prepareStatement(sqlEquiv);
                     ResultSet r = stm.executeQuery()) {
                    while (rs.next()) {
                        UE source = findInList(ues, r.getString("code_ue_source"));
                        UE cible  = findInList(ues, r.getString("code_ue_cible"));
                        if (source != null && cible != null) {
                            source.ajouterEquivalence(cible);
                        }
                    }
                }
            }
        }

        // Charger les prérequis
        String sqlPre = "SELECT code_ue, code_prerequis FROM UE_PREREQUIS";
        try (PreparedStatement stmt = connection.prepareStatement(sqlPre);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                UE ue  = findInList(ues, rs.getString("code_ue"));
                UE pre = findInList(ues, rs.getString("code_prerequis"));
                if (ue != null && pre != null) {
                    ue.ajouterPrerequis(pre);
                }
            }
        }

        return ues;
    }

    // surcharge sans mentions
    public List<UE> findAll() throws SQLException {
        return findAll(new ArrayList<>());
    }

    // ── utilitaires privés ────────────────────────────────────────
    private UE findInList(List<UE> ues, String code) {
        for (UE ue : ues) {
            if (ue.getCode().equals(code)) return ue;
        }
        return null;
    }

    private Mention findMentionByNom(List<Mention> mentions, String nom) {
        for (Mention m : mentions) {
            if (m.getNom().equals(nom)) return m;
        }
        return null;
    }
}