package app.logout;

import spark.Request;
import spark.Response;
import spark.Route;

public class LogoutController {
    //TODO capire perché non posso settare la logout come post

    public static Route handleLogoutPost = (Request req, Response res) -> {
        //Rimozione della sessione
        req.session().removeAttribute("username");
        res.redirect("/");

        return null;
    };
}
