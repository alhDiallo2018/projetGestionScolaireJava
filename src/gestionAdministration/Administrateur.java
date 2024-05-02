package gestionAdministration;

import gestionActivite.*;
import gestionCours.*;
import gestionMatiere.*;
import gestionSalle.*;
import gestionNiveau.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class Administrateur extends JFrame implements ActionListener {
    private JPanel dashboardPanel;
    private JLabel labelEleves, labelProfesseurs, labelChauffeurs;
    private boolean estConnecte = false;
    private String nomUtilisateur;
    private Connection connection;
    private JTextField nomUtilisateurField;
    private String prenomUtilisateur;
    private String photoUtilisateur;
    private String nomUtilisateurI;
    private String sexeUtilisateur;
    private JPanel topPanel; // Ajout d'un champ pour stocker le panneau supérieur

    public Administrateur() {
        setTitle("Espace Admin");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Connexion à la base de données
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/gestionScolaire", "root", "");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erreur de connexion à la base de données: " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        JPanel topPanel = createTopPanel(); // Création du panneau supérieur
        JMenuBar menuBar = createMenuBar(); // Création de la barre de menu

        dashboardPanel = new JPanel();
        dashboardPanel.setLayout(new GridLayout(4, 3));

        addButton("Gérer les Élèves (Ajout)", "ajouterEleve");
        addButton("Gérer les Élèves (Afficher)", "afficherEleves");
        addButton("Gérer les Cours", "cours");
        addButton("Gérer les Cours (Afficher)", "afficherCours");
        addButton("Gérer les Salles", "salles");
        addButton("Gérer les Salles (Afficher)", "afficherSalles");
        addButton("Gérer les Professeurs", "professeurs");
        addButton("Gérer les Professeurs (Afficher)", "afficherProfesseurs");
        addButton("Gérer les Activités", "activites");
        addButton("Gérer les Activités (Afficher)", "afficherActivites");
        addButton("Gérer les Parents", "parents");
        addButton("Gérer les Parents (Afficher)", "afficherParents");
        addButton("Gérer les Matières", "matieres");
        addButton("Gérer les Matières (Afficher)", "afficherMatieres");
        addButton("Gérer les Niveaux", "niveaux");
        addButton("Gérer les Niveaux (Afficher)", "afficherNiveaux");
        addButton("Gérer les Examens", "examens");
        addButton("Gérer les Examens (Afficher)", "afficherExamens");
        addButton("Gérer les Chauffeurs", "chauffeurs");
        addButton("Gérer les Chauffeurs (Afficher)", "afficherChauffeurs");
        addButton("Gérer les Accompagnateurs", "accompagnateurs");
        addButton("Gérer les Accompagnateurs (Afficher)", "afficherAccompagnateurs");

        add(topPanel, BorderLayout.NORTH);
        add(menuBar, BorderLayout.WEST);
        add(dashboardPanel, BorderLayout.CENTER);

        labelEleves = new JLabel("Nombre d'élèves: " + getNombreEnregistrements(2));
        labelProfesseurs = new JLabel("Nombre de professeurs: " + getNombreEnregistrements(3));
        labelChauffeurs = new JLabel("Nombre de chauffeurs: " + getNombreEnregistrements(4));

        dashboardPanel.add(labelEleves);
        dashboardPanel.add(labelProfesseurs);
        dashboardPanel.add(labelChauffeurs);

        pack();
        setLocationRelativeTo(null);

        verifierConnexion();
    }

    private void verifierConnexion() {
        if (!estConnecte) {
            // Si l'utilisateur n'est pas connecté, affichez la fenêtre de connexion
            afficherFenetreConnexion();
        } else {
            // Vérifier le rôle de l'utilisateur connecté
            int role_id = getRoleId();

            if (role_id != 5) {
                // Si le rôle de l'utilisateur n'est pas administrateur, affichez un message d'erreur et fermez l'application
                JOptionPane.showMessageDialog(this, "Vous n'avez pas les droits d'accès requis.", "Erreur d'accès", JOptionPane.ERROR_MESSAGE);
                System.exit(0);
            } else {
                // L'utilisateur est connecté avec succès, appelez createTopPanel() ici
                createTopPanel(); // Appel de createTopPanel() lorsque l'utilisateur est connecté
                setVisible(true); // Afficher la fenêtre principale après la connexion réussie
            }
        }
    }

    private void afficherFenetreConnexion() {
        JFrame fenetreConnexion = new JFrame("Connexion");
        fenetreConnexion.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        fenetreConnexion.setSize(300, 150);
        fenetreConnexion.setLayout(new GridLayout(3, 2));

        // Utilisez la variable de classe nomUtilisateurField
        nomUtilisateurField = new JTextField();
        JPasswordField motDePasseField = new JPasswordField();

        JButton boutonConnexion = new JButton("Connexion");
        boutonConnexion.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String nomUtilisateur = nomUtilisateurField.getText();
                String motDePasse = new String(motDePasseField.getPassword());

                if (verifierConnexion(nomUtilisateur, motDePasse)) {
                    estConnecte = true;
                    // Récupérer le prénom, le nom et la photo de l'utilisateur à partir de la base de données
                    // et les stocker dans les champs de classe appropriés
                    recupererInformationsUtilisateur(nomUtilisateur);
                    fenetreConnexion.setVisible(false);
                    setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(fenetreConnexion, "Nom d'utilisateur ou mot de passe incorrect.", "Erreur de connexion", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        fenetreConnexion.add(new JLabel("Nom d'utilisateur:"));
        fenetreConnexion.add(nomUtilisateurField);
        fenetreConnexion.add(new JLabel("Mot de passe:"));
        fenetreConnexion.add(motDePasseField);
        fenetreConnexion.add(new JLabel()); // Espace vide pour aligner les boutons
        fenetreConnexion.add(boutonConnexion);

        fenetreConnexion.setVisible(true); // Affichez la fenêtre de connexion
    }

    private boolean verifierConnexion(String nomUtilisateur, String motDePasse) {
        boolean authentifie = false;

        try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM utilisateur WHERE email = ? AND mot_de_passe = ?")) {
            statement.setString(1, nomUtilisateur);
            statement.setString(2, motDePasse);
            try (ResultSet resultSet = statement.executeQuery()) {
                authentifie = resultSet.next();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erreur de base de données: " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }

        return authentifie;
    }

    private int getRoleId() {
        int role_id = 0;
        try (PreparedStatement statement = connection.prepareStatement("SELECT role_id FROM utilisateur WHERE email = ? AND mot_de_passe = ?")) {
            statement.setString(1, nomUtilisateur);
            statement.setString(2, "");
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    role_id = resultSet.getInt("role_id");
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erreur de base de données: " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
        return role_id;
    }

    private int getNombreEnregistrements(int role_id) {
        int count = 0;
        try (PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) FROM utilisateur WHERE role_id = ?")) {
            statement.setInt(1, role_id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    count = resultSet.getInt(1);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erreur de base de données: " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
        return count;
    }

    private void recupererInformationsUtilisateur(String nomUtilisateur) {
        try (PreparedStatement statement = connection.prepareStatement("SELECT prenom, nom, photo, sexe FROM utilisateur WHERE email = ?")) {
            statement.setString(1, nomUtilisateur);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    prenomUtilisateur = resultSet.getString("prenom");
                    nomUtilisateurI = resultSet.getString("nom");
                    photoUtilisateur = resultSet.getString("photo");
                    sexeUtilisateur = resultSet.getString("sexe");
                    // Afficher les valeurs récupérées à la console pour déboguer
                    System.out.println("Prénom: " + prenomUtilisateur);
                    System.out.println("Nom: " + nomUtilisateurI);
                    System.out.println("Photo: " + photoUtilisateur);
                    // Mettre à jour le panneau supérieur après avoir récupéré les informations de l'utilisateur
                    updateTopPanel();
                } else {
                    System.out.println("Aucun résultat trouvé pour l'utilisateur: " + nomUtilisateur);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private JPanel createTopPanel() {
        topPanel = new JPanel(); // Utilisation du champ de classe pour le panneau supérieur
        topPanel.setLayout(new BorderLayout());

        // Afficher l'image de profil
        if (photoUtilisateur != null && !photoUtilisateur.isEmpty()) {
            ImageIcon profileImage = new ImageIcon(photoUtilisateur);
            JLabel profileLabel = new JLabel(profileImage);
            topPanel.add(profileLabel, BorderLayout.WEST);
        } else {
            // Si le chemin de la photo est null ou vide, afficher une image par défaut
            ImageIcon defaultImage = new ImageIcon("/Users/testad/Desktop/Pharmashop/images/logo.png");
            JLabel defaultLabel = new JLabel(defaultImage);
            topPanel.add(defaultLabel, BorderLayout.WEST);
        }

        if (prenomUtilisateur != null && nomUtilisateurI != null && sexeUtilisateur != null) {
            String genderText = "";
            if(sexeUtilisateur.equalsIgnoreCase("masculin")) {
                genderText = "Monsieur ";
            } else if(sexeUtilisateur.equalsIgnoreCase("feminin")) {
                genderText = "Madame ";
            }
            JLabel nameLabel = new JLabel("Bienvenue dans votre espace admin, " + genderText + prenomUtilisateur + " " + nomUtilisateurI);
            topPanel.add(nameLabel, BorderLayout.CENTER);
        }

        // Ajouter le bouton de déconnexion
        JButton logoutButton = new JButton("Déconnexion");
        logoutButton.addActionListener(e -> {
            int choice = JOptionPane.showConfirmDialog(this, "Voulez-vous vraiment vous déconnecter ?", "Déconnexion", JOptionPane.YES_NO_OPTION);
            if (choice == JOptionPane.YES_OPTION) {
                // Effectuer les actions de déconnexion ici
                // Par exemple, fermer la fenêtre actuelle et afficher la fenêtre de connexion
                setVisible(false); // Masquer la fenêtre actuelle
                afficherFenetreConnexion(); // Afficher la fenêtre de connexion
            }
        });
        topPanel.add(logoutButton, BorderLayout.EAST);

        return topPanel;
    }


    private void updateTopPanel() {
        // Supprimer tous les composants du panneau supérieur pour une mise à jour propre
        topPanel.removeAll();

        // Mettre à jour l'image de profil
        if (photoUtilisateur != null && !photoUtilisateur.isEmpty()) {
            ImageIcon profileImage = new ImageIcon(photoUtilisateur);
            JLabel profileLabel = new JLabel(profileImage);
            topPanel.add(profileLabel, BorderLayout.WEST);
        } else {
            // Si le chemin de la photo est null ou vide, afficher une image par défaut
            ImageIcon defaultImage = new ImageIcon("/Users/testad/Desktop/Pharmashop/images/background.jpeg");
            JLabel defaultLabel = new JLabel(defaultImage);
            topPanel.add(defaultLabel, BorderLayout.WEST);
        }

        if (prenomUtilisateur != null && nomUtilisateurI != null && sexeUtilisateur != null) {
            String genderText = "";
            if(sexeUtilisateur.equalsIgnoreCase("masculin")) {
                genderText = "Monsieur ";
            } else if(sexeUtilisateur.equalsIgnoreCase("feminin")) {
                genderText = "Madame ";
            }
            JLabel nameLabel = new JLabel("Bienvenue dans votre espace admin, " + genderText + prenomUtilisateur + " " + nomUtilisateurI);
            topPanel.add(nameLabel, BorderLayout.CENTER);
        }

        // Ajouter le bouton de déconnexion
        JButton logoutButton = new JButton("Déconnexion");
        logoutButton.addActionListener(e -> {
            int choice = JOptionPane.showConfirmDialog(this, "Voulez-vous vraiment vous déconnecter ?", "Déconnexion", JOptionPane.YES_NO_OPTION);
            if (choice == JOptionPane.YES_OPTION) {
                // Effectuer les actions de déconnexion ici
                // Par exemple, fermer la fenêtre actuelle et afficher la fenêtre de connexion
                setVisible(false); // Masquer la fenêtre actuelle
                afficherFenetreConnexion(); // Afficher la fenêtre de connexion
            }
        });
        topPanel.add(logoutButton, BorderLayout.EAST);

        // Rafraîchir l'interface utilisateur pour refléter les changements
        topPanel.revalidate();
        topPanel.repaint();
    }


    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Fichier");
        JMenuItem exitMenuItem = new JMenuItem("Quitter");

        exitMenuItem.addActionListener(e -> System.exit(0));

        menu.add(exitMenuItem);
        menuBar.add(menu);

        return menuBar;
    }

    private void addButton(String text, String actionCommand) {
        JButton button = new JButton(text);
        button.setActionCommand(actionCommand);
        button.addActionListener(this);
        dashboardPanel.add(button);
    }

    // Gérer les actions des boutons du tableau de bord
    @Override
    public void actionPerformed(ActionEvent e) {
        String actionCommand = e.getActionCommand();
        switch (actionCommand) {
            case "ajouterEleve":
               new AjoutEleve();
                break;
            case "afficherEleves":
                new AffichageEleve();
                break;
            case "cours":
                new AjoutCours();
                break;
            case "afficherCours":
                new ListerCours();
                break;
            case "salles":
                new ajoutSalle();
                break;
            case "afficherSalles":
                new listerSalle();
                break;
            case "professeurs":
                new AjoutProf();
                break;
            case "afficherProfesseurs":
                new AffichageProfessur();
                break;
            case "activites":
                new ajoutActivite();
                break;
            case "afficherActivites":
                new listerActivite();
                break;
            case "matieres":
                new ajoutMatiere();
                break;
            case "afficherMatieres":
                new listerMatiere();
                break;
            case "niveaux":
                new ajouterNiveau();
                break;
            case "afficherNiveaux":
                new listerNiveau();
                break;
            case "examens":
                JOptionPane.showMessageDialog(this, "Gérer les examens dans le administrateur");
                break;
            case "afficherExamens":
                JOptionPane.showMessageDialog(this, "afficher les examens dans le programme !");
                break;
            case "chauffeurs":
                JOptionPane.showMessageDialog(this, "Gérer les chauffeurs");
                break;
            case "afficherChauffeurs":
                JOptionPane.showMessageDialog(this, "Afficher les chauffeurs");
                break;
            case "parents":
                JOptionPane.showMessageDialog(this, "Gérer les parents");
                break;
            case "afficherParents":
                JOptionPane.showMessageDialog(this, "Afficher les parent");
                break;
            case "accompagnateurs":
                JOptionPane.showMessageDialog(this, "Gérer les accompagnateurs");
                break;
            case "afficherAccompagnateurs":
                JOptionPane.showMessageDialog(this, "Afficher les accompagnateurs");
                break;
            default:
                break;
        }
    }

    // Méthode principale pour tester la classe Administrateur
    public static void main(String[] args) {
        SwingUtilities.invokeLater(Administrateur::new);
    }
}
