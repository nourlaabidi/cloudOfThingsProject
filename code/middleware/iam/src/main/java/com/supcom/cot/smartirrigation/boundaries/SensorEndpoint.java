package com.supcom.cot.smartirrigation.boundaries;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import com.supcom.cot.smartirrigation.entities.Sensor;
import com.supcom.cot.smartirrigation.repositories.SensorRepository;

import java.util.List;
import java.util.function.Supplier;

import static java.util.stream.Collectors.toList;
@ApplicationScoped
@Path("sensor") //The path where the REST service is going to be implemented
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)  // @produces and @consumes to specifiy that the data sent and received is in JSON format
public class SensorEndpoint {
    private static final Supplier<WebApplicationException> NOT_FOUND =
            () -> new WebApplicationException(Response.Status.NOT_FOUND);

    @Inject
    private SensorRepository repository; // Inject the repository to  utilize its methods of interacting with the database
    @GET
    public List<Sensor> findAll() { //GET METHOD to receive a list of all SensorDB data from the database
        return repository.findAll()
                .collect(toList());
    }
    @POST // POST METHOD to send the data of the sensor in JSON format and save it in the database
    public void save(Sensor sensor) {
        repository.save(sensor);
    }

    @PUT
    @Path("{id}")
    public void update(@PathParam("id") String id, Sensor updatedSensor) {
        Sensor existingSensor = repository.findById(id).orElseThrow(NOT_FOUND);

        existingSensor.setId(updatedSensor.getId());
        existingSensor.setTemperature(updatedSensor.getTemperature());
        existingSensor.setMoisture(updatedSensor.getMoisture());
        existingSensor.setHumidity(updatedSensor.getHumidity());

        repository.save(existingSensor);
    }
}
