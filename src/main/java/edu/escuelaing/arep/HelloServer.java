package edu.escuelaing.arep;

import static spark.Spark.*;

public class HelloServer {


    public static void main(String args []){
        port(getPort());

        secure(getKeyStore(), "password", null, null);

        get("/hello", (req, res) -> "Hello From My New Server");
    }


    static int getPort() {
        if (System.getenv("PORT") != null) {
            return Integer.parseInt(System.getenv("PORT"));
        }
        return 2703; //returns default port if heroku-port isn't set (i.e. on localhost)
    }

    static String getKeyStore() {
        if (System.getenv("KEYSTORE") != null) {
            return System.getenv("KEYSTORE");
        }
        return "keystores/ecikeystore.p12"; //returns default keystore if keystore isn't set (i.e. on localhost)
    }
}
