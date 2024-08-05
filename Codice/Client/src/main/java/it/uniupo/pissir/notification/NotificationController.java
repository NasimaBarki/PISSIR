package it.uniupo.pissir.notification;

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

public class NotificationController {
    static int LIMIT = 10;
    static int offset = 0;

    public static Route serveNotificationPage = (Request req, Response res) -> {
        RestTemplate rest = new RestTemplate();

        //Controllo se l'utente è autenticato e che sia anche un admin
        String username = req.session().attribute("authenticated");
        String admin = req.session().attribute("admin");
        String premium = req.session().attribute("premium");

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
            model = rest.getForObject("http://localhost:4567/api/v1.0/notifications?username=" + username + "&limit=" + LIMIT + "&offset=" + offset, Map.class);
        } catch (HttpClientErrorException e) {
            model = new HashMap<>();
            model.put("noNotifications", "noNotifications");
        }

        //Rimozione del bottone next
        try {
            int nextOffset = offset + LIMIT;
            rest.getForEntity("http://localhost:4567/api/v1.0/notifications?username=" + username + "&limit=" + LIMIT + "&offset=" + nextOffset, String.class);
        } catch (HttpClientErrorException e) {
            model.put("noNext", "noNext");
        }

        //Rimozione del bottone precedente alla prima pagina
        if (offset == 0){
            model.put("noPrev", "noPrev");
        }

        if(username != null){
            model.put("authenticated", username);
        }else halt(401, "Devi accedere per visualizzare questa pagina.");
        //Se l'utente è premium vedrà le sezioni ricariche e prenotazioni
        if (premium != null){
            model.put("premium", premium);
        }
        //Se l'utente è admin vedrà: monitora posti, aggiorna prezzi, e pagamenti
        if (admin != null){
            model.put("admin", admin);
        }

        return new HandlebarsTemplateEngine().render(
                new ModelAndView(model, "layouts/notification.hbs")
        );
    };
}
