package signIn;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

//import com.mysql.cj.xdevapi.PreparableStatement;

public class AuthenticationManager {
    private Connection conn;

    public AuthenticationManager() {
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/gestionScolaire", "root", "");
            System.out.println("connexion etablie");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean authenticate(String userName, String password) {
        try {
            String sql = "SELECT * FROM utilisateur WHERE email = ? AND mot_de_passe = ?";
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1, userName);
            statement.setString(2, password);
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next(); // vrai si une ligne correspond, faux sinon
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean rediriger() throws SQLException{
        if(authenticate(null, null)){
            String sql = "SELECT * FROM utilisateur WHERE role_id >=1";

            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1, sql);
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next(); 
        }
        return false;
    }

    public int getUserRole(String userName) throws SQLException {
        int role = 0; // Initialiser à un rôle par défaut (par exemple, 0 pour un rôle invalide)
        String sql = "SELECT role_id FROM utilisateur WHERE email = ?";
        PreparedStatement statement = conn.prepareStatement(sql);
        statement.setString(1, userName);
        ResultSet resultSet = statement.executeQuery();
        if (resultSet.next()) {
            role = resultSet.getInt("role_id");
        }
        return role;
    }
    
}
