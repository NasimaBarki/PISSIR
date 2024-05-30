package app.keycloak;

import spark.Request;
import spark.Response;
import spark.Route;

public class KeycloakController {
    //Visualizza la pagina di login
    public static Route serveKeycloak = (Request req, Response res) -> {

        //Generazione dell'url per visualizzare il form
        String redirectUri = "http://localhost:8000/";
        String clientId = "parcheggioClient";
        String clientSecret = "b59a8511-fca2-4ee7-bc56-8bd081cfc8e5";
        String scope = "openid";

        String authUrl = "http://localhost:8080/auth/realms/parcheggioRealm/protocol/openid-connect/auth"
                + "?response_type=code"
                + "&response_mode=fragment"
                + "&client_id=" + clientId
                + "&client_secret" + clientSecret
                + "&redirect_uri=" + redirectUri
                + "&scope=" + scope;

        res.redirect(authUrl);

        return null;
    };
}
