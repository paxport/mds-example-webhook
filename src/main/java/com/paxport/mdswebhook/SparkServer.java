package com.paxport.mdswebhook;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;

import static spark.Spark.*;

/**
 * This is the server entry point and it listens on port 8080 by default
 */
public class SparkServer {

    private final static Logger logger = LoggerFactory.getLogger(SparkServer.class);

    // this is the handler that will do the work
    private final static WebhookHandler handler = new WebhookHandlerFactory().create();

    // expecting query param token=_tellno1_ otherwise request will be rejected
    private final static String SHARED_SECRET = "_tellno1_";

    public static void main(String[] args) {
        if ( args.length > 0 ){
            port(Integer.parseInt(args[0]));
        }
        else{
            port(8080);
        }
        get("*", (req, res) -> "GETs are not supported by this example webhook");
        post("/orders", (req, res) -> handleOrderPushMessage(req,res));
    }

    private static String handleOrderPushMessage(Request req, Response res) {
        String json = req.body();
        if ( !tokenMatchesSecret(req) ){
            res.status(403);
            return "unauthorised";
        }
        if (logger.isDebugEnabled()){
            logger.debug("incoming request -->\n" + json );
        }
        return handler.handleIncomingOrder(json);
    }

    private static boolean tokenMatchesSecret(Request req) {
        String token = req.queryParams("token");
        return SHARED_SECRET.equals(token);
    }

}
