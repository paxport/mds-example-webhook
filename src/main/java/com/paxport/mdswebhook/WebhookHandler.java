package com.paxport.mdswebhook;

/**
 * An example implementation of this interface is {@link PersistTransactionsHandler}
 */
public interface WebhookHandler {

    String handleIncomingOrder(String json);
}
