package app.mqtt;

import org.eclipse.paho.client.mqttv3.*;

import java.nio.charset.StandardCharsets;


public class HueMqtt implements MqttCallback {

    // init the client
    private MqttClient mqttClient;
    private Lamps l;
    private static final String TOPIC_ATT = "home/att";
    private static final String TOPIC_ATT_STATE = "home/att-state";
    private static final String TOPIC_LWT = "home/LWT";
    // URL del broker
    //private static final String brokerURL = "tcp://193.206.52.98:1883";
    private static final String brokerURL = "tcp://127.0.0.1:1883";
    /**
     * Costruttore. Genera un id casuale per il client MQTT e lo istanzia.
     */
    public HueMqtt() {
        l = new Lamps();
        try {
            mqttClient = new MqttClient(brokerURL, MqttClient.generateClientId());

        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void connectionLost(Throwable cause) {
        // what happens when the connection is lost. We could reconnect here, for example.
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        // what happens when a new message arrive: in this case, we print it out.
        String strmsg = new String(message.getPayload(), StandardCharsets.UTF_8);
        System.out.println("Message arrived for the topic '" + topic + "': " + strmsg);

        //System.out.println("Message arrived for the topic '" + topic + "': " + message.toString());

        // additional action for the Last Will and Testament message
        if ("home/LWT".equals(topic)) {
            System.err.println("Publisher is gone!");
        } else
        {if ("ON".equals(strmsg)) l.switchOn(1);
        else if ("OFF".equals(strmsg)) l.switchOff(1);
        else System.out.println("Unespected message "+strmsg+"from topic "+topic);}
        // Attualmente riceve solo e visualizza su lampadina
        // si potrebbe aggiungere l'invio su home/state-att della conferma dello stato
        // della lampadina
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        // called when delivery for a message has been completed, and all acknowledgments have been received
        // no-op, here
    }

    public void start() {
        try { String password = "pissir2020";
            char pwd[] = password.toCharArray();
            MqttConnectOptions options = new MqttConnectOptions();
            options.setUserName("pissir");
            options.setPassword(pwd);
            options.setCleanSession(false);
            options.setWill(mqttClient.getTopic("home/LWT"), "HUEmqtt: I'm gone. Bye.".getBytes(), 0, false);

            // set a callback and connect to the broker
            mqttClient.setCallback(this);
            mqttClient.connect(options);

            //Sottoscrive il topic degli attuatori
            final String topic = TOPIC_ATT;
            mqttClient.subscribe(topic);

            System.out.println("The subscriber is now listening to " + topic + "...");
            mqttClient.subscribe(TOPIC_LWT);

            // il seguente ciclo infinito invia a intervalli regolari lo stato dell'attuatore
           /* while (true) {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mqttClient.publish(TOPIC_ATT_STATE,("state value").getBytes(), 1, false);
            }*/
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // la parte iniziale è solo una prova di funzionamento corretto delle luci
        HueMqtt huemqtt = new HueMqtt();
        huemqtt.l.switchOff(1);
        huemqtt.l.switchOff(2);
        huemqtt.l.switchOff(3);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        huemqtt.l.setOnCol(20000, 200);
        huemqtt.l.switchOn(1);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        huemqtt.l.switchOff(1);
        // Fine prova di funzionamento delle luci, inizio ascolto su topic

        huemqtt.start();
    }
}

