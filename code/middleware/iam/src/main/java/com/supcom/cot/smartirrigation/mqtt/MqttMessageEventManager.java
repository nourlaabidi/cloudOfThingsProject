//package com.supcom.cot.smartirrigation.mqtt;
//import jakarta.annotation.PostConstruct;
//import jakarta.ejb.Singleton;
//import jakarta.ejb.Startup;
//import org.eclipse.paho.mqttv5.client.*;
//import org.eclipse.paho.mqttv5.client.MqttConnectionOptions;
//import org.eclipse.paho.mqttv5.client.persist.MemoryPersistence;
//import org.eclipse.paho.mqttv5.common.MqttException;
//import org.eclipse.paho.mqttv5.common.MqttMessage;
//import org.eclipse.paho.mqttv5.common.MqttSubscription;
//import org.eclipse.paho.mqttv5.common.packet.MqttProperties;
//import java.nio.charset.StandardCharsets;
//import org.json.JSONObject;
//import com.supcom.cot.smartirrigation.entities.Sensor;
//import com.supcom.cot.smartirrigation.boundaries.PublishWebsocketEndpoint;
//import org.eclipse.microprofile.config.Config;
//import org.eclipse.microprofile.config.ConfigProvider;
//@Startup
//@Singleton // With singleton and startup annotations, you can launch a function on server startup
//public class MqttMessageEventManager {
//    public static int qos           = 1;
//    public static String topic      = "Smartirrigation";
//    public static MemoryPersistence persistence = new MemoryPersistence();
//    public static MqttClient client;
//    private final Config config = ConfigProvider.getConfig(); // Get sensitive data such as MQTT broker password from the system variables
//    private final String mqttusername = config.getValue("mqtt.broker.username",String.class); // Username of the MQTT broker
//    private final String mqttPassword = config.getValue("mqtt.broker.password",String.class); // Password of the mqtt broker
//    private final String broker = config.getValue("mqtt.broker.broker",String.class); // URI of the MQTT broker
//    private final String clientId = config.getValue("mqtt.broker.clientId",String.class); // Cliend ID for the connection
//
//    @PostConstruct // annotate the function to be started on launch with post construct. this function will connect on the broker.
//    public void start() {
//        System.out.println("Connecting to the MQTT broker...: ");
//
//
//        MqttMessageEventManager MqttListener= new MqttMessageEventManager();
//
//        try {
//            MqttListener.connect();
//            MqttListener.listen(topic);
//        }
//        catch ( Exception e ) {
//            e.printStackTrace();
//        }
//    }
//
//    public MqttMessageEventManager() {
//    }
//    public void Hello(){
//        System.out.println("Hello: ");
//    }
//
//
//
//    public void connect() throws MqttException {
//        try {
//            System.out.println("Connecting to MQTT broker: "+broker);
//
//            MqttConnectionOptions connOpts = new MqttConnectionOptions(); // set the connection options
//            connOpts.setUserName(mqttusername);
//            connOpts.setPassword(mqttPassword.getBytes(StandardCharsets.UTF_8));
//            connOpts.setCleanStart(false);
//
//            client = new MqttClient(broker, clientId, persistence);
//            client.connect(connOpts);
//            client.setCallback(new MqttCallback() {
//                @Override
//                public void disconnected(MqttDisconnectResponse mqttDisconnectResponse) {
//
//                }
//
//                @Override
//                public void mqttErrorOccurred(MqttException e) {
//
//                }
//
//                @Override
//                public void messageArrived(String topic, MqttMessage mqttMessage) { // On message receival, construct sensor json object and publish to Websocket
//                    JSONObject object = new JSONObject(new String( mqttMessage.getPayload() ));
//                    String messageTxt=object.getString("id");
//                    Double temprature=object.isNull("temprature") ? null : object.getDouble("temprature");
//                    Double humidity=object.isNull("humidity") ? null : object.getDouble("humidity");
//                    Double moisture=object.isNull("moisture") ? null : object.getDouble("moisture");
//                    Sensor sensor=new Sensor(messageTxt,temprature,humidity,moisture);
//                    System.out.println("Message on " + topic + ": '" + messageTxt + "'");
//                    System.out.println(sensor);
//                    PublishWebsocketEndpoint.broadcastMessage(sensor);
//                    MqttProperties props = mqttMessage.getProperties();
//                    String responseTopic = props.getResponseTopic();
//
//
//                }
//
//                @Override
//                public void deliveryComplete(IMqttToken iMqttToken) {
//
//                }
//
//                @Override
//                public void connectComplete(boolean b, String s) {
//
//                }
//
//                @Override
//                public void authPacketArrived(int i, MqttProperties mqttProperties) {
//
//                }
//            });
//
//
//            System.out.println("Connected");
//        }
//        catch ( MqttException me ) {
//            System.out.println("reason "+me.getReasonCode());
//            System.out.println("msg "+me.getMessage());
//            System.out.println("loc "+me.getLocalizedMessage());
//            System.out.println("cause "+me.getCause());
//            System.out.println("excep "+me);
//            me.printStackTrace();
//            throw me;
//        }
//    }
//
//    public void disconnect() {
//        try {
//            client.disconnect();
//            System.out.println("Disconnected");
//        }
//        catch ( Exception e ) {
//            e.printStackTrace();
//        }
//    }
//
//    public void listen(String topic) throws Exception {
//        try {
//            System.out.println("Subscribing to topic " + topic);
//
//            MqttSubscription sub =
//                    new MqttSubscription(topic, qos);
//
//            IMqttToken token = client.subscribe(
//                    new MqttSubscription[] { sub }
//            );
//        }
//        catch ( Exception e ) {
//            e.printStackTrace();
//        }
//    }
//}