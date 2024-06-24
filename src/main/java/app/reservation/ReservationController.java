package app.reservation;

import app.car.CarDao;
import app.parkingSpot.ParkingSpot;
import app.parkingSpot.ParkingSpotDao;
import app.payment.Payment;
import app.payment.PaymentDao;
import app.price.Price;
import app.price.PriceDao;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.springframework.cglib.core.Local;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.template.handlebars.HandlebarsTemplateEngine;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static spark.Spark.halt;

public class ReservationController {
    static Gson gson = new Gson();

    public static Route serveReservationPage = (Request req, Response res) -> {
        //Controllo se l'utente è autenticato e che sia anche un admin
        String username = req.session().attribute("authenticated");
        String premium = req.session().attribute("premium");
        Map<Object, Object> model = new HashMap<>();

        if(username != null && premium != null){
            model.put("authenticated", username);
            model.put("premium", premium);
        } else halt(401, "Devi accedere ed essere un utente premium per visualizzare questa pagina.");

        //Ottieni tutti i posti dal DB
        List<ParkingSpot> allParkingSpots = ParkingSpotDao.getPremiumParkingSpots();
        List<Reservation> allReservations = ReservationDao.getAllReservations(username);

        //Carica tutte le informazioni sui posti in model
        String parkingSpotJson = gson.toJson(allParkingSpots);
        List<ParkingSpot> parkingSpots = gson.fromJson(parkingSpotJson, new TypeToken<List<ParkingSpot>>(){}.getType());
        model.put("parkingSpot", parkingSpots);

        String reservationsJson = gson.toJson(allReservations);
        List<Reservation> reservations = gson.fromJson(reservationsJson, new TypeToken<List<Reservation>>(){}.getType());
        model.put("reservation", reservations);

        return new HandlebarsTemplateEngine().render(
                new ModelAndView(model, "layouts/reservation.hbs")
        );
    };

    public static Route serveReservationRequestPage = (Request req, Response res) -> {
        //Controllo se l'utente è autenticato e che sia anche un admin
        String username = req.session().attribute("authenticated");
        String premium = req.session().attribute("premium");
        Map<Object, Object> model = new HashMap<>();

        if(username != null && premium != null){
            model.put("authenticated", username);
            model.put("premium", premium);
        } else halt(401, "Devi accedere ed essere un utente premium per visualizzare questa pagina.");

        if(CarDao.getCar(username) == null){
            halt(401, "Devi inserire i dati della tua auto prima di poter richiedere una ricarica");
        }

        return new HandlebarsTemplateEngine().render(
                new ModelAndView(model, "layouts/reservationRequest.hbs")
        );
    };

    public static Route handleReservationPost = (Request req, Response res) -> {
        String username = req.session().attribute("authenticated");
        String arrival = req.queryParams("arrival");
        String duration = req.queryParams("duration");

        Price parking = PriceDao.getServiceAndPrice("parking");

        ParkingSpot parkingSpotNumber = ParkingSpotDao.getFreePremiumSpot();

        Reservation reservation = new Reservation(username, arrival, Integer.parseInt(duration), true, parkingSpotNumber.getId(), false);
        //TODO decidere se il prezzo della prenotazione corrisponde a quello della sosta
        //TODO il prezzo della sosta va ad ore
        Payment payment = new Payment(username, LocalDate.now(), LocalTime.now(), "Sosta", parking.getPrice(), "Premium");

        //TODO controllo numero carta
        int error = ReservationDao.addReservation(reservation);
        int errorPayment = PaymentDao.addPayment(payment);

        res.redirect("/reservation");

        return null;
    };
}
