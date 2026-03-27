package dao;

import model.Mention;
import model.Parcours;
import model.UE;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO pour les Mentions et Parcours.
 * Utilise le NOM comme identifiant Java (pas l'id numérique Oracle).
 */
public class MentionDAO {

    private final Connection connection;

    public MentionDAO(Connection connection) {
        this.connection = connection;
    }

    /**
     * Charge toutes les mentions avec leurs parcours.
     * Le code Java = nom Oracle (ex: "MIASHS", "MIAGE", "IO").
     *
     * @param ues liste des UE déjà chargées (non utilisé ici, conservé pour compatibilité)
     * @return liste de toutes les mentions
     * @throws SQLException en cas d'erreur base de données
     */
    public List<Mention> findAll(List<UE> ues) throws SQLException {
        List<Mention> mentions = new ArrayList<>();

        // 1. Charger les mentions — code Java = nom Oracle
        String sqlMention = "SELECT id_mention, nom FROM MENTION ORDER BY nom";
        try (PreparedStatement stmt = connection.prepareStatement(sqlMention);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                // on stocke l'id Oracle dans une map locale pour la FK parcours
                String nom = rs.getString("nom");
                mentions.add(new Mention(nom, nom)); // code = nom (ex: "MIASHS")
            }
        }

        // 2. Charger les parcours avec JOIN pour récupérer nom_mention
        String sqlParcours =
                "SELECT p.nom AS parcours_nom, m.nom AS mention_nom " +
                        "FROM PARCOURS p " +
                        "JOIN MENTION m ON p.id_mention = m.id_mention " +
                        "ORDER BY p.nom";

        try (PreparedStatement stmt = connection.prepareStatement(sqlParcours);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                String parcoursNom = rs.getString("parcours_nom");
                String mentionNom  = rs.getString("mention_nom");

                Mention mention = findMentionByNom(mentions, mentionNom);
                if (mention != null) {
                    Parcours p = new Parcours(parcoursNom, parcoursNom, mention);
                    mention.ajouterParcours(p);
                }
            }
        }

        return mentions;
    }

    /**
     * Retourne la liste à plat de tous les parcours
     * à partir des mentions déjà chargées.
     */
    public List<Parcours> getAllParcours(List<Mention> mentions) {
        List<Parcours> result = new ArrayList<>();
        for (Mention m : mentions) {
            result.addAll(m.getParcours());
        }
        return result;
    }

    // ── utilitaires privés ──────────────────────────────────────
    private Mention findMentionByNom(List<Mention> mentions, String nom) {
        for (Mention m : mentions) {
            if (m.getNom().equals(nom)) return m;
        }
        return null;
    }
}