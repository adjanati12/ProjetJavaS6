package fr.univ.miage;
import ui.MainFrame;
import model.*;
import service.EtudiantService;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            new MainFrame().setVisible(true);
        });
    }
}
