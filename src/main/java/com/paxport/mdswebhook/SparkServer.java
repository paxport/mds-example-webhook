package com.paxport.mdswebhook;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;

import static com.paxport.mdswebhook.Config.envOrSysProp;
import static spark.Spark.*;

/**
 * This is the server entry point and it listens on port 8080 by default
 */
public class SparkServer {

    private final static Logger logger = LoggerFactory.getLogger(SparkServer.class);

    private final static String LISTEN_PORT = envOrSysProp("LISTEN_PORT", "8181");

    // this is the handler that will do the work
    private final static WebhookHandler handler = new WebhookHandlerFactory().create();

    // expecting query param token=_tellno1_ otherwise request will be rejected
    private final static String SHARED_SECRET = envOrSysProp("SHARED_SECRET","oursharedsecr37");

    public static void main(String[] args) {
        int port = Integer.parseInt(LISTEN_PORT);
        port(port);
        get("*", (req, res) -> "GETs are not supported by this example webhook");
        post("/orders", (req, res) -> handleOrderPushMessage(req,res));
        logger.info("Webhook is now listening for incoming POSTs to /orders on port " + port);
    }

    private static String handleOrderPushMessage(Request req, Response res) throws Exception {
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
