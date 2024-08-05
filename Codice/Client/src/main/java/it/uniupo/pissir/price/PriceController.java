package it.uniupo.pissir.price;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
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

public class PriceController {
    public static Route servePricePage = (Request req, Response res) -> {
        RestTemplate rest = new RestTemplate();

        //Controllo se l'utente è autenticato e che sia anche un admin
        String username = req.session().attribute("authenticated");
        String admin = req.session().attribute("admin");

        Map model;

        try {
            model = rest.getForObject("http://localhost:4567/api/v1.0/prices", Map.class);
        } catch (HttpClientErrorException e) {
            model = new HashMap<>();
        }

        if(username != null && admin != null){
            model.put("authenticated", username);
            model.put("admin", admin);
        } else halt(401, "Devi accedere ed essere un admin per visualizzare questa pagina.");

        return new HandlebarsTemplateEngine().render(
                new ModelAndView(model, "layouts/price.hbs")
        );
    };

    public static Route handleParkingPriceChange = (Request req, Response res) -> {
        RestTemplate rest = new RestTemplate();

        String price = req.queryParams("parking");

        try {
                rest.exchange("http://localhost:4567/api/v1.0/prices?service=parking&price=" + price, HttpMethod.PUT, HttpEntity.EMPTY, Void.class);
        } catch (HttpClientErrorException e) {
            e.printStackTrace();
        }

        res.redirect("/price");

        return null;
    };

    public static Route handleRechargePriceChange = (Request req, Response res) -> {
        RestTemplate rest = new RestTemplate();

        String price = req.queryParams("recharging");

        try {
            rest.exchange("http://localhost:4567/api/v1.0/prices?service=recharging&price=" + price, HttpMethod.PUT, HttpEntity.EMPTY, Void.class);
        } catch (HttpClientErrorException e) {
            e.printStackTrace();
        }

        res.redirect("/price");

        return null;
    };;
}
