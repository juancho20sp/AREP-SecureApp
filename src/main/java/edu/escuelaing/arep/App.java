package edu.escuelaing.arep;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import edu.escuelaing.arep.model.User;
import edu.escuelaing.arep.utils.PasswordManager;
import edu.escuelaing.arep.utils.SecureURLReader;
import spark.Request;
import spark.Response;

import java.util.ArrayList;
import java.util.HashMap;

import static spark.Spark.*;

public class App {

    private static HashMap<String, String> users = new HashMap<String, String>();

    public static void main( String[] args ) {

        // Get default port
        port(getPort());

        // Connect securely to the server
        SecureURLReader.connectSecurely();

        // Generate the dummy database
        generateUsers();

        // Set the file location
        staticFileLocation("/public");

        //API: secure(keystoreFilePath, keystorePassword, truststoreFilePath, truststorePassword);
//        secure("keystores/ecikeystore.p12", "password", null, null);
        secure(getKeyStore(), "password", null, null);


        get("/hello", (req, res) -> "Hello World");

        // Allow CORS
        options("/*",
                (request, response) -> {
                    String accessControlRequestHeaders = request.headers("Access-Control-Request-Headers");
                    if (accessControlRequestHeaders != null) {
                        response.header("Access-Control-Allow-Headers", accessControlRequestHeaders);
                    }
                    String accessControlRequestMethod = request.headers("Access-Control-Request-Method");
                    if (accessControlRequestMethod != null) {
                        response.header("Access-Control-Allow-Methods", accessControlRequestMethod);
                    }
                    return "OK";
                });

        before((request, response) -> response.header("Access-Control-Allow-Origin", "*"));
        before("/security/*",(req, res) -> isUserLoggedIn(req));

        get("/", (req, res) -> {
            res.redirect("/login.html");
            res.status(200);
            return null;
        });

        post("/login", (req, res) -> {
            res.type("application/json");

            if (req.body() != null) {
                ArrayList<String> responses = login(req);
                return createJson(200, responses.get(0), responses.get(1));
            }
            return createJson(400, "Bad request" ,"Not logged");
        });

//        get("/login/service", (req, res) -> {
//            System.out.println("SERVICIO");
//            return createJson(200, "Login successful!", getHelloServiceResponse(req));
//        });

        get("/security/helloService", (req, res) -> onHelloService(res));

    }

    private static ArrayList<String> login(Request req){
        User user = (new Gson()).fromJson(req.body(), User.class);

        // Validate that the username and password are in the request and are valid
        String username = user.getUsername();
        String password = user.getPassword();

        boolean isUsernamePresent = users.containsKey(username);
        boolean isPasswordCorrect = users.get(username).equals(PasswordManager.hashPassword(password));

        ArrayList<String> responses = new ArrayList<String>();

        if(isUsernamePresent){
            if(isPasswordCorrect){
                req.session(true);
                req.session().attribute("isLoggedIn", true);
                responses.add("Login successful!");;
                responses.add(getHelloServiceResponse(req));
                return responses;
            }

            responses.add("Wrong password");
            responses.add(getHelloServiceResponse(req));
            return responses;
        }

        responses.add("User doesn't exist");
        responses.add(getHelloServiceResponse(req));
        return responses;
    }

    private static String getHelloServiceResponse(Request req){
        req.session();
        try{
            if((boolean)req.session().attribute("login")){;
//                return SecureURLReader.readURL("https://localhost:2703/hello");
                return SecureURLReader.readURL("https://ec2-3-80-114-17.compute-1.amazonaws.com:2703/hello");

            }
        }catch(Exception e){
            System.out.println(e);
            System.out.println("Not");
            return "Not Logged in!";
        }
        return null;

    }

    /**
     * Redirect to login page when the user is not logged in.
     * @param req
     * @param res
     * @return The status of the user trying to connect to the page
     */
//    private static String goToLoginPage(Request req, Response res){
//        String statusMessage = "error";
//        int statusCode = 404;
//
//        req.session(true);
//
//        // Get the user info
//        User user = (new Gson()).fromJson(req.body(), User.class);
//
//        // Validate that the username and password are in the request and are valid
//        String username = user.getUsername();
//        String password = user.getPassword();
//
//        boolean isUsernamePresent = users.containsKey(username);
//        boolean isPasswordCorrect = users.get(username).equals(PasswordManager.hashPassword(password));
//
//        if (isUsernamePresent && isPasswordCorrect){
//            req.session().attribute("username", username);
//            req.session().attribute("isLoggedIn", true);
//
//            statusCode = 200;
//            statusMessage = "Login successful";
//        }
//
//        res.status(statusCode);
//        return statusMessage;
//    }


    /**
     * Verifies if the user is logged in
     */
    private static boolean isUserLoggedIn(Request req) {
        // $
        System.out.println("is User Logged in");

        req.session(true);

        if (req.session().isNew()) {
            req.session().attribute("isLoggedIn", false);
        }

        if ((boolean) req.session().attribute("isLoggedIn")){
            return true;
        }

        halt(401, "<h1>No está autorizado para estar aquí</h1>");
        return false;
    }

    /**
     * Go to hello service!
     */
    private static String onHelloService(Response res){
        try {
//            return "<h1>" + SecureURLReader.readURL("https://localhost:2703/hello") + "</h1>";
            return "<h1>" + SecureURLReader.readURL("https://ec2-3-80-114-17.compute-1.amazonaws.com:2703/hello") + "</h1>";

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        res.status(500);
        return "<h1>Internal server error</h1>";
    }

    /**
     * Method used for generating the dummy database
     */
    private static  void generateUsers(){
        users.put("juan", PasswordManager.hashPassword("password"));
        users.put("david", PasswordManager.hashPassword("password"));
        users.put("test", PasswordManager.hashPassword("password"));
    }

    /**
     * Method for unifying the JSON responses
     * @param status
     * @param result
     * @param serverResponse
     * @return
     */
    private static JsonObject createJson(int status, String result, String serverResponse){
        JsonObject json  =new JsonObject();
        json.addProperty("status", status);
        json.addProperty("result", result);
        json.addProperty("serverResponse", serverResponse);

        return json;
    }

    /**
     * This method reads the default port as specified by the PORT environment variable.
     */
    static int getPort() {
        if (System.getenv("PORT") != null) {
            return Integer.parseInt(System.getenv("PORT"));
        }
        return 4567; //returns default port if heroku-port isn't set (i.e. on localhost)
    }

    static String getKeyStore() {
        if (System.getenv("KEYSTORE") != null) {
            return System.getenv("KEYSTORE");
        }
        return "keystores/ecikeystore.p12"; //returns default keystore if keystore isn't set (i.e. on localhost)
    }


}


//i-0c63a2cc
//        i-00412
//
//        java -cp "target/classes:target/dependency/*" "edu.escuelaing.arep.App"
//
//        sudo amazon-linux-extras install java-openjdk11




