package it.uniupo.pissir.reservation;

public class Reservation {
    //ID univoco della prenotazione
    private int id;
    //Username di colui che ha effettuato la prenotazione
    private final String username;
    //Dichiarazione di arrivo al posto auto
    private final String arrival;
    //Durata della sosta
    private final String reservationEnd;
    //Indica il posto auto dove l'utente premium potrò parcheggiare
    private final int parkingSpotNumber;
    //0 non viene applicato nessun importo in più, 1 altrimenti
    private final int late;

    //Costruttore con id
    public Reservation(int id, String username, String arrival, String reservationEnd, int parkingSpotNumber, int late) {
        this.id = id;
        this.username = username;
        this.arrival = arrival;
        this.reservationEnd = reservationEnd;
        this.parkingSpotNumber = parkingSpotNumber;
        this.late = late;
    }

    //Costruttore senza id
    public Reservation(String username, String arrival, String reservationEnd, int parkingSpotNumber, int late) {
        this.username = username;
        this.arrival = arrival;
        this.reservationEnd = reservationEnd;
        this.parkingSpotNumber = parkingSpotNumber;
        this.late = late;
    }

    //Getter
    public String getUsername() {
        return username;
    }

    public String getArrival() {
        return arrival;
    }

    public String getReservationEnd() {
        return reservationEnd;
    }

    public int getParkingSpotNumber() {
        return parkingSpotNumber;
    }

    public int getLate() {
        return late;
    }

    public int getId() {
        return id;
    }
}
