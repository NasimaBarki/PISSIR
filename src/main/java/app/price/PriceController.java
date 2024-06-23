package app.price;

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
        String username = req.session().attribute("authenticated");
        String admin = req.session().attribute("admin");
        Map<Object, Object> model = new HashMap<>();

        if(username != null && admin != null){
            model.put("authenticated", username);
            model.put("admin", admin);
        } else halt(401, "Devi accedere ed essere un admin per visualizzare questa pagina.");

        Price parkingPrice = PriceDao.getServiceAndPrice("parking");
        Price rechargingPrice = PriceDao.getServiceAndPrice("recharging");

        model.put("parkingPrice", String.format("%.2f", parkingPrice.getPrice()));
        model.put("rechargingPrice", String.format("%.2f", rechargingPrice.getPrice()));

        return new HandlebarsTemplateEngine().render(
                new ModelAndView(model, "layouts/price.hbs")
        );
    };

    public static Route handleParkingPriceChange = (Request req, Response res) -> {
        String price = req.queryParams("parking");

        int error = PriceDao.changePrice(Float.parseFloat(price), "parking");

        //TODO display dell'errore nella pagina web
        if(error == 0){
            System.out.println("Impossibile cambiare il prezzo.");
        }

        res.redirect("/price");

        return null;
    };

    public static Route handleRechargePriceChange = (Request req, Response res) -> {
        String price = req.queryParams("recharging");

        int error = PriceDao.changePrice(Float.parseFloat(price), "recharging");

        if(error == 0){
            System.out.println("Impossibile cambiare il prezzo.");
        }

        res.redirect("/price");

        return null;
    };
}
