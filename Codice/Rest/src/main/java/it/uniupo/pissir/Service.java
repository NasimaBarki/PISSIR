package it.uniupo.pissir;

import com.google.gson.Gson;
import it.uniupo.pissir.car.Car;
import it.uniupo.pissir.car.CarDao;
import it.uniupo.pissir.notification.Notification;
import it.uniupo.pissir.notification.NotificationDao;
import it.uniupo.pissir.parkingSpot.ParkingSpot;
import it.uniupo.pissir.parkingSpot.ParkingSpotDao;
import it.uniupo.pissir.payment.Payment;
import it.uniupo.pissir.payment.PaymentDao;
import it.uniupo.pissir.price.Price;
import it.uniupo.pissir.price.PriceDao;
import it.uniupo.pissir.recharge.Recharge;
import it.uniupo.pissir.recharge.RechargeDao;
import it.uniupo.pissir.reservation.Reservation;
import it.uniupo.pissir.reservation.ReservationDao;
import it.uniupo.pissir.user.User;
import it.uniupo.pissir.user.UserDao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static spark.Spark.*;

public class Service {
    public static void main(String[] args) {
        Gson gson = new Gson();
        String baseURL = "/api/v1.0";

        //Abilita CORS
        before((request, response) -> {
            response.header("Access-Control-Allow-Origin", "*");
            response.header("Access-Control-Allow-Methods", "GET, POST");
            response.header("Access-Control-Allow-Headers", "Content-Type");
        });

        //Ottieni un singolo utente
        get(baseURL + "/users", "application/json", (req, res) -> {
            System.out.println("GET USER");

            String username = req.queryParams("username");

            User user = UserDao.getUser(username);

            if (user == null) {
                halt(401);
            }

            String password = req.queryParams("password");

            if(password != null){
                if (!UserDao.verifyPassword(username, password)) {
                    halt(401);
                }
            }

            Map<String, User> finalJson = new HashMap<>();
            finalJson.put("user", user);
            res.status(200); //OK
            res.type("application/json");

            return finalJson;
        }, gson::toJson);

        //Aggiungi un nuovo utente
        post(baseURL + "/users", "application/json", (req, res) -> {
            System.out.println("POST USER");
            Map addRequest = gson.fromJson(req.body(), Map.class);
            User user;
            Map<String, User> finalJson = new HashMap<>();

            if(addRequest!=null && addRequest.containsKey("name") && addRequest.containsKey("surname") && addRequest.containsKey("email") && addRequest.containsKey("username") && addRequest.containsKey("password") && addRequest.containsKey("type")) {
                String name = String.valueOf(addRequest.get("name"));
                String surname = String.valueOf(addRequest.get("surname"));
                String email = String.valueOf(addRequest.get("email"));
                String username = String.valueOf(addRequest.get("username"));
                String password = String.valueOf(addRequest.get("password"));
                int type = Integer.parseInt(String.valueOf(addRequest.get("type")));

                //Aggiungi il nuovo utente al DB
                user = UserDao.addUser(new User(name, surname, email, username, password, type));

                //Se lo user non c'è ritorna 404
                if(user == null)
                    halt(404);

                finalJson.put("task", user);
                res.type("application/json");
                res.status(201); //Nuova risorsa aggiunta
            }
            else {
                halt(403);
            }

            return finalJson;
        },gson::toJson);

        //Ottieni tutte le ricariche
        get(baseURL + "/recharges", "application/json", (req, res) -> {
            System.out.println("GET RECHARGE");

            String username = req.queryParams("username");
            String completed = req.queryParams("completed");

            res.type("application/json");
            res.status(200);

            //Ottieni tutte le ricariche
            if (username == null && completed == null) {
                // get all tasks from the DB
                List<Recharge> allRecharges = RechargeDao.getAllRecharges();
                // prepare the JSON-related structure to return
                Map<String, List<Recharge>> finalJson = new HashMap<>();
                finalJson.put("recharges", allRecharges);

                return finalJson;
            }
            //Ottieni le ricariche di un utente in particolare
            else if (username != null && completed == null){
                Recharge recharge = RechargeDao.getRecharge(username);

                if (recharge == null) {
                    halt(404);
                }

                Map<String, Recharge> finalJson = new HashMap<>();
                finalJson.put("recharge", recharge);
                res.status(200); //OK
                res.type("application/json");

                return finalJson;
            }
            //Ottieni le ricariche completate di un utente
            else if (username != null && completed.equals("yes")) {
                Recharge recharge = RechargeDao.getNotCompletedRecharge(username);

                if (recharge == null) {
                    halt(404);
                }

                Map<String, Recharge> finalJson = new HashMap<>();
                finalJson.put("recharge", recharge);
                res.status(200); //OK
                res.type("application/json");

                return finalJson;
            } else if (completed.equals("no")) {
                // get all not completed tasks from the DB
                int numRecharges = RechargeDao.getAllNotCompletedRecharges();
                // prepare the JSON-related structure to return
                Map<String, String> finalJson = new HashMap<>();
                finalJson.put("rechargeQueue", String.valueOf(numRecharges));

                return finalJson;
            }
            return null;
        }, gson::toJson);

        //Modifica la richiesta di ricarica
        put(baseURL + "/recharges", "application/json", (req, res) -> {
            System.out.println("PUT RECHARGE");

            String username = req.queryParams("username");

            res.type("application/json");
            res.status(200);

            Recharge modifiedRecharge = gson.fromJson(req.body(), Recharge.class);

            if (username != null && modifiedRecharge != null) {
                RechargeDao.changeRecharge(modifiedRecharge);
            } else halt(404);

            return modifiedRecharge;
        });

        //Aggiungi una nuova richiesta di ricarica
        post(baseURL + "/recharges", "application/json", (req, res) -> {
            System.out.println("POST RECHARGE");

            Map addRequest = gson.fromJson(req.body(), Map.class);
            Recharge recharge;
            Map<String, Recharge> finalJson = new HashMap<>();

            if (addRequest!=null && addRequest.containsKey("username") && addRequest.containsKey("percentage") && addRequest.containsKey("notification") && addRequest.containsKey("completed")) {
                String username = String.valueOf(addRequest.get("username"));
                int percentage = Integer.parseInt(String.valueOf(addRequest.get("percentage")));
                int notification = Integer.parseInt(String.valueOf(addRequest.get("notification")));
                int completed = Integer.parseInt(String.valueOf(addRequest.get("completed")));

                recharge = RechargeDao.addRecharge(new Recharge(username, percentage, notification, completed));

                if(recharge == null) {
                    halt(404);
                }

                finalJson.put("recharge", recharge);
                res.type("application/json");
                res.status(201);
            } else {
                halt(403);
            }

            return finalJson;
        });

        //Ottieni info auto
        get(baseURL + "/cars/:licensePlate", "application/json", (req, res) -> {
            System.out.println("GET CAR");
            Car car = CarDao.getCarByLicensePlate(req.params(":licensePlate"));

            if(car==null)
                halt(404);

            // prepare the JSON-related structure to return
            // and the suitable HTTP response code and type
            Map<String, Car> finalJson = new HashMap<>();
            finalJson.put("car", car);
            res.status(200);
            res.type("application/json");

            return finalJson;
        },gson::toJson);

        //Ottieni l'auto di un certo utente
        get(baseURL + "/users/:username/car", "application/json", (req, res) -> {
            System.out.println("GET CAR OF USER");
            Car car = CarDao.getCar(req.params(":username"));

            if(car==null)
                halt(404);

            // prepare the JSON-related structure to return
            // and the suitable HTTP response code and type
            Map<String, Car> finalJson = new HashMap<>();
            finalJson.put("car", car);
            res.status(200);
            res.type("application/json");

            return finalJson;
        },gson::toJson);

        //Aggiungi una nuova macchina al DB
        post(baseURL + "/cars", "application/json", (req, res) -> {
            System.out.println("POST CAR");
            Map addRequest = gson.fromJson(req.body(), Map.class);
            Car car;
            Map<String, Car> finalJson = new HashMap<>();

            if(addRequest != null && addRequest.containsKey("licensePlate") && addRequest.containsKey("username") && addRequest.containsKey("model") && addRequest.containsKey("capacity")){
                String licensePlate = String.valueOf(addRequest.get("licensePlate"));
                String username = String.valueOf(addRequest.get("username"));
                String model = String.valueOf(addRequest.get("model"));
                int capacity = Integer.parseInt(String.valueOf(addRequest.get("capacity")));

                car = CarDao.addCar(new Car(licensePlate, username, model, capacity));

                if (car == null) {
                    halt(404);
                }

                finalJson.put("car", car);
                res.type("application/json");
                res.status(201);
            }
            else {
                halt(403);
            }

            return finalJson;
        },gson::toJson);

        //Ottieni tutti i posti auto
        get(baseURL + "/parkingSpots", "application/json", (req, res) -> {
            res.type("application/json");
            res.status(200);

            String arrival = req.queryParams("arrival");
            String reservationEnd = req.queryParams("reservationEnd");
            String premium = req.queryParams("premium");
            String available = req.queryParams("available");

            if (arrival != null && reservationEnd != null) {
                System.out.println("GET PREMIUM AVAILABLE PARKING SPOTS");
                List<ParkingSpot> allPremiumParkingSpots = ParkingSpotDao.getPremiumAvailableParkingSpot(arrival, reservationEnd);

                if (allPremiumParkingSpots == null) {
                    halt(404);
                }

                Map<String, List<ParkingSpot>> finalJson = new HashMap<>();
                finalJson.put("parkingSpot", allPremiumParkingSpots);

                res.status(200);
                res.type("application/json");

                return gson.toJson(finalJson);
            } else if (premium != null && premium.equals("yes")) {
                System.out.println("GET PREMIUM AVAILABLE SPOTS");

                List<ParkingSpot> allPremiumAvailableNowParkingSpots = ParkingSpotDao.getPremiumAvailableNowParkingSpot();

                Map<String, String> finalJson = new HashMap<>();
                finalJson.put("emptyPremiumParkingSpots", String.valueOf(allPremiumAvailableNowParkingSpots.size()));
                return gson.toJson(finalJson);
            } else if (available != null && available.equals("yes")) {
                System.out.println("GET AVAILABLE SPOTS");

                List<ParkingSpot> allAvailableSpots = ParkingSpotDao.getOccupiedParkingSpots();

                Map<String, String> finalJson = new HashMap<>();
                finalJson.put("cars", String.valueOf(allAvailableSpots.size()));
                return gson.toJson(finalJson);
            } else {
                System.out.println("GET ALL PARKING SPOTS");

                String limit = req.queryParams("limit");
                String offset = req.queryParams("offset");

                if (limit != null && offset != null) {
                    List<ParkingSpot> allParkingSpots = ParkingSpotDao.getAllParkingSpots();
                    List<ParkingSpot> subParkingSpots = null;
                    try {
                        subParkingSpots = allParkingSpots.subList(Integer.parseInt(offset), Math.min(allParkingSpots.size(), Integer.parseInt(offset) + Integer.parseInt(limit)));
                    } catch (IllegalArgumentException e) {
                        halt(400);
                    }
                    Map<String, List<ParkingSpot>> finalJson = new HashMap<>();
                    finalJson.put("parkingSpots", subParkingSpots);

                    return gson.toJson(finalJson);
                } else {
                    List<ParkingSpot> allParkingSpots = ParkingSpotDao.getAllParkingSpots();
                    Map<String, List<ParkingSpot>> finalJson = new HashMap<>();
                    finalJson.put("parkingSpots", allParkingSpots);

                    res.status(200);
                    res.type("application/json");

                    return gson.toJson(finalJson);
                }
            }
        });

        //Modifica stato posto auto
        put(baseURL + "/parkingSpots", "application/json", (req, res) -> {
            System.out.println("PUT PARKING SPOT");

            String id = req.queryParams("id");
            String value = req.queryParams("value");

            res.type("application/json");
            res.status(200);

            if (id != null && value != null) {
                ParkingSpotDao.changeState(id, value);
            } else halt(404);

            return null;
        });

        //Ottieni tutti i prezzi
        get(baseURL + "/prices", "application/json", (req, res) -> {
            System.out.println("GET ALL PRICES");

            String service = req.queryParams("service");

            if(service == null) {
                res.type("application/json");
                res.status(200);
                // get all tasks from the DB
                List<Price> allPrices = PriceDao.getAllPrices();
                // prepare the JSON-related structure to return
                Map<String, List<Price>> finalJson = new HashMap<>();
                finalJson.put("prices", allPrices);

                return gson.toJson(finalJson);
            } else {
                Price price = PriceDao.getPrice(service);

                if (price == null) {
                    halt(404);
                }

                Map<String, Price> finalJson = new HashMap<>();
                finalJson.put("price", price);
                res.status(200);
                res.type("application/json");

                return gson.toJson(finalJson);
            }
        });

        //Modifica prezzo
        put(baseURL + "/prices", "application/json", (req, res) -> {
            System.out.println("PUT PRICE");

            String service = req.queryParams("service");
            Float price = Float.valueOf(req.queryParams("price"));

            res.type("application/json");
            res.status(200);

            PriceDao.changePrice(service, price);

            return price;
        });

        //Ottieni tutte le prenotazioni
        get(baseURL + "/reservations", "application/json", (req, res) -> {
            System.out.println("GET RESERVATION");

            String username = req.queryParams("username");

            res.type("application/json");
            res.status(200);

            //Ottieni tutte le ricariche
            if (username == null) {
                List<Reservation> allReservations = ReservationDao.getAllReservations();
                Map<String, List<Reservation>> finalJson = new HashMap<>();
                finalJson.put("reservations", allReservations);

                return finalJson;
            }
            //Ottieni le ricariche di un utente in particolare
            else {
                Reservation reservation = ReservationDao.getReservation(username);

                if (reservation == null) {
                    halt(404);
                }

                Map<String, Reservation> finalJson = new HashMap<>();
                finalJson.put("reservation", reservation);
                res.status(200); //OK
                res.type("application/json");

                return finalJson;
            }
        }, gson::toJson);

        //Aggiungi prenotazione
        post(baseURL + "/reservations", "application/json", (req, res) -> {
            System.out.println("POST RESERVATION");

            Map addRequest = gson.fromJson(req.body(), Map.class);
            Reservation reservation;
            Map<String, Reservation> finalJson = new HashMap<>();

            if (addRequest!=null && addRequest.containsKey("username") && addRequest.containsKey("arrival") && addRequest.containsKey("reservationEnd") && addRequest.containsKey("parkingSpotNumber") && addRequest.containsKey("late")) {
                String username = String.valueOf(addRequest.get("username"));
                String arrival = String.valueOf(addRequest.get("arrival"));
                String reservationEnd = String.valueOf(addRequest.get("reservationEnd"));
                int parkingSpotNumber = (int) Double.parseDouble(String.valueOf(addRequest.get("parkingSpotNumber")));
                int late = Integer.parseInt(String.valueOf(addRequest.get("late")));

                reservation = ReservationDao.addReservation(new Reservation(username, arrival, reservationEnd, parkingSpotNumber, late));

                if(reservation == null) {
                    halt(404);
                }

                finalJson.put("reservation", reservation);
                res.type("application/json");
                res.status(201);
            } else {
                halt(403);
            }

            return finalJson;
        });

        //Ottieni tutti i pagamenti
        get(baseURL + "/payments", "application/json", (req, res) -> {
            System.out.println("GET PAYMENTS");

            res.type("application/json");
            res.status(200);

            String limit = req.queryParams("limit");
            String offset = req.queryParams("offset");

            String start = req.queryParams("start");
            String service = req.queryParams("service");
            String userType = req.queryParams("userType");

            List<Payment> allPayments = PaymentDao.getAllPayments(start, service, userType);
            if (limit != null && offset != null) {
                List<Payment> subPayments = null;
                try {
                    subPayments = allPayments.subList(Integer.parseInt(offset), Math.min(allPayments.size(), Integer.parseInt(offset) + Integer.parseInt(limit)));
                } catch (IllegalArgumentException e) {
                    halt(400);
                }

                Map<String, List<Payment>> finalJson = new HashMap<>();
                finalJson.put("payments", subPayments);

                return gson.toJson(finalJson);
            } else {
                Map<String, List<Payment>> finalJson = new HashMap<>();
                finalJson.put("payments", allPayments);

                return gson.toJson(finalJson);
            }
        });

        //Aggiungi pagamento
        post(baseURL + "/payments", "application/json", (req, res) -> {
            System.out.println("POST PAYMENT");

            Map addRequest = gson.fromJson(req.body(), Map.class);
            Payment payment;
            Map<String, Payment> finalJson = new HashMap<>();

            if (addRequest != null && addRequest.containsKey("username") && addRequest.containsKey("date") && addRequest.containsKey("time") && addRequest.containsKey("type") && addRequest.containsKey("price") && addRequest.containsKey("userType")) {
                String username = String.valueOf(addRequest.get("username"));
                String date = String.valueOf(addRequest.get("date"));
                String time = String.valueOf(addRequest.get("time"));
                String type = String.valueOf(addRequest.get("type"));
                float price = Float.parseFloat(String.valueOf(addRequest.get("price")));
                String userType = String.valueOf(addRequest.get("userType"));

                payment = PaymentDao.addPayment(new Payment(username, date, time, type, price, userType));

                if (payment == null) {
                    halt(404);
                }

                finalJson.put("payment", payment);
                res.type("application/json");
                res.status(201);
            }
            else {
                halt(403);
            }

            return finalJson;
        });

        //Elimina richieste di ricarica
        delete(baseURL + "/users/:username/recharges", "application/json", (req, res) -> {
            System.out.println("DELETE RECHARGE");
            int exitval = RechargeDao.deleteRecharges(req.params(":username"));

            if(exitval ==0 )
                halt(404);

            res.status(200);
            res.type("application/json");

            return "";
        });

        //Elimina prenotazioni
        delete(baseURL + "/users/:username/reservations", "application/json", (req, res) -> {
            System.out.println("DELETE RESERVATION");
            int exitval = ReservationDao.deleteReservations(req.params(":username"));

            if(exitval ==0 )
                halt(404);

            res.status(200);
            res.type("application/json");

            return "";
        });

        //Aggiungi notifica
        post(baseURL + "/notifications", "application/json", (request, response) -> {
            // get the body of the HTTP request
            Notification notification = gson.fromJson(request.body(), Notification.class);
            Map<String, Notification> finalJson = new HashMap<>();

            // check whether everything is in place
            if(notification!=null) {

                notification = NotificationDao.addNotification(notification);

                if(notification==null)
                    halt(404);

                finalJson.put("notification", notification);
                response.type("application/json");

                response.status(201);
            }
            else {
                halt(403);
            }

            return finalJson;
        },gson::toJson);

        //Ottieni notifiche
        get(baseURL + "/notifications", "application/json", (req, res) -> {
            System.out.println("GET NOTIFICATIONS");

            String username = req.queryParams("username");
            String offset = req.queryParams("offset");
            String limit = req.queryParams("limit");

            if (username == null) {
                halt(401);
            }

            if(offset != null & limit != null) {
                List<Notification> allNotifications = NotificationDao.getNotifications(username);
                List<Notification> subNotifications = null;

                if (allNotifications.isEmpty()) {
                    halt(404);
                }
                try {
                    subNotifications = allNotifications.subList(Integer.parseInt(offset), Math.min(allNotifications.size(), Integer.parseInt(offset) + Integer.parseInt(limit)));
                } catch (IllegalArgumentException e) {
                    halt(400);
                }
                Map<String, List<Notification>> finalJson = new HashMap<>();
                finalJson.put("notifications", subNotifications);
                res.status(200); //OK
                res.type("application/json");
                return finalJson;
            }

            return halt(404);

        }, gson::toJson);
    }
}