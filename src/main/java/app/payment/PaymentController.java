package app.payment;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.template.handlebars.HandlebarsTemplateEngine;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static spark.Spark.halt;

public class PaymentController {
    static Gson gson = new Gson();

    public static Route servePaymentPage = (Request req, Response res) -> {
        //Controllo se l'utente è autenticato e che sia anche un admin
        String username = req.session().attribute("authenticated");
        String admin = req.session().attribute("admin");
        Map<Object, Object> model = new HashMap<>();

        if(username != null && admin != null){
            model.put("authenticated", username);
            model.put("admin", admin);
        } else halt(401, "Devi accedere ed essere un admin per visualizzare questa pagina.");

        //Ottieni tutti i posti dal DB
        List<Payment> allPayments = PaymentDao.getAll();

        //Carica tutte le informazioni sui posti in model
        //TODO display dei prezzi come float col simbolo euro finale
        String paymentJson = gson.toJson(allPayments);
        List<Payment> payments = gson.fromJson(paymentJson, new TypeToken<List<Payment>>(){}.getType());
        model.put("payment", payments);

        return new HandlebarsTemplateEngine().render(
                new ModelAndView(model, "layouts/payment.hbs")
        );
    };
}
