package it.uniupo.pissir.reservation;

import com.google.gson.Gson;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.template.handlebars.HandlebarsTemplateEngine;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static spark.Spark.halt;

public class ReservationController {

    public static Route serveReservationPage = (Request req, Response res) -> {
        //Controllo se l'utente è autenticato e che sia anche un admin
        String username = req.session().attribute("authenticated");
        String premium = req.session().attribute("premium");

        RestTemplate rest = new RestTemplate();
        Map model;

        try {
            //Ottieni le prenotazioni dell'utente
            model = rest.getForObject("http://localhost:4567/api/v1.0/reservations?username=" + username, Map.class);
        } catch (HttpClientErrorException e) {
            model = new HashMap<>();
            model.put("noReservation", "noReservation");
        }

        //Controlla se l'utente ha già registrato la propria auto
        try {
            ResponseEntity<String> apiResponse = rest.getForEntity("http://localhost:4567/api/v1.0/users/" + username + "/car", String.class);
        } catch (HttpClientErrorException e) {
            model.put("noCar", "noCar");
            System.out.println("Non è ancora stata registrata l'auto.");
        }

        //Ottieni il numero di posti auto prenotabili al momento
        try {
            Map json = rest.getForObject("http://localhost:4567/api/v1.0/parkingSpots?premium=yes", Map.class);
            model.put("emptyPremiumParkingSpots", json.get("emptyPremiumParkingSpots"));

        } catch (HttpClientErrorException e) {
            model.put("emptyPremiumParkingSpots", "0");
        }

        if(username != null && premium != null){
            model.put("authenticated", username);
            model.put("premium", premium);
        } else halt(401, "Devi accedere ed essere un utente premium per visualizzare questa pagina.");

        return new HandlebarsTemplateEngine().render(
                new ModelAndView(model, "layouts/reservation.hbs")
        );
    };

    public static Route serveReservationRequestPage = (Request req, Response res) -> {
        RestTemplate restTemplate = new RestTemplate();

        //Controllo se l'utente è autenticato e che sia anche un admin
        String username = req.session().attribute("authenticated");
        String premium = req.session().attribute("premium");

        //Controlla se l'utente ha già una prenotazione
        try {
            ResponseEntity<String> apiResponse = restTemplate.getForEntity("http://localhost:4567/api/v1.0/reservations?username=" + username , String.class);

            if (apiResponse.getStatusCode().is2xxSuccessful()) {
                res.redirect("/reservation");
            }
        } catch (HttpClientErrorException e) {
            e.printStackTrace();
        }

        Map<Object, Object> model = new HashMap<>();

        if(username != null && premium != null){
            model.put("authenticated", username);
            model.put("premium", premium);
        } else halt(401, "Devi accedere ed essere un utente premium per visualizzare questa pagina.");

        //Se esiste un messaggio di errore mostralo e ricompila i campi
        if(req.session().attribute("errorMessage") != null){
            model.put("card", req.session().attribute("card"));
            model.put("arrival", req.session().attribute("arrival"));
            model.put("duration", req.session().attribute("duration"));

            model.put("errorMessage", req.session().attribute("errorMessage"));
        }

        return new HandlebarsTemplateEngine().render(
                new ModelAndView(model, "layouts/reservationRequest.hbs")
        );
    };

    public static Route handleReservationPost = (Request req, Response res) -> {
        RestTemplate rest = new RestTemplate();
        Gson gson = new Gson();

        String username = req.session().attribute("authenticated");
        String card = req.queryParams("card");
        String arrival = req.queryParams("arrival");
        String duration = req.queryParams("duration");
        String price = "";

        String errorMessage = "";
        String parkingSpotNumber = "";

        //Reformat della data di arrivo per renderlo più leggibile per l'utente
        LocalDateTime dt = LocalDateTime.parse(arrival, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"));
        arrival = dt.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"));

        //Tramite la durata calcolo la fine della permanenza
        String reservationEnd = dt.plusMinutes(Long.parseLong(duration)).format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"));

        //Rimozione spazi dalla carta
        card = card.replaceAll("\\s+","");

        //Validazione carta
        if (!card.matches("^(5[1-5][0-9]{14}|2(22[1-9][0-9]{12}|2[3-9][0-9]{13}|[3-6][0-9]{14}|7[0-1][0-9]{13}|720[0-9]{12}))$")) {
            errorMessage = "La carta non è valida";
        }

        //Validazione data
        if (LocalDate.now().equals(dt.toLocalDate()) && LocalTime.now().isAfter(dt.toLocalTime())) {
            errorMessage = "La data e l'ora selezionati non sono validi.";
        }

        //Numero posto
        try {
            Map json = rest.getForObject("http://localhost:4567/api/v1.0/parkingSpots?arrival=" + arrival + "&reservationEnd=" + reservationEnd, Map.class);
            List<Map<String, Object>> parkingSpots = (List<Map<String, Object>>) json.get("parkingSpot");
            if (!parkingSpots.isEmpty()) {
                System.out.println(parkingSpots);
                Map<String, Object> firstParkingSpot = parkingSpots.get(0);
                parkingSpotNumber = String.valueOf(firstParkingSpot.get("id"));
            } else {
                errorMessage = "Non sono presenti posti disponibili per quel giorno e ora.";
            }
        } catch (HttpClientErrorException | NullPointerException e) {
            e.printStackTrace();
        }

        if (errorMessage.isEmpty()) {
            Map<String, String> body = new HashMap<>();
            body.put("username", username);
            body.put("arrival", arrival);
            body.put("reservationEnd", reservationEnd);
            body.put("parkingSpotNumber", parkingSpotNumber);
            body.put("late", "0");

            String jsonStringReservation = gson.toJson(body);

            body.clear();

            try {
                Map json = rest.getForObject("http://localhost:4567/api/v1.0/prices?service=parking", Map.class);
                Map<String, Object> priceMap = (Map<String, Object>) json.get("price");
                price = priceMap.get("price").toString();
            } catch (HttpClientErrorException e) {
                e.printStackTrace();
            }

            //Calcolo del prezzo all'ora
            price = String.valueOf(Float.parseFloat(price) * (Float.parseFloat(duration) / 60));
            price = String.format(Locale.US, "%.2f", Float.parseFloat(price));
            System.out.println(price);

            body.put("username", username);
            body.put("date", LocalDate.now().toString());
            body.put("time", LocalTime.now().toString());
            body.put("type", "Sosta");
            body.put("userType", "Premium");
            body.put("price", price);

            String jsonStringPayment = gson.toJson(body);

            try {
                ResponseEntity<String> apiResponseReservation = rest.postForEntity("http://localhost:4567/api/v1.0/reservations", jsonStringReservation, String.class);
                ResponseEntity<String> apiResponsePayment = rest.postForEntity("http://localhost:4567/api/v1.0/payments", jsonStringPayment, String.class);

                if(apiResponseReservation.getStatusCode().is2xxSuccessful() && apiResponsePayment.getStatusCode().is2xxSuccessful()){
                    res.redirect("/reservation");
                }
            } catch (HttpClientErrorException e) {
                errorMessage = "Si è verificato un problema.";
                req.session().attribute("errorMessage", errorMessage);

                res.redirect("/reservation");
            }
        } else {
            //Creazione del messaggio di errore
            req.session().attribute("errorMessage", errorMessage);

            //Ricompilazione dei campi
            req.session().attribute("card", card);
            req.session().attribute("arrival", arrival);
            req.session().attribute("duration", Integer.valueOf(duration));

            res.redirect("/reservationRequest");
        }

        return null;
    };
}
