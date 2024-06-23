package app.reservation;

public class Reservation {
    int id;
    String username;
    String arrival;
    int duration;
    boolean active;
    int parkingSpotNumber;
    boolean late;

    public Reservation(int id, String username, String arrival, int duration, boolean active, int parkingSpotNumber, boolean late) {
        this.id = id;
        this.username = username;
        this.arrival = arrival;
        this.duration = duration;
        this.active = active;
        this.parkingSpotNumber = parkingSpotNumber;
        this.late = late;
    }

    public Reservation(String username, String arrival, int duration, boolean active, int parkingSpotNumber, boolean late) {
        this.username = username;
        this.arrival = arrival;
        this.duration = duration;
        this.active = active;
        this.parkingSpotNumber = parkingSpotNumber;
        this.late = late;
    }

    public String getUsername() {
        return username;
    }

    public String getArrival() {
        return arrival;
    }

    public int getDuration() {
        return duration;
    }

    public boolean isActive() {
        return active;
    }

    public int getParkingSpotNumber() {
        return parkingSpotNumber;
    }

    public boolean isLate() {
        return late;
    }

    public int getId() {
        return id;
    }
}
