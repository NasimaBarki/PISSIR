package app.price;

public class Price {
    private final String service;
    private final float price;

    public Price(String service, float price) {
        this.service = service;
        this.price = price;
    }

    public String getService() {
        return service;
    }

    public float getPrice() {
        return price;
    }
}
