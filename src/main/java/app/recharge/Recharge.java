package app.recharge;

public class Recharge {
    private final String username;
    private final int percentage;
    private final int notification;
    private final String completed;

    public Recharge(String username, int percentage, int notification, String completed) {
        this.username = username;
        this.percentage = percentage;
        this.notification = notification;
        this.completed = completed;
    }

    public Recharge(String username, int percentage, int notification) {
        this.username = username;
        this.percentage = percentage;
        this.notification = notification;
        this.completed = "no";
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

    public String getCompleted() {
        return completed;
    }
}
