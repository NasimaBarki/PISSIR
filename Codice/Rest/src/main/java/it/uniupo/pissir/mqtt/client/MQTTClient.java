package it.uniupo.pissir.mqtt.client;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class MQTTClient {
    //Init client
    MqttClient client;

    //Costruttore
    public MQTTClient() {
        try {
            client = new MqttClient(
                    "tcp://broker.emqx.io:1883", //URI
                    MqttClient.generateClientId(), //ClientId
                    new MemoryPersistence()); //Persistence
        } catch (MqttException e) {
            throw new RuntimeException(e);
        }
    }

    //Start del client
    public void start() {
        try {
            //Connessione al broker
            client.setCallback(new SubscribeCallback());
            client.connect();

            //Iscrizione ai topic
            String topic = "parkingLot/sensor";
            client.subscribe(topic, 0);
            System.out.println("Il client è ora iscritto al topic " + topic);

            topic = "parkingLot/MWbot";
            client.subscribe(topic, 0);
            System.out.println("Il client è ora iscritto al topic " + topic);

        } catch (MqttException e) {
            throw new RuntimeException(e);
        }
    }

    //Main
    public static void main(String[] args) {
        MQTTClient client = new MQTTClient();
        client.start();
    }
}
