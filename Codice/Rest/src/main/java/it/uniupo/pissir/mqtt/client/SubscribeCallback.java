package it.uniupo.pissir.mqtt.client;

import com.google.gson.Gson;
import it.uniupo.pissir.mqtt.Message;
import it.uniupo.pissir.notification.Notification;
import it.uniupo.pissir.recharge.Recharge;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.concurrent.TimeUnit;

public class SubscribeCallback implements MqttCallback {

    public void connectionLost(Throwable cause) {
        // what happens when the connection is lost. We could reconnect here, for example.
    }

    public void messageArrived(String topic, MqttMessage message) throws Exception {
        Gson gson = new Gson();

        Message mqttMessage = gson.fromJson(message.toString(), Message.class);

        communicateWithRestAPI(topic, mqttMessage.getMsg());

        // what happens when a new message arrive: in this case, we print it out.
        System.out.println("Messaggio dal topic '" + topic + "': " + mqttMessage.getMsg());

        // additional action for the Last Will and Testament message
        if ("home/LWT".equals(topic)) {
            System.err.println("Il publisher non c'è più.");
        }
    }

    private void communicateWithRestAPI(String topic, String msg) {
        RestTemplate rest = new RestTemplate();

        try {
            switch (topic) {
                case "parkingLot/sensor" -> {
                    String[] filters = msg.split(" ");
                    String id = filters[3];
                    String availability;

                    System.out.println(id);
                    if (filters[1].equals("left")) {
                        availability = "0";
                    } else if (filters[1].equals("arrived")) {
                        availability = "1";
                    } else break;
                    System.out.println(availability);
                    rest.exchange("http://localhost:4567/api/v1.0/parkingSpots?id=" + id + "&value=" + availability, HttpMethod.PUT, HttpEntity.EMPTY, Void.class);
                }
                case "parkingLot/MWbot" -> {
                    Gson gson = new Gson();

                    //Ottengo la richiesta di ricarica dell'utente
                    String json = rest.getForObject("http://localhost:4567/api/v1.0/recharges?username=" + msg, String.class);


                    //Trasformo il json nell'oggetto Recharge
                    assert json != null;
                    String[] ao = json.split("\\{");
                    json = "{" + ao[2].substring(0, ao[2].length() - 1);
                    Recharge rechargeRequest = gson.fromJson(json, Recharge.class);
                    HttpEntity<Recharge> request;
                    if (rechargeRequest != null) {
                        request = new HttpEntity<>(rechargeRequest);
                    } else break;
                    rechargeRequest.setCompleted(1);

                    //Delay
                    //TimeUnit.SECONDS.sleep(rechargeRequest.getPercentage() / 10);

                    //Modifico nel DB il campo "completed"
                    rest.exchange("http://localhost:4567/api/v1.0/recharges?username=" + msg, HttpMethod.PUT, request, Void.class);


                    //Pubblico nel topic che la ricarica è stata completata
                    //TODO se provo ad evocare un publisher in questa porzione di codice creo deadlock
                    String messageToPublish = rechargeRequest.getUsername() + "'s recharge request has been completed.";
                    System.out.println(messageToPublish);

                    //Se l'utente ha richiesto una notifica la aggiungo al database
                    if (rechargeRequest.getNotification() == 1) {
                        String date = LocalDate.now() + " " + LocalTime.now().getHour() + ":" + LocalTime.now().getMinute();
                        Notification notification = new Notification(rechargeRequest.getUsername(), "La ricarica della tua auto e' stata completata.", date);
                        HttpEntity<Notification> requestNotification = new HttpEntity<>(notification);
                        rest.postForObject("http://localhost:4567/api/v1.0/notifications", requestNotification, Notification.class);
                    }
                }
                default -> System.out.println("Topic non esistente.");
            }
        } catch (HttpClientErrorException e) {
            System.out.println("TOPIC: " + topic + "\nREST API: 404");
        }
    }

    public void deliveryComplete(IMqttDeliveryToken token) {
        // called when delivery for a message has been completed, and all acknowledgments have been received
        // no-op, here
    }

}
