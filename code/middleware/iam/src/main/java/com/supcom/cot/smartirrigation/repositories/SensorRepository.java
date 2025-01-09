package com.supcom.cot.smartirrigation.repositories;

import com.supcom.cot.smartirrigation.entities.Sensor;
import jakarta.data.repository.CrudRepository;
import jakarta.data.repository.Repository;

import java.util.stream.Stream;

@Repository
public interface SensorRepository extends CrudRepository<Sensor, String> { // repository containing the methods for interacting with SensorDB entity in mongodb
    Stream<Sensor> findAll();

}
