package it.uniupo.pissir.signup;

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

public class SignupController {
    //Display della pagina di registrazione
    public static Route serveSignupPage = (Request req, Response res) -> {
        Map<Object, Object> model = new HashMap<>();

        //Se esiste un messaggio di errore mostralo e ricompila i campi
        if(req.session().attribute("errorMessage") != null){
            model.put("name", req.session().attribute("name"));
            model.put("surname", req.session().attribute("surname"));
            model.put("username", req.session().attribute("username"));
            model.put("email", req.session().attribute("email"));

            model.put("errorMessage", req.session().attribute("errorMessage"));
        }

        return new HandlebarsTemplateEngine().render(
                new ModelAndView(model, "layouts/signup.hbs")
        );
    };

    //Creazione di un nuovo utente
    public static Route handleSignupPost = (Request req, Response res) -> {
        //Dimensioni massime e minime di nome, cognome e username
        int MIN_FIELD_SIZE = 3;
        int MAX_FIELD_SIZE = 25;

        Gson gson = new Gson();
        RestTemplate restTemplate = new RestTemplate();

        String errorMessage = "";

        //Recupero i campi dal form
        String name = req.queryParams("name");
        String surname = req.queryParams("surname");
        String email = req.queryParams("email");
        String username = req.queryParams("username");
        String password = req.queryParams("password");

        //Validazione nome
        if (!name.matches("[a-zA-Z]+")) {
            errorMessage = "Il nome deve contenere solo caratteri.";
        }
        if (name.length() > MAX_FIELD_SIZE || name.length() < MIN_FIELD_SIZE) {
            errorMessage = "Il nome deve contenere almeno tre caratteri e non superare i venticinque.";
        }

        //Validazione cognome
        if (!surname.matches("[a-zA-Z]+")) {
            errorMessage = "Il cognome deve contenere solo caratteri.";
        }
        if (surname.length() > MAX_FIELD_SIZE || surname.length() < MIN_FIELD_SIZE) {
            errorMessage = "Il cognome deve contenere almeno tre caratteri e non superare i venticinque.";
        }

        //Validazione username
        if (!username.matches("^\\d*[a-zA-Z][a-zA-Z0-9]*$")){
            errorMessage = "L'username deve contenere almeno un carattere e può contenere numeri.";
        }
        if (username.length() > MAX_FIELD_SIZE || username.length() < MIN_FIELD_SIZE) {
            errorMessage = "L'username deve contenere almeno tre caratteri e non superare i venticinque.";
        }

        try {
            ResponseEntity<String> apiResponse = restTemplate.getForEntity("http://localhost:4567/api/v1.0/users?username=" + username, String.class);
            if (apiResponse.getStatusCode().is2xxSuccessful()) {
                errorMessage = "L'username esiste già.";
            }
        } catch (HttpClientErrorException e) {
            System.out.println(e + "\nL'username non esiste nel database.");
        }

        //Validazione email
        if (!email.matches("^[a-zA-Z0-9_!#$%&'*+/=?``{|}~^.-]+@[a-zA-Z0-9.-]+$")){
            errorMessage = "E-mail non valida.";
        }

        //Validazione password
        if (!password.matches("\\A(?=.*[A-Z])(?=.*\\d)[a-zA-Z0-9]{8,}\\z")){
            errorMessage = "La password deve contenere:\nalmeno 8 caratteri\nalmeno una lettera maiuscola\nalmeno una lettera minuscola\nnon sono ammessi caratteri speciali.";
        }

        if(errorMessage.isEmpty()) {
            //Genero il json da mandare alla REST API
            Map<String, String> body = new HashMap<>();
            body.put("name", name);
            body.put("surname", surname);
            body.put("email", email);
            body.put("username", username);
            body.put("password", password);
            body.put("type", "0");

            String jsonString = gson.toJson(body);

            //Chiamata alla REST API
            try {
                ResponseEntity<String> apiResponse = restTemplate.postForEntity("http://localhost:4567/api/v1.0/users", jsonString, String.class);

                if(apiResponse.getStatusCode().is2xxSuccessful()){
                    //Senza questo il messaggio di errore compare negli altri form
                    errorMessage = "";
                    req.session().attribute("errorMessage", errorMessage);

                    res.redirect("/login");
                }
            } catch (HttpClientErrorException e) {
                errorMessage = "Si è verificato un problema.";
                req.session().attribute("errorMessage", errorMessage);

                res.redirect("/signup");
            }
        } else {
            //Creazione del messaggio di errore
            req.session().attribute("errorMessage", errorMessage);

            //Ricompilazione dei campi
            req.session().attribute("name", name);
            req.session().attribute("name", name);
            req.session().attribute("surname", surname);
            req.session().attribute("email", email);
            req.session().attribute("username", username);

            res.redirect("/signup");
        }

        return null;
    };
}
