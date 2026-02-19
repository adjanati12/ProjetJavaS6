package ui;

import javax.swing.*;
import java.awt.*;
import service.EtudiantService;

public class MainFrame extends JFrame {
    private final EtudiantService service = new EtudiantService();

    public MainFrame() {
        setTitle("Suivi des étudiants");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Layout simple
        setLayout(new FlowLayout(FlowLayout.LEFT));

        JButton btnAjouter = new JButton("Ajouter étudiant");
        add(btnAjouter);

        btnAjouter.addActionListener(e -> ouvrirDialogAjoutEtudiant());
    }

    private void ouvrirDialogAjoutEtudiant() {
        JTextField numeroField = new JTextField(15);
        JTextField nomField = new JTextField(15);
        JTextField prenomField = new JTextField(15);

        JPanel panel = new JPanel(new GridLayout(0, 2, 8, 8));
        panel.add(new JLabel("Numéro étudiant :"));
        panel.add(numeroField);
        panel.add(new JLabel("Nom :"));
        panel.add(nomField);
        panel.add(new JLabel("Prénom :"));
        panel.add(prenomField);

        int result = JOptionPane.showConfirmDialog(
                this,
                panel,
                "Ajouter un étudiant",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            try {
                service.ajouterEtudiant(
                        numeroField.getText(),
                        nomField.getText(),
                        prenomField.getText()
                );

                JOptionPane.showMessageDialog(this, "Étudiant ajouté avec succès !");
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(
                        this,
                        ex.getMessage(),
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }

}

