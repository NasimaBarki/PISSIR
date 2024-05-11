package app.user;

import app.utils.DBConnect;

import java.sql.*;

public class UserDao {
    private static final int MAX_USERNAME_LENGTH = 25;

    public int addUser(String name, String surname, String email, String username, String password) {
        final String sql = "INSERT INTO users(name, surname, email, username, password) VALUES (?, ?, ?, ?, ?)";

        int error = 0;

        error = checkUserParams(name, surname, email, username, password);

        if(error > 0){
            return error;
        }

        try{
            Connection conn = DBConnect.getInstance().getConnection();
            PreparedStatement st = conn.prepareStatement(sql);
            st.setString(1, name);
            st.setString(2, surname);
            st.setString(3, email);
            st.setString(4, username);
            st.setString(5, password);

            st.executeUpdate();

            conn.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return error;
    }

    private int checkUserParams(String name, String surname, String email, String username, String password) {
        int error = 0;

        //Controlla se il nome contiene solo lettere
        if(!isLettersOnly(name)){
            error = 1;
        }
        //Controlla se il cognome contiene solo lettere
        else if(!isLettersOnly(surname)) {
            error = 2;
        }
        //Controlla se l'username è più lungo di MAX_USERNAME_LENGTH
        else if(username.length() > MAX_USERNAME_LENGTH){
            error = 3;
        }
        //Controlla se la password è di almeno otto caratteri
        else if(password.length() < 8){
            error = 4;
        }
        //Controlla se la password contiene almeno una Maiuscola
        else if(!containsUpper(password)){
            error = 5;
        }
        //Controlla se la password contiene almeno una minuscola
        else if(!containsLower(password)){
            error = 6;
        }
        //Controlla se la password contiene almeno un numero
        else if(!containsNumber(password)) {
            error = 7;
        }
        //Controlla se la password contiene almeno un carattere speciale
        else if(!containsSpecialChar(password)) {
            error = 8;
        }
        //Controlla se il nome è vuoto
        else if(name.isEmpty()){
            error = 9;
        }
        //Controlla se il cognome è vuoto
        else if(surname.isEmpty()){
            error = 10;
        }
        //Controlla se l'email è vuota
        else if(email.isEmpty()){
            error = 11;
        }
        //Controlla se lo username è vuoto
        else if(username.isEmpty()){
            error = 12;
        }

        return error;
    }

    private boolean containsSpecialChar(String word) {
        for(int i = 0; i<word.length(); i++){
            if(word.charAt(i) >= 33 || word.charAt(i) <= 47){
                return true;
            }
        }
        return false;
    }

    private boolean containsNumber(String word) {
        for(int i = 0; i<word.length(); i++){
            if(word.charAt(i) >= 48 || word.charAt(i) <= 57){
                return true;
            }
        }
        return false;
    }

    private boolean containsLower(String word) {
        for(int i = 0; i<word.length(); i++){
            if(word.charAt(i) >= 97 || word.charAt(i) <= 122){
                return true;
            }
        }
        return false;
    }

    private boolean containsUpper(String word) {
        for(int i = 0; i<word.length(); i++){
            if(word.charAt(i) >= 65 || word.charAt(i) <= 90){
                return true;
            }
        }
        return false;
    }

    private boolean isLettersOnly(String word) {
        for(int i = 0; i<word.length(); i++){
            if((word.charAt(i) < 65 || word.charAt(i) > 90) || (word.charAt(i) < 97 || word.charAt(i) > 122)){
                return true;
            }
        }
        return false;
    }
}
