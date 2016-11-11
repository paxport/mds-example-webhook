package com.paxport.mdswebhook;

/**
 * Handle the incoming orders by pulling out the supplier transactions
 * and persisting them to a mysql database
 */
public class PersistTransactionsHandler implements WebhookHandler {
    @Override
    public String handleIncomingOrder(String json) {


        return "okay";
    }
}
