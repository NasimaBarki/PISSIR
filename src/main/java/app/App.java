package app;

import app.index.IndexController;
import app.keycloak.KeycloakController;
import spark.Spark;

import static spark.Spark.*;
import org.keycloak.admin.client.Keycloak;

public class App {
    public static void main(String[] args) {
        port(8000);
        staticFileLocation("/public");

        before((req, res) -> {
            if(req.attribute("code") != null){
                System.out.println((char[]) req.attribute("code"));
            }
        });

        //Index
        Spark.get("/", IndexController.serveIndexPage);

        //Login e registrazione tramite keycloak
        Spark.post("/login", KeycloakController.serveKeycloak);
    }
}
