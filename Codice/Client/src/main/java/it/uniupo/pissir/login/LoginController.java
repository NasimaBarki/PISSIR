package it.uniupo.pissir.login;

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

public class LoginController {

    //Display della pagina di login
    public static Route serveLoginPage = (Request req, Response res) -> {
        Map<Object, Object> model = new HashMap<>();

        //Se esiste un messaggio di errore mostralo e ricompila il campo username dell'ultima get
        if(req.session().attribute("errorMessage") != null){
            model.put("username", req.session().attribute("username"));
            model.put("errorMessage", req.session().attribute("errorMessage"));
        }

        return new HandlebarsTemplateEngine().render(
                new ModelAndView(model, "layouts/login.hbs")
        );
    };

    //Login dell'utente con creazione della sessione
    public static Route handleLoginPost = (Request req, Response res) -> {
        //Ottengo dal form username e password
        String username = req.queryParams("username");
        String password = req.queryParams("password");

        String errorMessage = "Le credenziali sono errate.";

        RestTemplate restTemplate = new RestTemplate();

        //Chiamata alla REST API. Se l'utente esiste viene reindirizzato alla home, altrimenti viene riportato alla pagina di login
        try {
            ResponseEntity<String> apiResponse = restTemplate.getForEntity("http://localhost:4567/api/v1.0/users?username=" + username + "&password=" + password, String.class);

            if(apiResponse.getStatusCode().is2xxSuccessful()){
                //Recupero il json dell'utente
                Map json = restTemplate.getForObject("http://localhost:4567/api/v1.0/users?username=" + username, Map.class);
                Map<String, Object> user = (Map<String, Object>) json.get("user");

                //Creazione della sessione
                req.session(true).attribute("authenticated", user.get("username"));

                if(user.get("type").toString().equals("1.0")){
                    req.session(true).attribute("premium", "true");
                } else if (user.get("type").toString().equals("2.0")) {
                    req.session(true).attribute("admin", "true");
                }

                //Senza questo il messaggio di errore compare negli altri form
                errorMessage = "";
                req.session().attribute("errorMessage", errorMessage);

                if (user.get("type").toString().equals("2.0")){
                    //TODO reindirizzare l'admin a monitora posti
                    res.redirect("/");
                } else res.redirect("/recharge");

            }
        } catch (HttpClientErrorException e) {
            //Creazione del messaggio di errore
            req.session().attribute("errorMessage", errorMessage);
            //Ricompila in automatico il campo username
            req.session().attribute("username", username);

            res.redirect("/login");
        }

        return null;
    };
}
