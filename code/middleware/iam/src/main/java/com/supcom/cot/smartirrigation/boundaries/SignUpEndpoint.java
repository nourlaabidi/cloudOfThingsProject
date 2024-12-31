package com.supcom.cot.smartirrigation.boundaries;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import com.supcom.cot.smartirrigation.util.Argon2Utility;

import java.util.Optional;

import com.supcom.cot.smartirrigation.entities.User;
import com.supcom.cot.smartirrigation.repositories.UserRepository;

@ApplicationScoped
@Path("user")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SignUpEndpoint {
    @Inject
    private UserRepository repository;
    @POST
    public Response save(User user) {
        // Vérification que les données ne sont pas nulles
        if (user == null || user.getmail() == null || user.getpassword() == null || user.getPermissionLevel() == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("{\"message\": \"Invalid user data\"}").build();
        }

        try {
            Optional<User> existingUser = repository.findById(user.getmail());
            if (existingUser.isPresent()) {
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity("{\"message\": \"User already exists\"}")
                        .build();
            }
            String password = user.getpassword();
            String passwordhash = Argon2Utility.hash(password.toCharArray()); // Hash the password tapped by the user before saving it in the database
            User userhash = new User(user.getmail(), user.getuserName(), passwordhash, user.getPermissionLevel());
            repository.save(userhash);

            return Response.ok().entity("{\"message\": \"User created successfully\"}").build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"message\": \"An error occurred while creating the user\"}")
                    .build();
        }
    }






}

