package app.user;

import app.utils.DBConnect;
import at.favre.lib.crypto.bcrypt.BCrypt;

import java.sql.*;

public class UserDao {
    private static final int MAX_USERNAME_LENGTH = 25;
    private static final int MIN_FIELD_LENGTH = 3;

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

            //Hashing della password
            String passwordBCrypt = BCrypt.withDefaults().hashToString(10, password.toCharArray());

            //TODO fare in modo che nome e cognome abbiano solo la prima lettera maiuscola

            st.setString(1, name);
            st.setString(2, surname);
            st.setString(3, email);
            st.setString(4, username);
            st.setString(5, passwordBCrypt);

            st.executeUpdate();

            conn.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return error;
    }

    private int checkUserParams(String name, String surname, String email, String username, String password) {
        int error = 0;

        //TODO per qualche motivo è accettabile anche idk@g?

        //Controlla se il nome contiene solo lettere
        if(isNotLettersOnly(name)){
            error = 1;
        }
        //Controlla se il cognome contiene solo lettere
        else if(isNotLettersOnly(surname)) {
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
        //Controlla se il nome è almeno lungo tre caratteri
        else if(name.length() < MIN_FIELD_LENGTH){
            error = 9;
        }
        //Controlla se il cognome è almeno lungo tre caratteri
        else if(surname.length() < MIN_FIELD_LENGTH){
            error = 10;
        }
        //Controlla se l'email è vuota
        else if(email.isEmpty()){
            error = 11;
        }
        //Controlla se lo username è almeno lungo tre caratteri
        else if(username.length() < MIN_FIELD_LENGTH){
            error = 12;
        }

        return error;
    }

    private boolean containsSpecialChar(String word) {
        for(int i = 0; i<word.length(); i++){
            if(word.charAt(i) >= 33 && word.charAt(i) <= 47){
                return true;
            }
        }
        return false;
    }

    private boolean containsNumber(String word) {
        for(int i = 0; i<word.length(); i++){
            if(word.charAt(i) >= 48 && word.charAt(i) <= 57){
                return true;
            }
        }
        return false;
    }

    private boolean containsLower(String word) {
        for(int i = 0; i<word.length(); i++){
            if(word.charAt(i) >= 97 && word.charAt(i) <= 122){
                return true;
            }
        }
        return false;
    }

    private boolean containsUpper(String word) {
        for(int i = 0; i<word.length(); i++){
            if(word.charAt(i) >= 65 && word.charAt(i) <= 90){
                return true;
            }
        }
        return false;
    }

    private boolean isNotLettersOnly(String word) {
        for(int i = 0; i<word.length(); i++){
            if (!Character.isLetter(word.charAt(i))){
                return true;
            }
        }
        return false;
    }
}
