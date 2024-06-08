package app.price;

public class Price {
    private final String service;
    private final int price;

    public Price(String service, int price) {
        this.service = service;
        this.price = price;
    }

    public String getService() {
        return service;
    }

    public int getPrice() {
        return price;
    }
}
