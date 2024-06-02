package app.signup;

import app.user.UserDao;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.template.handlebars.HandlebarsTemplateEngine;

import java.util.HashMap;
import java.util.Map;

public class SignupController {
    //TODO aggiustare la post in modo tale che non crei un nuovo model ogni volta ma che passi i parametri alla get
    static UserDao userDao = new UserDao();

    //Visualizza la pagina di registrazione
    public static Route serveSignupPage = (Request req, Response res) -> {
        Map<Object, Object> model = new HashMap<>();
        return new HandlebarsTemplateEngine().render(
                new ModelAndView(model, "layouts/signup.hbs")
        );
    };

    //Gestisci la registrazione dell'utente
    public static Route handleSignupPost = (Request req, Response res) -> {
        String name = req.queryParams("name");
        String surname = req.queryParams("surname");
        String email = req.queryParams("email");
        String username = req.queryParams("username");
        String password = req.queryParams("password");

        String errorMessage;

        int error;

        error = userDao.addUser(name, surname, email, username, password);

        switch(error) {
            case 1:
                errorMessage = "Il nome può contenere solo lettere.";
                break;
            case 2:
                errorMessage = "Il cognome può contenere solo lettere.";
                break;
            case 3:
                errorMessage = "L'username è troppo lungo. La lunghezza consentita è di 25 caratteri.";
                break;
            case 4:
                errorMessage = "La password deve contenere almeno 8 caratteri.";
                break;
            case 5:
                errorMessage = "La password deve contenere almeno una maiuscola.";
                break;
            case 6:
                errorMessage = "La password deve contenere almeno una minuscola.";
                break;
            case 7:
                errorMessage = "La password deve contenere almeno un numero.";
                break;
            case 8:
                errorMessage = "La password deve contenere almeno un carattere speciale."; //TODO specificare quali caratteri speciali
                break;
            case 9:
                errorMessage = "Il nome deve contenere almeno tre caratteri.";
                break;
            case 10:
                errorMessage = "Il cognome deve contenere almeno tre caratteri.";
                break;
            case 11:
                errorMessage = "L'email non può essere vuota.";
                break;
            case 12:
                errorMessage = "L'username deve contenere almeno tre caratteri.";
                break;
            default:
                errorMessage = "Spiacenti, si è verificato un errore.";
        }

        if(error == 0){
            res.redirect("/login");
        }
        else {
            Map<Object, Object> model = new HashMap<>();

            //Passo a model il messaggio di errore
            model.put("errorMessage", errorMessage);

            //Passo anche i vari campi, altrimenti vengono cancellati
            model.put("name", name);
            model.put("surname", surname);
            model.put("email", email);
            model.put("username", username);

            return new HandlebarsTemplateEngine().render(
                    new ModelAndView(model, "layouts/signup.hbs")
            );
        }
        return null;
    };
}
