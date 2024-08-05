package it.uniupo.pissir.recharge;

import com.google.gson.Gson;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.template.handlebars.HandlebarsTemplateEngine;

import java.util.HashMap;
import java.util.Map;

import static spark.Spark.halt;

public class RechargeController {

    public static Route serveRechargePage = (Request req, Response res) -> {
        String username = req.session().attribute("authenticated");
        String premium = req.session().attribute("premium");
        String admin = req.session().attribute("admin");

        RestTemplate rest = new RestTemplate();
        Map model;

        try {
            //Ottieni le ricariche dell'utente
            model = rest.getForObject("http://localhost:4567/api/v1.0/recharges?username=" + username, Map.class);
        } catch (HttpClientErrorException e) {
            model = new HashMap<>();
            model.put("noRecharge", "noRecharge");
        }

        //Controlla se l'utente ha già registrato la propria auto
        try {
            ResponseEntity<String> apiResponse = rest.getForEntity("http://localhost:4567/api/v1.0/users/" + username + "/car", String.class);
        } catch (HttpClientErrorException e) {
            model.put("noCar", "noCar");
            System.out.println("Non è ancora stata registrata l'auto.");
        }

        if(username != null){
            model.put("authenticated", username);
        } else halt(401, "Devi accedere per visualizzare questa pagina.");

        if (premium != null){
            model.put("premium", premium);
        }
        if (admin != null){
            model.put("admin", admin);
        }

        return new HandlebarsTemplateEngine().render(
                new ModelAndView(model, "layouts/recharge.hbs")
        );
    };

    public static Route serveRechargeRequestPage = (Request req, Response res) -> {
        RestTemplate restTemplate = new RestTemplate();

        String username = req.session().attribute("authenticated");
        String premium = req.session().attribute("premium");
        String admin = req.session().attribute("admin");

        //Controlla se l'utente ha già una richiesta
        try {
            ResponseEntity<String> apiResponse = restTemplate.getForEntity("http://localhost:4567/api/v1.0/recharges?username=" + username , String.class);

            if (apiResponse.getStatusCode().is2xxSuccessful()) {
                res.redirect("/recharge");
            }
        } catch (HttpClientErrorException e) {
            e.printStackTrace();
        }

        Map model = new HashMap<>();

        try {
            model = restTemplate.getForObject("http://localhost:4567/api/v1.0/recharges?completed=no", Map.class);
        } catch (HttpClientErrorException e) {
            model.put("rechargeQueue", "0");
        }

        if(username != null){
            model.put("authenticated", username);
        } else halt(401, "Devi accedere per visualizzare questa pagina.");

        if (premium != null){
            model.put("premium", premium);
        }
        if (admin != null){
            model.put("admin", admin);
        }

        //Mostra il messaggio di errore e ricompila il campo percentuale
        if(req.session().attribute("errorMessage") != null){
            model.put("errorMessage", req.session().attribute("errorMessage"));
            model.put("percentage", req.session().attribute("percentage"));
        }

        return new HandlebarsTemplateEngine().render(
                new ModelAndView(model, "layouts/rechargeRequest.hbs")
        );
    };

    public static Route handleRechargeRequestPost = (Request req, Response res) -> {
        RestTemplate restTemplate = new RestTemplate();
        Gson gson = new Gson();

        String username = req.session().attribute("authenticated");
        String percentage = req.queryParams("percentage");
        String notification = req.queryParams("notification");
        int completed = 0;

        //Trasformo il valore di notifica in un int
        if(notification.equals("yes")){
            notification = "1";
        } else notification = "0";

        Map<String, String> body = new HashMap<>();
        body.put("username", username);
        body.put("percentage", percentage);
        body.put("notification", notification);
        body.put("completed", String.valueOf(completed));

        String jsonString = gson.toJson(body);

        try {
            ResponseEntity<String> apiResponse = restTemplate.postForEntity("http://localhost:4567/api/v1.0/recharges", jsonString, String.class);

            if(apiResponse.getStatusCode().is2xxSuccessful()){
                res.redirect("/recharge");
            }
        } catch (HttpClientErrorException e) {
            String errorMessage = "Si è verificato un problema.";
            req.session().attribute("errorMessage", errorMessage);

            res.redirect("/recharge");
        }

        return null;
    };
}
