package dao;

import model.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO pour les étudiants.
 * Charge les étudiants avec leur parcours (via JOIN) et leurs inscriptions.
 */
public class EtudiantDAO {

    private final Connection connection;

    public EtudiantDAO(Connection connection) {
        this.connection = connection;
    }

    /**
     * Charge tous les étudiants avec leur parcours et leurs inscriptions.
     * Utilise un JOIN pour récupérer le nom du parcours directement.
     *
     * @param ues      liste des UE déjà chargées
     * @param parcours liste des parcours déjà chargés (code = nom)
     * @return liste de tous les étudiants
     * @throws SQLException en cas d'erreur base de données
     */
    public List<Etudiant> findAll(List<UE> ues, List<Parcours> parcours) throws SQLException {
        List<Etudiant> etudiants = new ArrayList<>();

        // JOIN avec PARCOURS pour récupérer le nom directement
        String sql =
                "SELECT e.numero, e.nom, e.prenom, p.nom AS parcours_nom " +
                        "FROM ETUDIANT e " +
                        "LEFT JOIN PARCOURS p ON e.id_parcours = p.id_parcours " +
                        "ORDER BY e.nom, e.prenom";

        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                String parcoursNom = rs.getString("parcours_nom");
                Parcours p = null;
                if (parcoursNom != null) {
                    p = findParcoursByNom(parcours, parcoursNom);
                }
                etudiants.add(new Etudiant(
                        rs.getString("numero"),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        p
                ));
            }
        }

        // Charger les inscriptions
        String sqlInsc =
                "SELECT numero_etudiant, code_ue, annee_universitaire, semestre, valide " +
                        "FROM INSCRIPTION " +
                        "ORDER BY numero_etudiant, annee_universitaire, semestre";

        try (PreparedStatement stmt = connection.prepareStatement(sqlInsc);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Etudiant etudiant = findInList(etudiants, rs.getString("numero_etudiant"));
                UE ue             = findUE(ues, rs.getString("code_ue"));
                if (etudiant != null && ue != null) {
                    Inscription insc = new Inscription(
                            ue,
                            rs.getString("annee_universitaire"),
                            Semestre.valueOf(rs.getString("semestre"))
                    );
                    insc.setValide(rs.getInt("valide") == 1);
                    etudiant.ajouterInscription(insc);
                }
            }
        }

        return etudiants;
    }

    // surcharge rétrocompatibilité
    public List<Etudiant> findAll(List<UE> ues) throws SQLException {
        return findAll(ues, new ArrayList<>());
    }

    // ── opérations CRUD ───────────────────────────────────────────

    public void insert(Etudiant etudiant) throws SQLException {
        String sql = "INSERT INTO ETUDIANT (numero, nom, prenom) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, etudiant.getNumero());
            stmt.setString(2, etudiant.getNom());
            stmt.setString(3, etudiant.getPrenom());
            stmt.executeUpdate();
        }
    }

    public void update(Etudiant etudiant) throws SQLException {
        if (etudiant.getParcours() != null) {
            // Récupérer l'id_parcours depuis le nom du parcours
            String sql =
                    "UPDATE ETUDIANT SET nom = ?, prenom = ?, " +
                            "id_parcours = (SELECT id_parcours FROM PARCOURS WHERE nom = ?) " +
                            "WHERE numero = ?";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, etudiant.getNom());
                stmt.setString(2, etudiant.getPrenom());
                stmt.setString(3, etudiant.getParcours().getNom());
                stmt.setString(4, etudiant.getNumero());
                stmt.executeUpdate();
            }
        } else {
            // Pas de parcours → on met NULL
            String sql =
                    "UPDATE ETUDIANT SET nom = ?, prenom = ?, id_parcours = NULL " +
                            "WHERE numero = ?";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, etudiant.getNom());
                stmt.setString(2, etudiant.getPrenom());
                stmt.setString(3, etudiant.getNumero());
                stmt.executeUpdate();
            }
        }

    }

    public void delete(String numero) throws SQLException {
        String sql = "DELETE FROM ETUDIANT WHERE numero = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, numero);
            stmt.executeUpdate();
        }
    }

    // ── utilitaires privés ────────────────────────────────────────
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

    private Parcours findParcoursByNom(List<Parcours> parcours, String nom) {
        for (Parcours p : parcours) {
            if (p.getNom().equals(nom)) return p;
        }
        return null;
    }
}