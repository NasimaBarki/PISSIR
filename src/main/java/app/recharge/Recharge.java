package app.recharge;

public class Recharge {
    private final String username;
    private final int percentage;
    private final int notification;

    public Recharge(String username, int percentage, int notification) {
        this.username = username;
        this.percentage = percentage;
        this.notification = notification;
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
}
