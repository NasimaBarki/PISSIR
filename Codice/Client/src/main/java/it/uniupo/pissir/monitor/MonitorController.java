package it.uniupo.pissir.monitor;

import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.template.handlebars.HandlebarsTemplateEngine;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MonitorController {
    public static Route serveMonitorPage = (Request req, Response res) -> {
        RestTemplate rest = new RestTemplate();
        Map<Object, Object> model = new HashMap<>();

        try {
            Map json = rest.getForObject("http://localhost:4567/api/v1.0/parkingSpots?available=yes", Map.class);
            model.put("cars", json.get("cars"));
        } catch (HttpClientErrorException e) {
            model.put("cars", "0");
        }

        return new HandlebarsTemplateEngine().render(
                new ModelAndView(model, "layouts/monitor.hbs")
        );
    };
}
