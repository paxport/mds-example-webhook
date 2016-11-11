package com.paxport.mdswebhook;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;

import static spark.Spark.*;

/**
 * This is the server entry point
 */
public class SparkServer {

    private final Logger logger = LoggerFactory.getLogger(SparkServer.class);
    private final static WebhookHandler handler = new WebhookHandlerFactory().create();

    // expecting query param token=_tellno1_ otherwise request will be rejected
    private final static String SHARED_SECRET = "_tellno1_";

    public static void main(String[] args) {
        port(8080);
        get("*", (req, res) -> "GETs are not supported by this example webhook");
        post("/orders", (req, res) -> handleOrderPushMessage(req,res));
    }

    private static String handleOrderPushMessage(Request req, Response res) {
        if ( !tokenMatchesSecret(req) ){
            res.status(403);
            return "unauthorised";
        }
        return handler.handleIncomingOrder(req.body());
    }

    private static boolean tokenMatchesSecret(Request req) {
        String token = req.queryParams("token");
        return SHARED_SECRET.equals(token);
    }

}
