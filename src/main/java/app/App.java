package app;

import app.index.IndexController;
import app.login.LoginController;
import app.signup.SignupController;
import spark.Spark;

import static spark.Spark.*;

public class App {
    public static void main(String[] args) {

        staticFileLocation("/public");

        //Index
        Spark.get("/", IndexController.serveIndexPage);

        //Login
        Spark.get("/login", LoginController.serveLoginPage);
        Spark.post("/login", LoginController.handleLoginPost);

        //Registrazione
        Spark.get("/signup", SignupController.serveSignupPage);
        Spark.post("/signup", SignupController.handleSignupPost);
    }
}
