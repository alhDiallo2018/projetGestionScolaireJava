package signIn;


import javax.swing.*;

import gestionAdministration.Administrateur;
import gestionEleve.Eleve;
import gestionParent.Parent;
import gestionPersonnel.Personnel;
import gestionProfesseur.Professeur;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

public class SignIn extends JFrame {
    private JTextField txtUserName, txtPassword;
    private JLabel labelUserName, labelPassword;
    private JButton btnSignIn, btnClose;

    private AuthenticationManager authManager;

    public SignIn() {
        setTitle("Sign in");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(300, 500);
        setLocationRelativeTo(null);
        setLayout(null);

        txtUserName = new JTextField();
        txtPassword = new JPasswordField();
        labelUserName = new JLabel("Username");
        labelPassword = new JLabel("Password");
        btnSignIn = new JButton("Sign in");
        btnClose = new JButton("Sign out");

        labelUserName.setBounds(20, 120, 200, 25);
        txtUserName.setBounds(20, 150, 200, 25);
        labelPassword.setBounds(20, 180, 200, 25);
        txtPassword.setBounds(20, 210, 200, 25);
        btnSignIn.setBounds(20, 250, 100, 25);
        btnClose.setBounds(140, 250, 100, 25);

        add(labelUserName);
        add(txtUserName);
        add(labelPassword);
        add(txtPassword);
        add(btnSignIn);
        add(btnClose);

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
                            dispose(); 
                        } else if (role == 2) { 
                            showMessage("Welcome, student!");
                            Eleve eleve = new Eleve();
                            dispose(); 
                        }else if (role == 3) { 
                            showMessage("Welcome, Parent/Titeur!");
                            Parent parent = new Parent();
                            dispose(); 
                        }else if (role == 4) { 
                            showMessage("Welcome, statut Personnel!");
                           Personnel pers = new Personnel();
                            dispose(); 
                        }else if (role == 5) { 
                            showMessage("Welcome, admin!");
                            Administrateur admin= new Administrateur(); 
                            dispose(); 
                        }
                        else {
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
