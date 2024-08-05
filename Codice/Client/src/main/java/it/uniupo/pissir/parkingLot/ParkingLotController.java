package it.uniupo.pissir.parkingLot;

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

public class ParkingLotController {
    static int LIMIT = 5;
    static int offset = 0;

    public static Route serveParkingLotPage = (Request req, Response res) -> {
        RestTemplate rest = new RestTemplate();

        //Controllo se l'utente è autenticato e che sia anche un admin
        String username = req.session().attribute("authenticated");
        String admin = req.session().attribute("admin");

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

        try {
            model = rest.getForObject("http://localhost:4567/api/v1.0/parkingSpots?limit=" + LIMIT + "&offset=" + offset, Map.class);
        } catch (HttpClientErrorException e) {
            model = new HashMap<>();
        }

        //Rimozione del bottone next
        try {
            int nextOffset = offset + LIMIT;
            rest.getForEntity("http://localhost:4567/api/v1.0/parkingSpots?limit=" + LIMIT + "&offset=" + nextOffset, String.class);
        } catch (HttpClientErrorException e) {
            model.put("noNext", "noNext");
        }

        //Rimozione del bottone precedente alla prima pagina
        if (offset == 0){
            model.put("noPrev", "noPrev");
        }

        if(username != null && admin != null){
            model.put("authenticated", username);
            model.put("admin", admin);
        } else halt(401, "Devi accedere ed essere un admin per visualizzare questa pagina.");

        return new HandlebarsTemplateEngine().render(
                new ModelAndView(model, "layouts/parking.hbs")
        );
    };
}
