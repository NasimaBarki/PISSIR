package app.index;

import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.template.handlebars.HandlebarsTemplateEngine;

import java.util.HashMap;
import java.util.Map;

public class IndexController {

    //Visualizza la pagina home
    public static Route serveIndexPage = (Request req, Response res) -> {

        Map<Object, Object> model = new HashMap<>();

        return new HandlebarsTemplateEngine().render(
                new ModelAndView(model, "layouts/index.hbs")
        );
    };
}
