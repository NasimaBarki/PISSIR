package it.uniupo.pissir.parkingSpot;

public class ParkingSpot {
    //ID univoco del posto auto
    private int id;
    //0 se il posto è libero, 1 altrimenti
    private final int available;
    //Indica se il posto auto è riservato agli utenti premium
    private final int premium;

    //Costruttore con id
    public ParkingSpot(int id, int available, int premium) {
        this.id = id;
        this.available = available;
        this.premium = premium;
    }

    //Costruttore senza id
    public ParkingSpot(int available, int premium) {
        this.available = available;
        this.premium = premium;
    }

    //Getter
    public int getId() {
        return id;
    }

    public int getAvailable() {
        return available;
    }

    public int getPremium() {
        return premium;
    }
}
