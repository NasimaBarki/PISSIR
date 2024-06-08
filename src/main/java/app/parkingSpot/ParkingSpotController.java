package app.parkingSpot;

import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.template.handlebars.HandlebarsTemplateEngine;
import com.google.gson.Gson;

import java.util.*;

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
        } else halt(401, "Devi accedere ed essere un admin per visualizzare questa pagina.");

        List<ParkingSpot> allParkingSpots = ParkingSpotDao.getAllParkingSpots();
        String parkingSpotJson = gson.toJson(allParkingSpots);

        List<ParkingSpot> parkingSpots = gson.fromJson(parkingSpotJson, new TypeToken<List<ParkingSpot>>(){}.getType());
        model.put("parkingSpot", parkingSpots);

        for (ParkingSpot p : allParkingSpots){
            System.out.println(p.getId());
        }

        return new HandlebarsTemplateEngine().render(
                new ModelAndView(model, "layouts/parking.hbs")
        );
    };
}
