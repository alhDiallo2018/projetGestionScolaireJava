package signIn;

import  gestionAdministration.Administrateur;
import gestionEleve.Eleve;
import gestionParent.Parent;
import gestionPersonnel.Personnel;
import gestionProfesseur.Professeur;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

public class SignIn extends JFrame {
    private JTextField txtUserName, txtPassword;
    private JLabel labelUserName, labelPassword;
    private JButton btnSignIn, btnClose;
    private JLabel backgroundLabel;

    private AuthenticationManager authManager;

    public SignIn() {
        setTitle("Sign in");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 500);
        setLocationRelativeTo(null);

        // Chargement de l'image de fond
        ImageIcon backgroundImage = new ImageIcon("/Users/testad/Pictures/logoIsepD.jpeg");
        Image scaledImage = backgroundImage.getImage().getScaledInstance(400, 500, Image.SCALE_DEFAULT);
        ImageIcon scaledBackgroundImage = new ImageIcon(scaledImage);
        backgroundLabel = new JLabel(scaledBackgroundImage);
        backgroundLabel.setBounds(0, 0, 400, 500);

        // Création du conteneur principal
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(800, 500));

        // Création des composants de l'interface
        txtUserName = new JTextField();
        txtPassword = new JPasswordField();
        labelUserName = new JLabel("Username");
        labelPassword = new JLabel("Password");
        btnSignIn = new JButton("Sign in");
        btnClose = new JButton("Sign out");

        // Positionnement des composants de l'interface
        labelUserName.setBounds(450, 120, 200, 25);
        txtUserName.setBounds(450, 150, 200, 25);
        labelPassword.setBounds(450, 180, 200, 25);
        txtPassword.setBounds(450, 210, 200, 25);
        btnSignIn.setBounds(450, 250, 100, 25);
        btnClose.setBounds(570, 250, 100, 25);

        // Ajout des composants au conteneur principal
        layeredPane.add(backgroundLabel, JLayeredPane.DEFAULT_LAYER);
        layeredPane.add(labelUserName, JLayeredPane.PALETTE_LAYER);
        layeredPane.add(txtUserName, JLayeredPane.PALETTE_LAYER);
        layeredPane.add(labelPassword, JLayeredPane.PALETTE_LAYER);
        layeredPane.add(txtPassword, JLayeredPane.PALETTE_LAYER);
        layeredPane.add(btnSignIn, JLayeredPane.PALETTE_LAYER);
        layeredPane.add(btnClose, JLayeredPane.PALETTE_LAYER);

        // Ajout du conteneur principal à la fenêtre
        add(layeredPane);

        setVisible(true);

        authManager = new AuthenticationManager();

        btnSignIn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String userName = txtUserName.getText();
                String password = txtPassword.getText();
                if (authManager.authenticate(userName, password)) {
                    showMessage("Welcome");

                    try {
                        int role = authManager.getUserRole(userName);
                        if (role == 1) {
                            showMessage("Welcome, Professeur");
                            Professeur prof = new Professeur();
                            System.out.println(prof + "hello ProfesseurPanel");
                            dispose();
                        } else if (role == 2) {
                            showMessage("Welcome, student!");
                            Eleve eleve = new Eleve();
                            System.out.println(eleve + "hello EtudiantPanel");
                            dispose();
                        } else if (role == 3) {
                            showMessage("Welcome, Parent/Tuteur!");
                            Parent parent = new Parent();
                            System.out.println(parent + "hello ParentPanel");
                            dispose();
                        } else if (role == 4) {
                            showMessage("Welcome, statut Personnel!");
                            Personnel pers = new Personnel();
                            System.out.println(pers + "hello PersonnelPanel");
                            dispose();
                        } else if (role == 5) {
                            showMessage("Welcome, admin!");
                            Administrateur admin = new Administrateur();
                            System.out.println(admin + "hello AdminPanel");
                            dispose();
                        } else {
                            showMessage("You are not authorized to access this section.");
                        }
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                } else {
                    showMessage("Incorrect username or password");
                }
            }
        });
    }

    public void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(SignIn::new);
    }
}
