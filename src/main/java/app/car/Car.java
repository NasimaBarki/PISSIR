package app.car;

public class Car {
    String username;
    String licensePlate;
    String model;
    int capacity;

    public Car(String username, String licensePlate, String model, int capacity) {
        this.username = username;
        this.licensePlate = licensePlate;
        this.model = model;
        this.capacity = capacity;
    }

    public String getUsername() {
        return username;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public String getModel() {
        return model;
    }

    public int getCapacity() {
        return capacity;
    }
}
