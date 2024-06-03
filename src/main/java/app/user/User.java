package app.user;

public class User {
    private final String name;
    private final String lastName;
    private final String email;
    private final String username;
    private final String password;
    private final int type;

    //Costruttore
    public User(String name, String lastName, String email, String username, String password, int type) {
        this.name = name;
        this.lastName = lastName;
        this.email = email;
        this.username = username;
        this.password = password;
        this.type = type;
    }

    //Getters
    public String getName() {
        return name;
    }

    public String getLastName() {
        return lastName;
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
