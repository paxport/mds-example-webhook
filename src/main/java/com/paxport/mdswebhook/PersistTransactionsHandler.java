package com.paxport.mdswebhook;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.paxport.mdswebhook.db.SupplierTransactionRepo;

import java.io.IOException;

/**
 * Handle the incoming orders by pulling out the supplier transactions
 * and persisting them to a mysql database
 */
public class PersistTransactionsHandler implements WebhookHandler {

    private ObjectMapper mapper = new ObjectMapper();
    private SupplierTransactionRepo repo = new SupplierTransactionRepo();

    @Override
    public String handleIncomingOrder(String json) throws IOException {
        JsonNode root = mapper.readTree(json);

        // supplierTransactions
        JsonNode supplierTransactions = root.get("supplierTransactions");
        for (JsonNode txn : supplierTransactions) {
            repo.upsertTransaction(txn,root);
        }

        return "okay";
    }
}
