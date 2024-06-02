package app.recharge;

import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.template.handlebars.HandlebarsTemplateEngine;

import java.util.HashMap;
import java.util.Map;

import static spark.Spark.halt;

public class RechargeController {
    //Pagina in cui si vede lo stato della ricarica
    public static Route serveRechargePage = (Request req, Response res) -> {
        String username = req.session().attribute("username");
        Map<Object, Object> model = new HashMap<>();

        if(username != null){
            model.put("username", username);
        } else halt(401, "Devi accedere per visualizzare questa pagina.");

        int rechargeQueue = RechargeDao.getNumberOfRechargeRequests();
        Recharge recharge = RechargeDao.getRechargeRequest(username);

        model.put("rechargeQueue", rechargeQueue);
        model.put("percentage", recharge.getPercentage());

        return new HandlebarsTemplateEngine().render(
                new ModelAndView(model, "layouts/recharge.hbs")
        );
    };

    //Form per la richiesta di ricarica
    public static Route serveRechargeRequest = (Request req, Response res) -> {
        String username = req.session().attribute("username");
        Map<Object, Object> model = new HashMap<>();

        if(username != null){
            model.put("username", username);
        } else halt(401, "Devi accedere per visualizzare questa pagina.");

        if(req.session().attribute("errorMessage") != null){
            model.put("errorMessage", req.session().attribute("errorMessage"));
            model.put("percentage", req.session().attribute("percentage"));
        }

        int rechargeQueue = RechargeDao.getNumberOfRechargeRequests();
        model.put("rechargeQueue", rechargeQueue);

        return new HandlebarsTemplateEngine().render(
                new ModelAndView(model, "layouts/rechargeRequest.hbs")
        );
    };

    //In caso di conferma di ricarica si ha una redirect
    public static Route handleRechargePost = (Request req, Response res) -> {
        String percentage = req.queryParams("percentage");
        String notification = req.queryParams("notification");
        String username = req.session().attribute("username");
        int notificationBool = 0;

        if(notification == "yes"){
            notificationBool = 1;
        }

        Recharge recharge = new Recharge(username, Integer.parseInt(percentage), notificationBool);


        int error = RechargeDao.addRechargeRequest(recharge);

        if(error == 1){
            String errorMessage = "È già presente una richiesta di ricarica.";
            req.session().attribute("errorMessage", errorMessage);
            req.session().attribute("percentage", percentage);
            res.redirect("/rechargeRequest");
        }
        else {
            res.redirect("/recharge");
        }

        return null;
    };
}
