package com.supcom.cot.smartirrigation.boundaries;
import jakarta.ejb.EJBException;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.RequestScoped;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import org.json.*;
import java.io.UnsupportedEncodingException;
import java.util.Base64;
import com.supcom.cot.smartirrigation.util.OAuth2PKCE;
import com.supcom.cot.smartirrigation.controllers.UserManager;
import com.supcom.cot.smartirrigation.util.Identity;



// Implementation of the SignIn method following Oauth2 and PKCE flow
@Path("/")
@RequestScoped
public class SignInEndpoint {
    public final static String XSS_COOKIE_NAME = "xssCookie";

    @EJB
    private OAuth2PKCE oAuth2PKCE; //injecting oAuth2PKCE methods
    @EJB
    private UserManager identityController;
    @Context
    private UriInfo uriInfo;


    @POST //The first step of the OAUTH2 PKCE authentication is a post method to /authorize path to generate a signInId
    @Path("/authorize")
    @Produces(MediaType.APPLICATION_JSON)
    public Response preSignIn(@HeaderParam("Pre-Authorization") String authorization) throws UnsupportedEncodingException { // The client must send a post request with the Header "Pre-Authorization" and value "Bearer encoded64utf8(clientid#codechallenge).
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid Authorization Header").build();
        }
        try {
            byte[] bytes = Base64.getDecoder().decode(authorization.substring("Bearer ".length()));
            String decoded = new String(bytes, "ISO_8859_1");
            String[] credentials = decoded.split("#"); //divide 'decoded' into two variables: clientid and code challenge
            NewCookie cookie = new NewCookie(XSS_COOKIE_NAME, //generate the cookie for security
                    oAuth2PKCE.generateXSSToken(credentials[0],uriInfo.getBaseUri().getPath()),
                    uriInfo.getBaseUri().getPath(),
                    uriInfo.getBaseUri().getHost(),"Secure Http Only Cookie",86400,true,true);
            return Response .status(Response.Status.FOUND)
                    .cookie(cookie)
                    .entity("{\"signInId\":\""+oAuth2PKCE.addChallenge(credentials[1],credentials[0])+ //Return SignInId
                            "\"}").build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Malformed Authorization Header").build();
        }

    }


    @POST //In the second step, the client taps in his mail and password and send a post request to get the authorization code
    @Path("/authenticate")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response signIn(String json){
        JSONObject obj = new JSONObject(json);
        String mail=obj.getString("mail"); //get the username password and signinId from the json object sent by the client
        String password=obj.getString("password");
        String signInId=obj.getString("signInId");
        if(mail == null || password == null || signInId == null ||
                mail.length()<4 || mail.length()>30 ){
            return Response.status(Response.Status.NOT_ACCEPTABLE).entity("{\"message\":\"Invalid Credentials!\"}").build();
        }
        try {
            Identity identity = identityController.authenticate(mail,password);// check if the user exists and his password against the hashed argon2 password are valid
            return Response.ok()
                    .entity("{\"authCode\":\""+oAuth2PKCE.generateAuthorizationCode(signInId,identity)+"\"}") //return authorization code
                    .build();
        } catch (EJBException e) {
            return Response.status(Response.Status.UNAUTHORIZED) //return status unauthorized if the conditions are not met
                    .entity("{\"message\":\""+e.getMessage()+"\"}").build();
        }
    }
    //After the second step, the client can now login to the application but he must request an access token (jwt) and refresh token to access data from the application
    @GET
    @Path("/oauth/token")
    @Produces(MediaType.APPLICATION_JSON)
    public Response postSignIn(@HeaderParam("Post-Authorization") String authorization) throws  UnsupportedEncodingException {// The client sends a request with header "Post-Authorization" and value "Bearer encoded64(Authcode#codeverifier)"
        byte[] bytes=Base64.getDecoder().decode(authorization.substring("Bearer ".length())); //Extract the encoded string with substring "Bearer "
        String decoded = new String(bytes,"ISO_8859_1"); //decoded=authcode#codeverifier
        String[] credentials = decoded.split("#");
        String token;
        try {
            token = oAuth2PKCE.checkCode(credentials[0],credentials[1]);// verify the credentials and return the access token if the credentials match
        } catch (Exception e) {
            return Response.serverError().entity("{\"message\":\""+e.getMessage()+"\"}").build();
        }
        return token==null?Response.status(Response.Status.UNAUTHORIZED).entity("{\"message\":\""+decoded+"\",Unauthorized Access!\"}").build():
                Response.ok().entity("{\"accessToken\":\""+token+"\",\"refreshToken\":\""+oAuth2PKCE.generateRefreshTokenFor(token)+"\"}").build(); // return the access token and generate the refreshtoken
    }
    // access tokens have an expiration date and no longer becomes valid after certain time, therefore the client must periodically send his access token and refresh token to get new ones
    @GET
    @Path("/oauth/token/refresh")
    @Produces(MediaType.APPLICATION_JSON)
    public Response refreshSignIn(@HeaderParam("Refresh-Authorization")String refreshToken,@HeaderParam(HttpHeaders.AUTHORIZATION) String accessToken) {  //Send access token and refresh token in Header parameters
        String refreshPayload = accessToken.substring("Bearer ".length(),accessToken.lastIndexOf("."));// Json tokens are composed of a Header, Payload and Signature that are seperated by "." . with substring we can get the Header and Payload
        if(oAuth2PKCE.check(refreshPayload,refreshToken)){ //Check if the refreshtoken given corresponds to the refreshtoken of the accesstoken
            String jwt=new String(Base64.getDecoder().decode(accessToken.substring(accessToken.indexOf(".")+1,accessToken.lastIndexOf(".")))); // with substring, get the Payload and decode it
            StringBuilder sb = new StringBuilder();
            for (int i=8; i<100; i++) { // get the username from jwt token
                if (jwt.charAt(i)=='"'){
                    break;
                }
                sb.append(jwt.charAt(i));

            }

            Identity identity = identityController.findByUsername(sb.toString()); //verify if the user exists in the database
            String token;

            try {
                token = oAuth2PKCE.generateTokenFor(identity); // generate new access token
            } catch (Exception e) {
                return Response.serverError().entity("{\"message\":\""+e.getMessage()+"\"}").build();
            }
            return token == null ?Response.status(Response.Status.UNAUTHORIZED).entity("{\"message\":\"Unauthorized Access!\"}").build():
                    Response.ok().entity("{\"accessToken\":\""+token+"\",\"refreshToken\":\""+oAuth2PKCE.generateRefreshTokenFor(token)+"\"}").build(); // generate new refresh token as well
        }
        return Response.status(Response.Status.UNAUTHORIZED).entity("{\"message\":\"Unauthorized Access!\"}").build();
    }

}
