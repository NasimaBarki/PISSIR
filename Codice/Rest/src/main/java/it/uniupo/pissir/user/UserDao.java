package it.uniupo.pissir.user;

import at.favre.lib.crypto.bcrypt.BCrypt;
import it.uniupo.pissir.utils.DBConnect;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDao {
    //Ottieni un singolo utente
    public static User getUser(String username) {
        User user = null;
        final String sql = "SELECT name, surname, email, username, password, type FROM users WHERE username = ?";

        try{
            Connection conn = DBConnect.getInstance().getConnection();
            PreparedStatement st = conn.prepareStatement(sql);
            st.setString(1, username);

            ResultSet rs = st.executeQuery();

            if(rs.next()){
                user = new User(rs.getString("name"), rs.getString("surname"), rs.getString("email"), rs.getString("username"), rs.getString("password"), rs.getInt("type"));
            }

            conn.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return user;
    }

    //Controlla se la password dell'utente è corretta
    public static boolean verifyPassword(String username, String password) {
        boolean success = false;

        final String sql = "SELECT username, password FROM users WHERE username = ?";

        try{
            Connection conn = DBConnect.getInstance().getConnection();
            PreparedStatement st = conn.prepareStatement(sql);
            st.setString(1, username);

            ResultSet rs = st.executeQuery();

            if (rs.next()){
                String storedBCrypt = rs.getString("password");
                BCrypt.Result BCryptResult = BCrypt.verifyer().verify(password.toCharArray(), storedBCrypt);

                if (BCryptResult.verified) success = true;
            }

            conn.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return success;
    }

    //Aggiungi un nuovo utente
    public static User addUser(User newUser) {
        final String sql = "INSERT INTO users(name, surname, email, username, password, type) VALUES (?, ?, ?, ?, ?, ?)";

        try{
            Connection conn = DBConnect.getInstance().getConnection();
            PreparedStatement st = conn.prepareStatement(sql);

            //Hashing della password
            String passwordBCrypt = BCrypt.withDefaults().hashToString(10, newUser.getPassword().toCharArray());

            //TODO fare in modo che nome e cognome abbiano solo la prima lettera maiuscola
            //TODO Controllare che email e username non ci siano nel database

            st.setString(1, newUser.getName());
            st.setString(2, newUser.getSurname());
            st.setString(3, newUser.getEmail());
            st.setString(4, newUser.getUsername());
            st.setString(5, passwordBCrypt);
            st.setInt(6, newUser.getType());

            st.executeUpdate();

            conn.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return newUser;
    }
}
