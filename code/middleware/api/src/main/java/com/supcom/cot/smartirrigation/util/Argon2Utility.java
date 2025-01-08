package com.supcom.cot.smartirrigation.util;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;

public class Argon2Utility { //Storing password directly into the database is dangerous. It is necessary to hash them.

    private final static Config config = ConfigProvider.getConfig(); // Get argon2 parameters from system variables
    private final static int saltLength = config.getValue("argon2.saltLength", Integer.class);
    private final static int hashLength = config.getValue("argon2.hashLength", Integer.class);
    private final static Argon2 argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id, saltLength, hashLength); //Create an argon2id variant which is a hybrid version of argon2i and argon2d, thus making it more well rounded.
    private final static int iterations = config.getValue("argon2.iterations", Integer.class);
    private final static int memory = config.getValue("argon2.memory", Integer.class);
    private final static int threadNumber = config.getValue("argon2.threadNumber", Integer.class);

    public static String hash(char[] clientHash){ // Function to hash passwords given the argon2 parameters.
        try{
            return argon2.hash(iterations, memory, threadNumber, clientHash);
        } finally {
            argon2.wipeArray(clientHash);
        }
    }
    public static boolean check(String dbHash, char[] clientHash) { //Used to authenticate the user, taking his password and the hashed password in the database as input
        try {
            return argon2.verify(dbHash, clientHash); //verifies the password against the argon hash stored for the user
        } finally {
            argon2.wipeArray(clientHash);
        }
    }

}
