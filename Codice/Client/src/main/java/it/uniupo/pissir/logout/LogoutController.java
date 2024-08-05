package it.uniupo.pissir.logout;

import spark.Request;
import spark.Response;
import spark.Route;

public class LogoutController {

    //Logout dell'utente
    public static Route handleLogout = (Request req, Response res) -> {
        //Rimozione della sessione
        req.session().removeAttribute("authenticated");
        req.session().removeAttribute("premium");
        req.session().removeAttribute("admin");
        res.redirect("/");

        return null;
    };
}
