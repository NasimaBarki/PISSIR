package it.uniupo.pissir;

import it.uniupo.pissir.monitor.MonitorController;
import it.uniupo.pissir.notification.NotificationController;
import it.uniupo.pissir.parkingLot.ParkingLotController;
import it.uniupo.pissir.car.CarController;
import it.uniupo.pissir.index.IndexController;
import it.uniupo.pissir.login.LoginController;
import it.uniupo.pissir.logout.LogoutController;
import it.uniupo.pissir.payment.ExitPaymentController;
import it.uniupo.pissir.payment.PaymentController;
import it.uniupo.pissir.price.PriceController;
import it.uniupo.pissir.recharge.RechargeController;
import it.uniupo.pissir.reservation.ReservationController;
import it.uniupo.pissir.signup.SignupController;
import spark.Spark;

import static spark.Spark.port;
import static spark.Spark.staticFileLocation;

public class Client {
    public static void main(String[] args) {
        //Cambio la porta di default
        port(5000);
        //Directory da cui prenderà immagini, css e js
        staticFileLocation("/public");

        //Home
        Spark.get("/", IndexController.serveIndexPage);

        //Login
        Spark.get("/login", LoginController.serveLoginPage);
        Spark.post("/login", LoginController.handleLoginPost);

        //Registrazione
        Spark.get("/signup", SignupController.serveSignupPage);
        Spark.post("/signup", SignupController.handleSignupPost);

        //Logout
        Spark.get("/logout", LogoutController.handleLogout);

        //Ricariche
        Spark.get("/recharge", RechargeController.serveRechargePage);
        Spark.get("/rechargeRequest", RechargeController.serveRechargeRequestPage);
        Spark.post("/rechargeRequest", RechargeController.handleRechargeRequestPost);

        //Dati macchina
        Spark.get("/car", CarController.serveCarPage);
        Spark.get("/registerCar", CarController.serveRegisterCarPage);
        Spark.post("/registerCar", CarController.handleRegisterCarPost);

        //Monitoraggio posti
        Spark.get("/monitorParkingLot", ParkingLotController.serveParkingLotPage);

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
        Spark.post("/payment", PaymentController.handlePaymentPost);

        //Pagamento all'uscita
        Spark.get("/exitPayment", ExitPaymentController.serveExitPaymentPage);
        Spark.post("/exitPayment", ExitPaymentController.handleExitPaymentPost);

        //Monitor all'ingresso
        Spark.get("/monitor", MonitorController.serveMonitorPage);

        //Notifiche
        Spark.get("/notification", NotificationController.serveNotificationPage);
    }
}