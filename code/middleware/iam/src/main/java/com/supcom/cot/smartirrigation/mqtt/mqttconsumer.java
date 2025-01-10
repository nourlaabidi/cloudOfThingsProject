//package com.supcom.cot.smartirrigation.mqtt;
//
//import com.supcom.cot.smartirrigation.entities.Sensor;
//import com.supcom.cot.smartirrigation.repositories.SensorRepository;
//import jakarta.annotation.PostConstruct;
//import jakarta.annotation.PreDestroy;
//import jakarta.enterprise.context.ApplicationScoped;
//import jakarta.inject.Inject;
//import org.eclipse.paho.mqttv5.client.MqttClient;
//import org.eclipse.paho.mqttv5.client.MqttConnectionOptions;
//
//
//@ApplicationScoped
//public class MqttConsumer {
//
//    private static final String BROKER_URL = "tcp://127.0.0.1:1883";
//    private static final String TOPIC = "garden/sensor/data";
//    private static final String CLIENT_ID = "jakarta-consumer";
//
//    @Inject
//    private SensorRepository repository;
//
//    private MqttClient client;
//
//    @PostConstruct
//    public void init() {
//        try {
//            client = new MqttClient(BROKER_URL, CLIENT_ID);
//            MqttConnectionOptions options = new MqttConnectionOptions();
//            options.setAutomaticReconnect(true);
//            options.setCleanStart(false);
//            client.connect(options);
//
//            client.subscribe(TOPIC, 1); // Où "1" est le niveau de QoS
//            client.setCallback(new MqttCallback() {
//                @Override
//                public void connectionLost(Throwable cause) {
//                    System.err.println("Connection lost: " + cause.getMessage());
//                }
//
//                @Override
//                public void messageArrived(String topic, MqttMessage message) throws Exception {
//                    String payload = new String(message.getPayload());
//                    System.out.println("Received message: " + payload);
//                    saveSensorData(payload); // Appelle ta méthode pour sauvegarder les données
//                }
//
//                @Override
//                public void deliveryComplete(IMqttDeliveryToken token) {
//                    // Cette méthode est utilisée pour la publication de messages
//                }
//            });
//
//
//            System.out.println("MQTT Consumer initialized and subscribed to topic: " + TOPIC);
//
//        } catch (Exception e) {
//            throw new RuntimeException("Failed to initialize MQTT Consumer", e);
//        }
//    }
//
//    private void saveSensorData(String payload) {
//        try {
//            // Convert JSON payload to Sensor object
//            Sensor sensor = parsePayload(payload);
//            repository.save(sensor);
//            System.out.println("Saved sensor data: " + sensor);
//        } catch (Exception e) {
//            System.err.println("Failed to save sensor data: " + e.getMessage());
//        }
//    }
//
//    private Sensor parsePayload(String payload) {
//        // Utiliser une bibliothèque comme Jackson ou JSON-B pour convertir
//        jakarta.json.bind.Jsonb jsonb = jakarta.json.bind.JsonbBuilder.create();
//        return jsonb.fromJson(payload, Sensor.class);
//    }
//
//    @PreDestroy
//    public void cleanup() {
//        try {
//            if (client != null && client.isConnected()) {
//                client.disconnect();
//                client.close();
//            }
//        } catch (Exception e) {
//            System.err.println("Failed to clean up MQTT Consumer: " + e.getMessage());
//        }
//    }
//}
