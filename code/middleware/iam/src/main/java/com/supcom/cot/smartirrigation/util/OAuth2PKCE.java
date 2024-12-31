package com.supcom.cot.smartirrigation.util;

import com.supcom.cot.smartirrigation.controllers.Role;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.RSASSASigner;
import jakarta.ejb.Singleton;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObjectBuilder;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.*;

@Singleton
public class OAuth2PKCE { //class containing useful methods needed to implement Oauth2 PKCE authentication with JWT
    public static final String AuthenticationSchemePrefix = "Bearer "; //All requests must have the prefix "Bearer "

    private final Map<String,String> challenges = new HashMap<>(); //Hashmap to store the code challenges of users
    private final Map<String,String> codes = new HashMap<>(); // Hashmap to store the authorization code
    private final Map<String,Identity> identities = new HashMap<>(); //Hashmap tp store the identities



    public String addChallenge(String codeChallenge,String clientId){  //store the code challenge in the hashmap and generate the signinId
        String signInId = clientId+"#"+UUID.randomUUID().toString();
        challenges.put(codeChallenge,signInId);
        return signInId;
    }

    public String generateAuthorizationCode(String signInId, Identity identity){ //generate the authorization code and store the code and identity in hashmap
        String code = UUID.randomUUID().toString();
        codes.put(signInId,code);
        identities.put(code,identity);
        return code;
    }

    public String checkCode(String code,String codeVerifier) throws Exception {// takes from the client an authorization code and the code verifier as input and generate a code challenge and compares it to the first code challenge given by the client. If they are equal, generate a jwt token.

        MessageDigest md = MessageDigest.getInstance("SHA-256");// code challenge=sha256(code verifier)
        md.update(codeVerifier.getBytes(StandardCharsets.UTF_8));
        String key = Base64.getEncoder().encodeToString(md.digest());
        key=key.substring(0,key.length()-1);//adapt the code challenge generation to the generated in flutter
        if (key.contains("/")) {key=key.replace("/","_");}//adapt the code challenge generation to the generated in flutter
        if (key.contains("+")) {key=key.replace("+","-");}//adapt the code challenge generation to the generated in flutter
        if(challenges.containsKey(key)){
            if(codes.get(challenges.get(key)).equals(code)){//compare the authorization code given to the one generated before
                codes.remove(challenges.get(key)); // remove the instance to not overload the hashmap
                challenges.remove(key); // remove the instance to not overload the hashmap
                return generateTokenFor(identities.remove(code)); //generate the jwt token
            }
        }
        codes.entrySet().stream().filter(e -> e.getValue().equals(code)).findFirst().ifPresent(
                e -> {
                    codes.remove(e.getKey()); // remove the instances to not overload the hasmap
                    challenges.entrySet().stream().filter(f -> f.getValue().equals(e.getKey())).findFirst()
                            .ifPresent(f->challenges.remove(f.getKey()));
                }
        );
        identities.remove(code);
        return null; // return null if the verification tests failed
    }

    private static final Config config = ConfigProvider.getConfig(); // get config properties from the system variables
    static {
        FileInputStream fis = null;
        char[] password = "password".toCharArray(); // Remplacer par le mot de passe du keystore
        String alias = "jwt"; // Remplacer par l'alias du keystore

        PrivateKey pk = null;
        PublicKey pub = null;
        try {
            KeyStore ks = KeyStore.getInstance("JKS"); // the jwt keystore is in .jks format
            String configDir = "C:\\Program Files\\wildfly\\wildfly-preview-34.0.0.Final\\standalone\\configuration"; // the jwt keystore must be generated beforehand with openssl and put in the wildfly/standalone/configuration directory beforehand
            String keystorePath = configDir + File.separator + "jwt.jks";
            fis = new FileInputStream(keystorePath); //read the file
            ks.load(fis, password); // the file can only be loaded with its correct password
            Key key = ks.getKey("jwt", password); // alias and password of keystore
            if (key instanceof PrivateKey) {
                pk = (PrivateKey) key; //get the private key, the private key is used for signing tokens
                // Get certificate of public key
                java.security.cert.Certificate cert = ks.getCertificate(alias);
                pub = cert.getPublicKey(); // get the public key, the public key is used for verifying tokens
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException ignored) {}
            }
        }
        privateKey = pk;
        publicKey = pub;
    }

    private static final PrivateKey privateKey;
    static final PublicKey publicKey;
    private static final int TOKEN_VALIDITY = config.getValue("jwtTokenValidity",Integer.class); // token time before expiration
    public static final String CLAIM_ROLES = config.getValue("jwtClaimRoles",String.class);
    static final String ISSUER = config.getValue("jwtIssuer",String.class); // domain name
    static final String AUDIENCE = config.getValue("jwtAudience",String.class);
    private final JWSSigner signer = new RSASSASigner(privateKey); //signature algorithm for the jwt used is RSA

    private Role[] getRoles(Long permissionLevel) {
        Set<Role> roles = new HashSet<>();
        for(Role role: Role.values()){
            if((permissionLevel & role.getValue()) != 0L){
                roles.add(role);
            }
        }
        return roles.toArray(new Role[0]);
    }

    public String generateTokenFor(Identity identity) throws Exception {//generation of the jwt token
        JsonArrayBuilder rolesBuilder = Json.createArrayBuilder();
        for (Role role : getRoles(identity.getPermissionLevel())) {//the role is based on the permission level of the identity
            rolesBuilder.add(role.toString());
        }
        long currentTime = System.currentTimeMillis() / 1000;
        JsonObjectBuilder claimsBuilder = Json.createObjectBuilder()
                .add("sub", identity.getName()) //id of the user (mail)
                .add("iss", ISSUER) // name of the domain generating it
                .add("aud", AUDIENCE)
                .add(CLAIM_ROLES, rolesBuilder.build())
                .add("iat",currentTime)
                .add("nbf",currentTime + 1)
                .add("exp",currentTime + TOKEN_VALIDITY) // expiration date of token
                .add("jti", "urn:phenix:token:"+UUID.randomUUID()); // unique identifier of token
        JWSObject jwsObject = new JWSObject(new JWSHeader.Builder(JWSAlgorithm.RS512) //RSASSA-PKCS-v1_5 using SHA-512 hash algorithm
                .type(new JOSEObjectType("jwt")).build(),
                new Payload(claimsBuilder.build().toString()));//create the jwt

        jwsObject.sign(signer); // sign the jwt with the private key

        return jwsObject.serialize();
    }

    private static Mac hmac; //hmac will be used to provide integrity protection for the refresh tokens

    static {
        try {
            hmac = Mac.getInstance("HmacSHA256");
            SecureRandom secureRandom = new SecureRandom();
            SecretKeySpec secret_key = new SecretKeySpec(secureRandom.generateSeed(32), "HmacSHA256");
            hmac.init(secret_key); // create a secret key and assign it to the HS256
        } catch (NoSuchAlgorithmException | InvalidKeyException  e) {
            e.printStackTrace();
        }
    }

    public String hmacSignature(String toSign){
        return Base64.getEncoder().encodeToString(hmac.doFinal(toSign.getBytes())); // sign the string with hmac and encode it
    }

    public String generateRefreshTokenFor(String accessToken){ // generation of refresh tokens
        String refreshPayload = accessToken.substring(0,accessToken.lastIndexOf(".")); //get the header and payload of the token
        String some = new SecureRandom().ints(48,123)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(64)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
        return some+"."+hmacSignature(refreshPayload+some);
    }

    public boolean check(String accessTokenHeaderAndPayload,String refresh){
        String[] split =refresh.split("\\.");
        return hmacSignature(accessTokenHeaderAndPayload + split[0]).equals(split[1]);

    }

    public String generateXSSToken(String credential,String baseURI) { // to protect against cross-site scripting
        long currentTime = System.currentTimeMillis() / 1000;
        JsonObjectBuilder claimsBuilder = Json.createObjectBuilder()
                .add("sub", credential)
                .add("iss", ISSUER)
                .add("aud", AUDIENCE)
                .add("uri", baseURI)
                .add("iat", currentTime)
                .add("nbf",currentTime + 1)
                .add("exp", currentTime+86407L)
                .add("jti", "urn:phenix:token:"+UUID.randomUUID());

        JWSObject jwsObject = new JWSObject(new JWSHeader.Builder(JWSAlgorithm.RS512)
                .type(new JOSEObjectType("jwt")).build(),
                new Payload(claimsBuilder.build().toString()));
        try {
            jwsObject.sign(signer);
        } catch (JOSEException e) {
            e.printStackTrace();
        }
        return jwsObject.serialize();
    }

}

