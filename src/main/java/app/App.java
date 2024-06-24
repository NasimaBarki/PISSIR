package app;

import app.car.CarController;
import app.index.IndexController;
import app.login.LoginController;
import app.logout.LogoutController;
import app.parkingSpot.ParkingSpotController;
import app.payment.PaymentController;
import app.price.PriceController;
import app.recharge.RechargeController;
import app.reservation.ReservationController;
import app.signup.SignupController;
import spark.Spark;

import static spark.Spark.*;

public class App {
    public static void main(String[] args) {
        port(8000);
        staticFileLocation("/public");

        //Index
        Spark.get("/", IndexController.serveIndexPage);

        //Login
        Spark.get("/login", LoginController.serveLoginPage);
        Spark.post("/login", LoginController.handleLoginPost);

        //Registrazione
        Spark.get("/signup", SignupController.serveSignupPage);
        Spark.post("/signup", SignupController.handleSignupPost);

        //Logout
        Spark.get("/logout", LogoutController.handleLogoutPost);

        //Ricariche
        Spark.get("/recharge", RechargeController.serveRechargePage);
        Spark.get("/rechargeRequest", RechargeController.serveRechargeRequest);
        Spark.post("/rechargeRequest", RechargeController.handleRechargePost);

        //Monitorazione posti
        Spark.get("/parkingSpots", ParkingSpotController.serverParkingPage);

        //Prezzi
        Spark.get("/price", PriceController.servePricePage);
        Spark.post("/price/parking", PriceController.handleParkingPriceChange);
        Spark.post("/price/recharging", PriceController.handleRechargePriceChange);

        //Prenotazioni
        Spark.get("/reservation", ReservationController.serveReservationPage);
        Spark.get("/reservationRequest", ReservationController.serveReservationRequestPage);
        Spark.post("/reservationRequest", ReservationController.handleReservationPost);

        //Pagamenti
        Spark.get("/payment", PaymentController.servePaymentPage);

        //Auto
        Spark.get("/car", CarController.serveCarPage);
        Spark.get("/changeCarInfo", CarController.serveChangeCarInfoPage);
        Spark.get("/registerCar", CarController.serveRegisterCarPage);
        Spark.post("/registerCar", CarController.handleRegisterCarPost);
        Spark.post("/changeCarInfo", CarController.handleChangeCarInfoPost);
        //TODO far sapere agli utenti i prezzi dei servizi
    }
}
