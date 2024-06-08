package app.parkingSpot;

import java.util.ArrayList;

public class ParkingSpot {
    private final int id;
    private final int available;
    private final int bookable;

    public ParkingSpot(int id, int available, int bookable) {
        this.id = id;
        this.available = available;
        this.bookable = bookable;
    }

    public int getId() {
        return id;
    }

    public int getAvailable() {
        return available;
    }

    public int getBookable() {
        return bookable;
    }
}
