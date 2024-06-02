package app.recharge;

import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.template.handlebars.HandlebarsTemplateEngine;

import java.util.HashMap;
import java.util.Map;

public class RechargeController {
    //Pagina in cui si vede lo stato della ricarica
    public static Route serveRechargePage = (Request req, Response res) -> {
        String username = req.session().attribute("username");
        Map<Object, Object> model = new HashMap<>();

        if(username != null){
            System.out.println(username);
            model.put("username", username);
        }

        return new HandlebarsTemplateEngine().render(
                new ModelAndView(model, "layouts/recharge.hbs")
        );
    };

    //Form per la richiesta di ricarica
    public static Route serveRechargeRequest = (Request req, Response res) -> {
        String username = req.session().attribute("username");
        Map<Object, Object> model = new HashMap<>();

        if(username != null){
            System.out.println(username);
            model.put("username", username);
        }

        return new HandlebarsTemplateEngine().render(
                new ModelAndView(model, "layouts/rechargeRequest.hbs")
        );
    };

    //In caso di conferma di ricarica si ha una redirect
    public static Route handleRechargePost = (Request req, Response res) -> {
        res.redirect("/recharge");

        return null;
    };
}
