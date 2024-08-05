package it.uniupo.pissir.car;

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

public class CarController {
    public static Route serveCarPage = (Request req, Response res) -> {
        RestTemplate restTemplate = new RestTemplate();

        //Ruoli diversi visualizzano una barra di navigazione diversa
        String username = req.session().attribute("authenticated");
        String premium = req.session().attribute("premium");
        String admin = req.session().attribute("admin");

        Map model;

        try {
            model = restTemplate.getForObject("http://localhost:4567/api/v1.0/users/" + username + "/car", Map.class);
        } catch (HttpClientErrorException e) {
            model = new HashMap<>();
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
                new ModelAndView(model, "layouts/car.hbs")
        );
    };

    public static Route serveRegisterCarPage = (Request req, Response res) -> {
        //Ruoli diversi visualizzano una barra di navigazione diversa
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

        //Se esiste un messaggio di errore mostralo e ricompila i campi
        if(req.session().attribute("errorMessage") != null){
            model.put("licensePlate", req.session().attribute("licensePlate"));
            model.put("model", req.session().attribute("model"));
            model.put("capacity", req.session().attribute("capacity"));

            model.put("errorMessage", req.session().attribute("errorMessage"));
        }

        return new HandlebarsTemplateEngine().render(
                new ModelAndView(model, "layouts/registerCar.hbs")
        );
    };

    public static Route handleRegisterCarPost = (Request req, Response res) -> {
        RestTemplate restTemplate = new RestTemplate();
        Gson gson = new Gson();

        String errorMessage = "";

        //Recupero i campi dal form
        String username = req.session().attribute("authenticated");
        String licensePlate = req.queryParams("licensePlate");
        String model = req.queryParams("model");
        String capacity = req.queryParams("capacity");

        //Validazione targa
        if (!licensePlate.matches("[a-zA-Z]{2}[0-9]{3,4}[a-zA-Z]{2}")) {
            errorMessage = "La targa non è corretta.";
        }

        //Controlla se la targa è già stata registrata
        try {
            ResponseEntity<String> apiResponse = restTemplate.getForEntity("http://localhost:4567/api/v1.0/cars/" + licensePlate, String.class);
            if (apiResponse.getStatusCode().is2xxSuccessful()) {
                errorMessage = "La targa è già stata registrata.";
            }
        } catch (HttpClientErrorException e) {
            System.out.println(e + "\nLa targa non esiste nel database.");
        }

        if (errorMessage.isEmpty()){
            //Genero il json da mandare alla REST API
            Map<String, String> body = new HashMap<>();
            body.put("username", username);
            body.put("licensePlate", licensePlate);
            body.put("model", model);
            body.put("capacity", capacity);

            String jsonString = gson.toJson(body);

            //Chiamata alla REST API
            try {
                ResponseEntity<String> apiResponse = restTemplate.postForEntity("http://localhost:4567/api/v1.0/cars", jsonString, String.class);

                if (apiResponse.getStatusCode().is2xxSuccessful()) {
                    //Senza questo il messaggio di errore compare negli altri form
                    errorMessage = "";
                    req.session().attribute("errorMessage", errorMessage);

                    res.redirect("/car");
                }
            } catch (HttpClientErrorException e) {
                errorMessage = "Si è verificato un problema.";
                req.session().attribute("errorMessage", errorMessage);

                res.redirect("/registerCar");
            }
        } else {
            //Creazione del messaggio di errore
            req.session().attribute("errorMessage", errorMessage);

            //Ricompilazione dei campi
            req.session().attribute("licensePlate", licensePlate);
            req.session().attribute("model", model);
            req.session().attribute("capacity", Integer.valueOf(capacity));

            res.redirect("/registerCar");
        }

        return null;
    };
}
