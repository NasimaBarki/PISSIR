package it.uniupo.pissir.car;

public class Car {
    //Targa della macchina (id univoco)
    private final String licensePlate;
    //Username del proprietario
    private final String username;
    //Modello della macchina
    private final String model;
    //Capacità della macchina
    private final int capacity;

    //Costruttore
    public Car(String licensePlate, String username, String model, int capacity) {
        this.licensePlate = licensePlate;
        this.username = username;
        this.model = model;
        this.capacity = capacity;
    }

    //Getter

    public String getLicensePlate() {
        return licensePlate;
    }

    public String getUsername() {
        return username;
    }

    public String getModel() {
        return model;
    }

    public int getCapacity() {
        return capacity;
    }
}
