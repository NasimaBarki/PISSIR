package it.uniupo.pissir.recharge;

public class Recharge {
    private int id;
    //Username del richiedente
    private final String username;
    //Percentuale di ricarica desiderata
    private final int percentage;
    //Assume il valore 0 se l'utente non desidera una notifica, 1 altrimenti
    private final int notification;
    //Assume il valore 0 se la ricarica non è stata effettuata, 1 altrimenti
    private int completed;

    //Costruttore
    public Recharge(int id, String username, int percentage, int notification, int completed) {
        this.id = id;
        this.username = username;
        this.percentage = percentage;
        this.notification = notification;
        this.completed = completed;
    }

    //Costruttore senza id
    public Recharge(String username, int percentage, int notification, int completed) {
        this.username = username;
        this.percentage = percentage;
        this.notification = notification;
        this.completed = completed;
    }

    //Getter
    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public int getPercentage() {
        return percentage;
    }

    public int getNotification() {
        return notification;
    }

    public int getCompleted() {
        return completed;
    }

    //Setter

    public void setId(int id) {
        this.id = id;
    }

    public void setCompleted(int completed) {
        this.completed = completed;
    }
}
