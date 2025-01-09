package com.supcom.cot.smartirrigation.entities;
import jakarta.nosql.Column;
import jakarta.nosql.Entity;
import jakarta.nosql.Id;
import jakarta.json.bind.annotation.JsonbVisibility;
import java.io.Serializable;
import java.util.Objects;

@Entity
@JsonbVisibility(FieldPropertyVisibilityStrategy.class)
public class Sensor implements Serializable  { // Sensor entity for MQTT messages. This entity will be used to broadcast sensor data on websocket
    @Id
    private String id; //Sensor ID

    @Column
    private Double temperature; // from DHT sensor

    @Column
    private Double humidity; // from DHT sensor

    @Column
    private Double moisture; // from Moisture sensor

    public Sensor() {
    }

    public Sensor(String id, Double temperature, Double humidity , Double moisture) {
        this.id= id;
        this.temperature = temperature;
        this.humidity=humidity;
        this.moisture=moisture;

    }


    //Getters
    public String getId() {
        return id;
    }
    public Double getTemperature() {
        return temperature;
    }

    public Double getHumidity() {
        return humidity;
    }

    public Double getMoisture() {
        return moisture;
    }


    public void setId(String id) {
        this.id = id;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    public void setHumidity(Double humidity) {
        this.humidity = humidity;
    }

    public void setMoisture(Double moisture) {
        this.moisture = moisture;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Sensor)) {
            return false;
        }
        Sensor sensor = (Sensor) o;
        return Objects.equals(id, sensor.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Sensor{" +
                "id='" + id + '\'' +
                ", temperature=" + temperature +
                ", humidity=" + humidity +
                ", moisture=" + moisture +

                '}';
    }

}

