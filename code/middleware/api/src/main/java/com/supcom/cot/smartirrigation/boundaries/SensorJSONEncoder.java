//package com.supcom.cot.smartirrigation.boundaries;
//
//import jakarta.json.Json;
//import jakarta.json.JsonObject;
//import jakarta.websocket.EncodeException;
//import jakarta.websocket.Encoder;
//import com.supcom.cot.smartirrigation.entities.Sensor;
//
//public class SensorJSONEncoder implements Encoder.Text<Sensor> {
//    @Override
//    public String encode(Sensor sensor) throws EncodeException {
//
//        JsonObject jsonObject = Json.createObjectBuilder()
//                .add("id", sensor.getId())
//                .add("temperature", sensor.getTemperature())
//                .add("humidity", sensor.getHumidity())
//                .add("moisture", sensor.getMoisture())
//                .build();
//        return jsonObject.toString();
//
//    }
//
//
//}
