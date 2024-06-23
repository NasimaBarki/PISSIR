package app.signup;

import app.user.User;
import app.user.UserDao;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.template.handlebars.HandlebarsTemplateEngine;

import java.util.HashMap;
import java.util.Map;

public class SignupController {
    static UserDao userDao = new UserDao();

    //Visualizza la pagina di registrazione
    public static Route serveSignupPage = (Request req, Response res) -> {
        Map<Object, Object> model = new HashMap<>();

        if(req.session().attribute("errorMessage") != null){
            model.put("errorMessage", req.session().attribute("errorMessage"));
            model.put("name", req.session().attribute("name"));
            model.put("surname", req.session().attribute("surname"));
            model.put("email", req.session().attribute("email"));
            model.put("username", req.session().attribute("username"));
        }
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
        int role = 0;

        String errorMessage;

        int error;

        User user = new User(name, surname, email, username, password, role);
        error = userDao.addUser(user);

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
            case 13:
                errorMessage = "L'username esiste già.";
                break;
            default:
                errorMessage = "Spiacenti, si è verificato un errore.";
        }

        if(error == 0){
            res.redirect("/login");
        }
        else {
            req.session().attribute("name", name);
            req.session().attribute("surname", surname);
            req.session().attribute("email", email);
            req.session().attribute("username", username);
            //TODO azzerrare l'error message altrimenti compare in altri form
            req.session().attribute("errorMessage", errorMessage);

            res.redirect("/signup");
        }
        return null;
    };
}
