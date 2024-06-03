package app.mqtt;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

public class Lamps {
    String lightsURL;
    String baseURL;
    String username;
    String color ;

    Lamps() {
        baseURL = "http://localhost:80 ";
        username = "newdeveloper";
        /*String baseURL = "http://172.30.1.138";
          username = "Ugb7HMqMlfrsN7m2gzvWIwzgoNefeFPrUVgt5TmE";*/
         lightsURL = baseURL + "/api/" + username + "/lights/";
    }

    void setOnCol(int hue, int bri) {
        this.color = "{\"on\":true,\"bri\":"+bri+",\"hue\":"+hue+"}";
    }


    void switchOn(int light)
    {   RestTemplate restloc = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        this.setOnCol(15000,254);
        HttpEntity<String> request = new HttpEntity<>(color,headers);
        String callURL = lightsURL + light + "/state";
        restloc.put(callURL, request);
    }

    void switchOff(int light)
    {   RestTemplate restloc = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String off = "{\"on\": false}";
        HttpEntity<String> request = new HttpEntity<>(off,headers);
        String callURL = lightsURL + light + "/state";
        restloc.put(callURL, request);
    }

}
