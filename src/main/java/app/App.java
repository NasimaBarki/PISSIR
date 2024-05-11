package app;

import app.user.UserDao;
import spark.ModelAndView;
import spark.template.handlebars.HandlebarsTemplateEngine;

import java.util.HashMap;
import java.util.Map;

import static spark.Spark.*;

public class App {
    public static void main(String[] args) {

        UserDao userDao = new UserDao();

        staticFileLocation("/public");

        //Pagina iniziale
        get("/", (req, res) -> {
            Map<Object, Object> model = new HashMap<>();
            return new HandlebarsTemplateEngine().render(
                    new ModelAndView(model, "layouts/index.hbs")
            );
        });

        //Pagina login
        get("/login", (req, res) -> {
            Map<Object, Object> model = new HashMap<>();
            return new HandlebarsTemplateEngine().render(
                    new ModelAndView(model, "layouts/login.hbs")
            );
        });

        //Pagina di registrazione
        get("/signup", (req, res) -> {
            Map<Object, Object> model = new HashMap<>();
            return new HandlebarsTemplateEngine().render(
                    new ModelAndView(model, "layouts/signup.hbs")
            );
        });

        //Registrazione di un nuovo utente
        post("/signup", (req, res) -> {
            String name = req.queryParams("name");
            String surname = req.queryParams("surname");
            String email = req.queryParams("email");
            String username = req.queryParams("username");
            String password = req.queryParams("password");

            String errorMessage;

            System.out.println(name);
            System.out.println(surname);

            int error = 0;

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
                    errorMessage = "Il nome non può essere vuoto.";
                    break;
                case 10:
                    errorMessage = "Il cognome non può essere vuoto.";
                    break;
                case 11:
                    errorMessage = "L'email non può essere vuota.";
                    break;
                case 12:
                    errorMessage = "L'username non può essere vuoto.";
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
        });

        //Login di un utente
        post("/login", (req, res) -> {

            return null;
        });
    }
}
