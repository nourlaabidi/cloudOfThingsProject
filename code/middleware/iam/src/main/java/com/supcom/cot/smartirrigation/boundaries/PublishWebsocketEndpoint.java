//package com.supcom.cot.smartirrigation.boundaries;
//import jakarta.ejb.EJB;
//import jakarta.websocket.*;
//import jakarta.websocket.server.ServerEndpoint;
//import java.io.IOException;
//import java.util.*;
//import com.supcom.cot.smartirrigation.entities.Sensor;
//import com.supcom.cot.smartirrigation.mqtt.MqttMessageEventManager;
//
//@ServerEndpoint(value = "/pushes", encoders = {SensorJSONEncoder.class}, decoders = {SensorJSONDecoder.class}) //Annotates path for websocket and with the json encoders and decoders
//public class PublishWebsocketEndpoint {
//    @EJB
//    private MqttMessageEventManager mqttlistener;
//    private static Hashtable<String, Session> sessions = new Hashtable<>(); // initialize sessions as empty Hashtable
//    public static void broadcastMessage(Sensor sensor) {
//        for (Session session : sessions.values()) {
//            try {
//                session.getBasicRemote().sendObject(sensor); // broadcast the message to websocket
//                System.out.println("Got it"); // for debugging
//            } catch (IOException | EncodeException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//    @OnOpen
//    public void onOpen(Session session){
//        this.mqttlistener = new MqttMessageEventManager();
//        mqttlistener.Hello(); // print Hello on session start, for debugging
//        mqttlistener.start();
//        sessions.put(session.getId(), session); //add the new session
//
//    }
//    @OnClose
//    public void onClose(Session session, CloseReason reason){
//        sessions.remove(session); // delete sessions when client leave
//    }
//    @OnError
//    public void onError(Session session, Throwable error){
//        System.out.println("Push WebSocket error for ID " + session.getId() + ": " + error.getMessage());
//    }
//
//
//}
