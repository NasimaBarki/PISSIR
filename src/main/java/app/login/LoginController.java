package app.login;

import app.user.User;
import app.user.UserDao;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.template.handlebars.HandlebarsTemplateEngine;

import java.util.HashMap;
import java.util.Map;

public class LoginController {

    static UserDao userDao = new UserDao();

    //Visualizza la pagina di login
    public static Route serveLoginPage = (Request req, Response res) -> {
        res.status(200);

        Map<Object, Object> model = new HashMap<>();

        //Se c'è un messaggio di errore mostra il messaggio e compila il campo username prima della POST
        if(req.session().attribute("errorMessage") != null){
            model.put("username", req.session().attribute("username"));
            model.put("errorMessage", req.session().attribute("errorMessage"));
        }

        return new HandlebarsTemplateEngine().render(
                new ModelAndView(model, "layouts/login.hbs")
        );
    };

    //Gestione del login dell'utente
    public static Route handleLoginPost = (Request req, Response res) -> {
        //Ottengo dal form username e password
        String username = req.queryParams("username");
        String password = req.queryParams("password");

        //Messaggio di errore di default
        String errorMessage = "L'utente o la password sono errati.";

        int error;

        //Controllo se l'username esiste. Se esiste vedo se la password è corretta
        error = userDao.getUserReturnError(username, password);

        if(error == 0){
            User user = userDao.getUser(username);

            //Ritorno come risposta della sessione username e ruolo
            req.session(true).attribute("authenticated", username);
            req.session(true).attribute("type", user.getType());
            int type = req.session().attribute("type");

            if(type == 1){
                req.session(true).attribute("premium", "true");
            } else if (type == 2) {
                req.session(true).attribute("admin", "true");
            }

            if(user.getType() == 2){
                res.redirect("/parkingSpots");
            }
            else{
                res.redirect("/recharge");
            }
        }
        else {
            if(error == 1){
                errorMessage = "L'utente non esiste.";
            } else if(error == 2){
                errorMessage = "La password è errata.";
            }

            req.session().attribute("errorMessage", errorMessage);
            req.session().attribute("username", username);

            res.redirect("/login");
        }

        res.status(201);

        return null;
    };
}
