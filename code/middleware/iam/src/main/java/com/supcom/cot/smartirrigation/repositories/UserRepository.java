package com.supcom.cot.smartirrigation.repositories;

import jakarta.data.repository.CrudRepository;
import com.supcom.cot.smartirrigation.entities.User;
import jakarta.data.repository.Repository;
import java.util.stream.Stream;

@Repository
public interface UserRepository  extends CrudRepository <User, String> { // repository containing the methods for interacting with SensorDB entity in mongodb
    Stream<User> findAll();
    Stream<User> findBypermissionLevel(Long L);
    Stream<User> findByfullnameIn(String s);


}
