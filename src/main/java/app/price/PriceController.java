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

        model.put("parkingPrice", parkingPrice.getPrice());
        model.put("rechargingPrice", rechargingPrice.getPrice());

        return new HandlebarsTemplateEngine().render(
                new ModelAndView(model, "layouts/price.hbs")
        );
    };

    public static Route handleParkingPriceChange = (Request req, Response res) -> {
        String service = req.params("parking");
        System.out.println(service);

        res.redirect("/price");

        //Non va

        return null;
    };
}
