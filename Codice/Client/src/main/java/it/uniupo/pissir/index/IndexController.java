package it.uniupo.pissir.index;

import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.template.handlebars.HandlebarsTemplateEngine;

import java.util.HashMap;
import java.util.Map;

public class IndexController {

    //Display della pagina home
    public static Route serveIndexPage = (Request req, Response res) -> {
        Map<Object, Object> model = new HashMap<>();

        //Reset del messaggio di error
        req.session().attribute("errorMessage", "");

        //Ruoli diversi visualizzano una barra di navigazione diversa
        String username = req.session().attribute("authenticated");
        String premium = req.session().attribute("premium");
        String admin = req.session().attribute("admin");

        //Se l'utente non è autenticato verrà mostrato soltanto login nella navbar
        if(username != null){
            model.put("authenticated", username);
        }
        //Se l'utente è premium vedrà le sezioni ricariche e prenotazioni
        if (premium != null){
            model.put("premium", premium);
        }
        //Se l'utente è admin vedrà: monitora posti, aggiorna prezzi, e pagamenti
        if (admin != null){
            model.put("admin", admin);
        }

        return new HandlebarsTemplateEngine().render(
                new ModelAndView(model, "layouts/index.hbs")
        );
    };
}
