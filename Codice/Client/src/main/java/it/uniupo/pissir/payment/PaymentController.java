package it.uniupo.pissir.payment;

import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.template.handlebars.HandlebarsTemplateEngine;

import java.util.HashMap;
import java.util.Map;

import static spark.Spark.halt;

public class PaymentController {
    static int LIMIT = 10;
    static int offset = 0;

    public static Route servePaymentPage = (Request req, Response res) -> {

        RestTemplate rest = new RestTemplate();

        //Controllo se l'utente è autenticato e che sia anche un admin
        String username = req.session().attribute("authenticated");
        String admin = req.session().attribute("admin");

        String start = req.session().attribute("start");
        String service = req.session().attribute("service");
        String userType = req.session().attribute("userType");

        Map model;

        //Pagination
        String action = req.queryParams("action");

        if ("next".equals(action)) {
            offset += LIMIT;
        } else if ("prev".equals(action)) {
            offset -= LIMIT;
            if (offset < 0) {
                offset = 0;
            }
        }

        //Generazione della stringa con eventuali filtri
        String additionalUrl = "";

        if (start != null || service != null || userType != null) {
            additionalUrl = "&";
            if (start != null) {
                additionalUrl += "start=" + start + "&";
            }
            if (service != null) {
                additionalUrl += "service=" + service + "&";
            }
            if (userType != null) {
                additionalUrl += "userType=" + userType + "&";
            }
        }

        try {
            model = rest.getForObject("http://localhost:4567/api/v1.0/payments?limit=" + LIMIT + "&offset=" + offset + additionalUrl, Map.class);
        } catch (HttpClientErrorException e) {
            model = new HashMap<>();
        }

        //Rimozione del bottone next
        try {
            int nextOffset = offset + LIMIT;
            rest.getForEntity("http://localhost:4567/api/v1.0/payments?limit=" + LIMIT + "&offset=" + nextOffset + additionalUrl, String.class);
        } catch (HttpClientErrorException e) {
            model.put("noNext", "noNext");
        }

        //Rimozione del bottone precedente alla prima pagina
        if (offset == 0){
            model.put("noPrev", "noPrev");
        }

        //Se sono stati applicati dei filtri li reinserisco nella sidebar
        if(start != null){
            model.put("start", start);
        }
        if(service != null){
            model.put("service", service);
        }
        if(userType != null){
            model.put("userType", userType);
        }

        if(username != null && admin != null){
            model.put("authenticated", username);
            model.put("admin", admin);
        } else halt(401, "Devi accedere ed essere un admin per visualizzare questa pagina.");

        return new HandlebarsTemplateEngine().render(
                new ModelAndView(model, "layouts/payment.hbs")
        );
    };

    public static Route handlePaymentPost = (Request req, Response res) -> {
        //Ottengo i filtri
        String start = req.queryParams("start");
        String service = req.queryParams("service");
        String userType = req.queryParams("userType");

        req.session(true).attribute("start", start);
        req.session(true).attribute("service", service);
        req.session(true).attribute("userType", userType);

        res.redirect("/payment");
        return null;
    };
}
