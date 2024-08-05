package it.uniupo.pissir.payment;

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
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

import static spark.Spark.halt;

public class ExitPaymentController {
    public static Route serveExitPaymentPage = (Request req, Response res) -> {
        RestTemplate rest = new RestTemplate();

        String username = req.session().attribute("authenticated");
        String premium = req.session().attribute("premium");
        String admin = req.session().attribute("admin");

        Map model = new HashMap<>();

        if(username != null){
            model.put("authenticated", username);
        } else halt(401, "Devi accedere per visualizzare questa pagina.");

        if (premium != null){
            model.put("premium", premium);
        }
        if (admin != null){
            model.put("admin", admin);
        }

        String pricePark = null;
        String priceRecharge = null;
        Float price = null;

        //Ottieni prezzo sosta
        try {
            Map json = rest.getForObject("http://localhost:4567/api/v1.0/prices?service=parking", Map.class);
            Map<String, Object> priceMap = (Map<String, Object>) json.get("price");
            pricePark = priceMap.get("price").toString();
            price = Float.parseFloat(pricePark);
        } catch (HttpClientErrorException e) {
            e.printStackTrace();
        }
        //Ottieni prezzo ricarica
        try {
            Map json = rest.getForObject("http://localhost:4567/api/v1.0/prices?service=recharging", Map.class);
            Map<String, Object> priceMap = (Map<String, Object>) json.get("price");
            priceRecharge = priceMap.get("price").toString();
        } catch (HttpClientErrorException e) {
            e.printStackTrace();
        }
        //Controlla se l'utente ha effettuato la ricarica
        try {
            String json = rest.getForObject("http://localhost:4567/api/v1.0/recharges?username=" + username + "&completed=yes", String.class);
            System.out.println(json);
            if (json != null) {
                price += Float.parseFloat(priceRecharge);
            }
        } catch (HttpClientErrorException e) {
            e.printStackTrace();
        }

        model.put("sum", price.toString());

        //Display dell'errore se esiste
        if(req.session().attribute("errorMessage") != null){
            model.put("card", req.session().attribute("card"));

            model.put("errorMessage", req.session().attribute("errorMessage"));
        }

        return new HandlebarsTemplateEngine().render(
                new ModelAndView(model, "layouts/exitPayment.hbs")
        );
    };

    public static Route handleExitPaymentPost = (Request req, Response res) -> {
        RestTemplate rest = new RestTemplate();
        Gson gson = new Gson();

        String username = req.session().attribute("authenticated");
        String card = req.queryParams("card");

        String errorMessage = "";

        //Rimozione spazi dalla carta
        card = card.replaceAll("\\s+","");

        //Validazione carta
        if (!card.matches("^(5[1-5][0-9]{14}|2(22[1-9][0-9]{12}|2[3-9][0-9]{13}|[3-6][0-9]{14}|7[0-1][0-9]{13}|720[0-9]{12}))$")) {
            errorMessage = "La carta non è valida";
        }

        if (errorMessage.isEmpty()) {
            //Creazione pagamento
            Map<String, String> body = new HashMap<>();

            String pricePark = null;
            String priceRecharge = null;
            Float price = null;
            String service = "Sosta";

            //Ottieni prezzo sosta
            try {
                Map json = rest.getForObject("http://localhost:4567/api/v1.0/prices?service=parking", Map.class);
                Map<String, Object> priceMap = (Map<String, Object>) json.get("price");
                pricePark = priceMap.get("price").toString();
                price = Float.parseFloat(pricePark);
            } catch (HttpClientErrorException e) {
                e.printStackTrace();
            }
            //Ottieni prezzo ricarica
            try {
                Map json = rest.getForObject("http://localhost:4567/api/v1.0/prices?service=recharging", Map.class);
                Map<String, Object> priceMap = (Map<String, Object>) json.get("price");
                priceRecharge = priceMap.get("price").toString();
            } catch (HttpClientErrorException e) {
                e.printStackTrace();
            }

            //Ottieni tipo dell'utente
            Map<String, Object> user = null;
            try {
                Map json = rest.getForObject("http://localhost:4567/api/v1.0/users?username=" + username, Map.class);
                user = (Map<String, Object>) json.get("user");

                if(user.get("type").toString().equals("0.0")){
                    body.put("userType", "Base");
                } else if (user.get("type").toString().equals("1.0")) {
                    body.put("userType", "Premium");
                }
            } catch (HttpClientErrorException e) {
                e.printStackTrace();
            }

            //Controlla se l'utente ha effettuato la ricarica
            try {
                String json = rest.getForObject("http://localhost:4567/api/v1.0/recharges?username=" + user.get("username").toString() + "&completed=yes", String.class);
                System.out.println(json);
                if (json != null) {
                    price += Float.parseFloat(priceRecharge);
                    service += " e Ricarica";
                }
            } catch (HttpClientErrorException e) {
                e.printStackTrace();
            }

            //Elimina la richiesta di ricarica dell'utente
            try {
                rest.delete("http://localhost:4567/api/v1.0/users/" + username + "/recharges");
            } catch (HttpClientErrorException e) {
                e.printStackTrace();
            }

            //Elimina l'eventuale prenotazione dell'utente
            try {
                rest.delete("http://localhost:4567/api/v1.0/users/" + username + "/reservations");
            } catch (HttpClientErrorException e) {
                e.printStackTrace();
            }

            body.put("username", username);
            body.put("date", LocalDate.now().toString());
            body.put("time", LocalTime.now().toString());
            body.put("type", service);
            body.put("price", price.toString());

            String jsonStringPayment = gson.toJson(body);

            try {
                ResponseEntity<String> apiResponsePayment = rest.postForEntity("http://localhost:4567/api/v1.0/payments", jsonStringPayment, String.class);
                if (apiResponsePayment.getStatusCode().is2xxSuccessful()) {
                    errorMessage = "";
                    res.redirect("/");
                }
                else res.redirect("/exitPayment");
            } catch (HttpClientErrorException e) {
                res.redirect("/exitPayment");
            }
            res.redirect("/");
        }  else {
            //Creazione del messaggio di errore
            req.session().attribute("errorMessage", errorMessage);

            //Ricompilazione dei campi
            req.session().attribute("card", card);

            res.redirect("/exitPayment");
        }

        return null;
    };
}
