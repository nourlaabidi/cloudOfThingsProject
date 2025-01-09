//package com.supcom.cot.smartirrigation.boundaries;
//
//
//import jakarta.json.Json;
//import jakarta.json.JsonObject;
//import java.io.StringReader;
//import jakarta.websocket.DecodeException;
//import jakarta.websocket.Decoder;
//import com.supcom.cot.smartirrigation.entities.Sensor;
//
//public class SensorJSONDecoder implements Decoder.Text<Sensor>{
//
//    @Override
//    public Sensor decode(String jsonMessage) throws DecodeException {
//
//        JsonObject jsonObject = Json
//                .createReader(new StringReader(jsonMessage)).readObject();
//        String id=jsonObject.getString("id");
//        Double temperature=jsonObject.isNull("temperature") ? null : jsonObject.getJsonNumber("temperature").doubleValue();
//        Double humidity=jsonObject.isNull("humidity") ? null : jsonObject.getJsonNumber("humidity").doubleValue();
//        Double moisture=jsonObject.isNull("moisture") ? null : jsonObject.getJsonNumber("moisture").doubleValue();
//        Sensor sensor = new Sensor(id,temperature,humidity,moisture);
//        return sensor;
//
//    }
//
//    @Override
//    public boolean willDecode(String jsonMessage) {
//        try {
//            // Check if incoming message is valid JSON
//            Json.createReader(new StringReader(jsonMessage)).readObject();
//            return true;
//        } catch (Exception e) {
//            return false;
//        }
//    }
//
//}
