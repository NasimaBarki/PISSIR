package app.parkingSpot;

import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.template.handlebars.HandlebarsTemplateEngine;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static spark.Spark.halt;

public class ParkingSpotController {
    static Gson gson = new Gson();

    public static Route serverParkingPage = (Request req, Response res) -> {
        String username = req.session().attribute("authenticated");
        String admin = req.session().attribute("admin");
        Map<Object, Object> model = new HashMap<>();

        if(username != null && admin != null){
            model.put("authenticated", username);
            model.put("admin", admin);
        } else halt(401, "Devi accedere per visualizzare questa pagina.");

        List<ParkingSpot> allParkingSpots = ParkingSpotDao.getAllParkingSpots();
        Map<String, List<ParkingSpot>> finalJson = new HashMap<>();
        finalJson.put("parkingSpots", allParkingSpots);

        model.put("parkingSpot", gson.toJson(finalJson));

        return new HandlebarsTemplateEngine().render(
                new ModelAndView(model, "layouts/parking.hbs")
        );
    };
}
