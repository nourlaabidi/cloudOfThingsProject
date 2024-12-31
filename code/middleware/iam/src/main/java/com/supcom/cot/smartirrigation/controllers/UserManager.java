package com.supcom.cot.smartirrigation.controllers;

import jakarta.ejb.EJBException;
import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import com.supcom.cot.smartirrigation.entities.User;
import com.supcom.cot.smartirrigation.repositories.UserRepository;
import com.supcom.cot.smartirrigation.util.Argon2Utility;

@Stateless
@LocalBean
public class UserManager {
    @Inject
    private UserRepository userRepository; // repository to interact with the user database
    public User findByUsername(String mail){ //takes mail as input and returns the user
        final User user = userRepository.findById(mail).orElseThrow();
        return user; // return the user with the mail specified.
    }
    public User authenticate(final String mail, final String password) throws EJBException {// method used in sign in, takes username and password as input and returns the user if authentication succeeds
        final User user = userRepository.findById(mail).orElseThrow();
        if(user != null && Argon2Utility.check(user.getpassword(), password.toCharArray())){ //checks the password with the hashed password in the database
            return user;
        }
        throw new EJBException("Failed sign in with mail: " + mail + " [Unknown mail or wrong password]");

    }
    public User authenticateadmin(final String mail, final String password) throws EJBException {// authentication for admin dashboard
        final User user = userRepository.findById(mail).orElseThrow();
        if(user != null && Argon2Utility.check(user.getpassword(), password.toCharArray()) && user.getPermissionLevel().equals(2L)){ //check the permission
            return user;
        }
        throw new EJBException("Failed sign in with mail: " + mail + " [Unknown mail or wrong password]");

    }


}
