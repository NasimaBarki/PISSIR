package app.car;

import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.template.handlebars.HandlebarsTemplateEngine;
import java.util.HashMap;
import java.util.Map;

import static spark.Spark.halt;

public class CarController {
    public static Route serveCarPage = (Request req, Response res) -> {
        //TODO decidere se permettere all'admin di vedere questa pagina
        String username = req.session().attribute("authenticated");
        String premium = req.session().attribute("premium");
        String admin = req.session().attribute("admin");
        Map<Object, Object> model = new HashMap<>();

        if(username != null){
            model.put("authenticated", username);
        } else halt(401, "Devi accedere per visualizzare questa pagina.");

        if (premium != null){
            model.put("premium", premium);
        }
        if (admin != null){
            model.put("admin", admin);
        }

        Car car = CarDao.getCar(username);

        if(car != null){
            model.put("licensePlate", car.getLicensePlate());
            model.put("model", car.getModel());
            model.put("capacity", car.getCapacity());
        }

        return new HandlebarsTemplateEngine().render(
                new ModelAndView(model, "layouts/car.hbs")
        );
    };
    public static Route serveRegisterCarPage = (Request req, Response res) -> {
        //TODO decidere se permettere all'admin di vedere questa pagina
        String username = req.session().attribute("authenticated");
        String premium = req.session().attribute("premium");
        String admin = req.session().attribute("admin");
        Map<Object, Object> model = new HashMap<>();

        if(username != null){
            model.put("authenticated", username);
        } else halt(401, "Devi accedere per visualizzare questa pagina.");

        if (premium != null){
            model.put("premium", premium);
        }
        if (admin != null){
            model.put("admin", admin);
        }

        return new HandlebarsTemplateEngine().render(
                new ModelAndView(model, "layouts/registerCar.hbs")
        );
    };

    public static Route handleRegisterCarPost = (Request req, Response res) -> {
        String username = req.session().attribute("authenticated");
        String licensePlate = req.queryParams("licensePlate");
        String model = req.queryParams("model");
        String capacity = req.queryParams("capacity");

        Car car = new Car(username, licensePlate, model, Integer.parseInt(capacity));

        //TODO controllo dati della macchina
        int error = CarDao.addCar(car);

        res.redirect("/car");

        return null;
    };

    public static Route handleChangeCarInfoPost = (Request req, Response res) -> {
        String username = req.session().attribute("authenticated");
        Car car = new Car(username, req.queryParams("licensePlate"), req.queryParams("model"), Integer.parseInt(req.queryParams("capacity")));

        int error = CarDao.changeCar(car);

        if(error == 0){
            //TODO avvisare sulla pagina web
            System.out.println("Impossibile cambiare i dati della auto");
        }

        res.redirect("/car");

        return null;
    };

    public static Route serveChangeCarInfoPage = (Request req, Response res) -> {
        //TODO decidere se permettere all'admin di vedere questa pagina
        String username = req.session().attribute("authenticated");
        String premium = req.session().attribute("premium");
        String admin = req.session().attribute("admin");
        Map<Object, Object> model = new HashMap<>();

        if(username != null){
            model.put("authenticated", username);
        } else halt(401, "Devi accedere per visualizzare questa pagina.");

        if (premium != null){
            model.put("premium", premium);
        }
        if (admin != null){
            model.put("admin", admin);
        }

        Car car = CarDao.getCar(username);

        if(car != null){
            model.put("licensePlate", car.getLicensePlate());
            model.put("model", car.getModel());
            model.put("capacity", car.getCapacity());
        } else halt(401, "Non puoi accedere a questa macchina perché al tuo account non è associata nessuna auto.");

        return new HandlebarsTemplateEngine().render(
                new ModelAndView(model, "layouts/changeCarInfo.hbs")
        );
    };;
}
