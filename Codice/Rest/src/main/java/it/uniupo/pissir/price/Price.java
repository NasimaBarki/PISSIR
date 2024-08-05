package it.uniupo.pissir.price;

public class Price {
    //Nome del servizio (univoco)
    private final String service;
    //Prezzo del servizio
    private final float price;

    //Costruttore
    public Price(String service, float price) {
        this.service = service;
        this.price = price;
    }

    //Getter
    public String getService() {
        return service;
    }

    public float getPrice() {
        return price;
    }
}
