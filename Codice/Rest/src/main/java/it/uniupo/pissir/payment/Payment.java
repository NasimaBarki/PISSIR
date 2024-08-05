package it.uniupo.pissir.payment;

import java.time.LocalDate;
import java.time.LocalTime;

public class Payment {
    private int id;
    private final String username;
    private final String date;
    private final String time;
    //Servizio
    private final String type;
    private final float price;
    private final String userType;

    //Costruttore con id
    public Payment(int id, String username, String date, String time, String type, float price, String userType) {
        this.id = id;
        this.username = username;
        this.date = date;
        this.time = time;
        this.type = type;
        this.price = price;
        this.userType = userType;
    }

    //Costruttore senza id
    public Payment(String username, String date, String time, String type, float price, String userType) {
        this.username = username;
        this.date = date;
        this.time = time;
        this.type = type;
        this.price = price;
        this.userType = userType;
    }

    //Getter
    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
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
