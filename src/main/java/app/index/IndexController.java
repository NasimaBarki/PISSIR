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
        //Far vedere prenotazioni al ruolo premium
        String username = req.session().attribute("authenticated");
        String premium = req.session().attribute("premium");
        String admin = req.session().attribute("admin");
        Map<Object, Object> model = new HashMap<>();

        if(username != null){
            model.put("authenticated", username);
        }
        if (premium != null){
            model.put("premium", premium);
        }
        if (admin != null){
            model.put("admin", admin);
        }


        return new HandlebarsTemplateEngine().render(
                new ModelAndView(model, "layouts/index.hbs")
        );
    };
}
