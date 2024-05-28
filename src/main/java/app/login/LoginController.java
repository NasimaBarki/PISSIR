package app.login;

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
        String redirectUri = "http://localhost:8000/"; // Replace with your callback URL
        String clientId = "parcheggioClient"; // Replace with your client ID
        String clientSecret = "b59a8511-fca2-4ee7-bc56-8bd081cfc8e5"; // Replace with your client secret
        String scope = "openid"; // Replace with the scopes you want to request

        String authUrl = "http://localhost:8080/auth/realms/parcheggioRealm/protocol/openid-connect/auth"
                + "?response_type=code"
                + "&response_mode=fragment"
                + "&client_id=" + clientId
                + "&clientSecret" + clientSecret
                + "&redirect_uri=" + redirectUri
                + "&scope=" + scope;

        res.redirect(authUrl);

        return null;
        /*Map<Object, Object> model = new HashMap<>();
        return new HandlebarsTemplateEngine().render(
                new ModelAndView(model, "layouts/login.hbs")
        );*/
    };

    //Gestione del login dell'utente
    public static Route handleLoginPost = (Request req, Response res) -> {
        String username = req.queryParams("username");
        String password = req.queryParams("password");

        String errorMessage = "L'utente o la password sono errati.";

        int error;

        error = userDao.getUser(username, password);

        if(error == 0){
            res.redirect("/");
        }
        else {
            Map<Object, Object> model = new HashMap<>();

            if(error == 1){
                errorMessage = "L'utente non esiste.";
            } else if(error == 2){
                errorMessage = "La password è errata.";
            }

            model.put("errorMessage", errorMessage);
            model.put("username", username);

            return new HandlebarsTemplateEngine().render(
                    new ModelAndView(model, "layouts/login.hbs")
            );
        }

        return null;
    };
}
