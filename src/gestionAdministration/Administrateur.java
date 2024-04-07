package gestionAdministration;

import javax.swing.*;
import java.awt.*;
import javax.swing.border.Border;

public class Administrateur extends JFrame {
    public Administrateur() {
        setTitle("Admin Page");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Création de la barre de menu
        JMenuBar menuBar = new JMenuBar();
        menuBar.setLayout(new BoxLayout(menuBar, BoxLayout.Y_AXIS));
        
        // Définir la couleur de fond noire pour chaque menu
        for (int i = 0; i < menuBar.getComponentCount(); i++) {
            Component comp = menuBar.getComponent(i);
            if (comp instanceof JMenu) {
                JMenu menu = (JMenu) comp;
                menu.setBackground(Color.BLACK);
                // Définir la couleur de fond noire pour chaque élément de menu
                for (int j = 0; j < menu.getMenuComponentCount(); j++) {
                    Component menuItemComp = menu.getMenuComponent(j);
                    if (menuItemComp instanceof JMenuItem) {
                        JMenuItem menuItem = (JMenuItem) menuItemComp;
                        menuItem.setBackground(Color.BLACK);
                    }
                }
            }
        }

        // Définir les menus principaux et leurs options associées
        String[] menuTitles = {
                "Etudiants", "Niveaux", "Activités", "Professeurs", "Classes",
                "Disciplines", "Parents", "Matières", "Chauffeurs", "Salles",
                "Examens", "Accompagnateurs", "File"
        };

        String[][] menuOptions = {
                {"Ajouter étudiant", "Voir les étudiants"},
                {"Ajouter niveau", "Voir les niveaux"},
                {"Ajouter activité", "Voir les activités"},
                {"Ajouter professeur", "Voir les professeurs"},
                {"Ajouter classe", "Voir les classes"},
                {"Ajouter discipline", "Voir les disciplines"},
                {"Ajouter parent", "Voir les parents"},
                {"Ajouter matière", "Voir les matières"},
                {"Ajouter chauffeur", "Voir les chauffeurs"},
                {"Ajouter salle", "Voir les salles"},
                {"Ajouter examen", "Voir les examens"},
                {"Ajouter accompagnateur", "Voir les accompagnateurs"},
                {"Exit"}
        };

        // Définir les bordures pour les éléments de menu
        Border defaultBorder = BorderFactory.createMatteBorder(1, 0, 1, 0, new Color(46, 49, 49));

        for (int i = 0; i < menuTitles.length; i++) {
            JMenu menu = new JMenu(menuTitles[i]);
            menu.setForeground(Color.WHITE); // Définir la couleur du texte
            menu.setBackground(Color.BLACK); // Définir la couleur de fond
            menu.setPreferredSize(new Dimension(menuTitles[i].length() * 10, 30)); // Définir la taille préférée du menu

            // Ajouter les options à chaque menu principal
            for (String option : menuOptions[i]) {
                JMenuItem menuItem = new JMenuItem(option);
                menuItem.setBackground(new Color(46, 49, 49)); // Définir la couleur de fond de l'élément de menu
                menuItem.setForeground(Color.WHITE); // Définir la couleur du texte de l'élément de menu
                menu.add(menuItem);
            }

            // Ajouter le menu à la barre de menu
            menuBar.add(menu);
        }

        // Créer un JPanel pour contenir la barre de menu
        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new BorderLayout());
        // menuPanel.setBackground(Color.YELLOW); // Couleur de fond jaune
        menuPanel.add(menuBar, BorderLayout.NORTH); // Ajouter la barre de menu au panel

        // Ajouter le panel de menu à la fenêtre
        add(menuPanel, BorderLayout.WEST);

        // Création du panneau pour le contenu principal (dashboard)
        JPanel dashboardPanel = new JPanel();
        dashboardPanel.setBackground(Color.blue); // Couleur de fond du tableau de bord

        JButton buttonEtudiant = new JButton("Etudiants\n(effectif total: 2)");
        JButton buttonNiveaux = new JButton("Niveaux\n(effectif total: 3)");
        JButton buttonActivites = new JButton("Activités\n(effectif total: 5)");
        JButton buttonProfesseurs = new JButton("Professeurs\n(effectif total: 4)");
        JButton buttonClasses = new JButton("Classes\n(effectif total: 6)");
        JButton buttonDisciplines = new JButton("Disciplines\n(effectif total: 8)");
        JButton buttonParents = new JButton("Parents\n(effectif total: 10)");
        JButton buttonMatieres = new JButton("Matières\n(effectif total: 7)");
        JButton buttonChauffeurs = new JButton("Chauffeurs\n(effectif total: 9)");
        JButton buttonSalles = new JButton("Salles\n(effectif total: 12)");
        JButton buttonExamens = new JButton("Examens\n(effectif total: 15)");
        JButton buttonAccompagnateurs = new JButton("Accompagnateurs\n(effectif total: 18)");
        JButton buttonFile = new JButton("File");

        dashboardPanel.add(buttonEtudiant);
        dashboardPanel.add(buttonNiveaux);
        dashboardPanel.add(buttonActivites);
        dashboardPanel.add(buttonProfesseurs);
        dashboardPanel.add(buttonClasses);
        dashboardPanel.add(buttonDisciplines);
        dashboardPanel.add(buttonParents);
        dashboardPanel.add(buttonMatieres);
        dashboardPanel.add(buttonChauffeurs);
        dashboardPanel.add(buttonSalles);
        dashboardPanel.add(buttonExamens);
        dashboardPanel.add(buttonAccompagnateurs);
        dashboardPanel.add(buttonFile);

        buttonEtudiant.setPreferredSize(new Dimension(180,80));
        buttonNiveaux.setPreferredSize(new Dimension(180,80));
        buttonActivites.setPreferredSize(new Dimension(180,80));
        buttonProfesseurs.setPreferredSize(new Dimension(180,80));
        buttonClasses.setPreferredSize(new Dimension(180,80));
        buttonDisciplines.setPreferredSize(new Dimension(180,80));
        buttonParents.setPreferredSize(new Dimension(180,80));
        buttonMatieres.setPreferredSize(new Dimension(180,80));
        buttonChauffeurs.setPreferredSize(new Dimension(180,80));
        buttonSalles.setPreferredSize(new Dimension(180,80));
        buttonExamens.setPreferredSize(new Dimension(180,80));
        buttonAccompagnateurs.setPreferredSize(new Dimension(180,80));
        buttonFile.setPreferredSize(new Dimension(180,80));


        // Ajouter le panneau du tableau de bord à droite
        add(dashboardPanel, BorderLayout.CENTER);

        // Adapter la taille de la fenêtre
        pack();
        setLocationRelativeTo(null); // Centrer la fenêtre

        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Administrateur::new);
    }
}
