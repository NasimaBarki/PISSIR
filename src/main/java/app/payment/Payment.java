package app.payment;

import java.time.LocalDate;
import java.time.LocalTime;

public class Payment {
    int id;
    String username;
    LocalDate date;
    LocalTime time;
    String type;
    float price;
    String userType;

    public Payment(int id, String username, LocalDate date, LocalTime time, String type, float price, String userType) {
        this.id = id;
        this.username = username;
        this.date = date;
        this.time = time;
        this.type = type;
        this.price = price;
        this.userType = userType;
    }

    public Payment(String username, LocalDate date, LocalTime time, String type, float price, String userType) {
        this.username = username;
        this.date = date;
        this.time = time;
        this.type = type;
        this.price = price;
        this.userType = userType;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public LocalDate getDate() {
        return date;
    }

    public LocalTime getTime() {
        return time;
    }

    public String getType() {
        return type;
    }

    public float getPrice() {
        return price;
    }

    public String getUserType() {
        return userType;
    }
}
