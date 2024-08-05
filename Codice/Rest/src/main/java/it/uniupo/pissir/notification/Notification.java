package it.uniupo.pissir.notification;

public class Notification {
    //Username dell'utente
    private final String username;
    //Testo del messaggio
    private final String message;
    //Data di creazione
    private final String date;

    //Costruttore
    public Notification(String username, String message, String date) {
        this.username = username;
        this.message = message;
        this.date = date;
    }

    //Getter
    public String getUsername() {
        return username;
    }

    public String getMessage() {
        return message;
    }

    public String getDate() {
        return date;
    }
}
