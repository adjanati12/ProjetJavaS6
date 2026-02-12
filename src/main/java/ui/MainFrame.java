package ui;
import javax.swing.*;
import service.EtudiantService;
import model.*;

public class MainFrame extends JFrame{
    private EtudiantService service = new EtudiantService();

    public MainFrame(){
        setTitle("Suivi des étudiants");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        //bouton ajouter un étudiant
        JButton btnAjouter = new JButton("Ajouter étudiant");
        add(btnAjouter);

    }

}
