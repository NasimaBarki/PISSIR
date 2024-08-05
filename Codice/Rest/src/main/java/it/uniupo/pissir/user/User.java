package it.uniupo.pissir.user;

    public class User {
    //Nome dell'utente
    private final String name;
    //Cognome dell'utente
    private final String surname;
    //E-mail dell'utente
    private final String email;
    //Username univoco dell'utente
    private final String username;
    //Password dell'utente
    private final String password;
    //Tipo dell'utente (0 utente base, 1 utente premium, 2 utente amministratore)
    private final int type;

    //Costruttore
    public User(String name, String surname, String email, String username, String password, int type) {
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.username = username;
        this.password = password;
        this.type = type;
    }

    //Getter
    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public int getType() {
        return type;
    }
}
