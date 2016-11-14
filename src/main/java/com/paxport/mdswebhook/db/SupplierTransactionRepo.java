package com.paxport.mdswebhook.db;

import com.fasterxml.jackson.databind.JsonNode;
import org.skife.jdbi.v2.DBI;

/**
 * Insert or Update the given supplier transaction and constituent items
 */
public class SupplierTransactionRepo {

    private DBI dbi = DBConnections.dbi();
    private UpsertSupplierTransaction upsertTransaction = new UpsertSupplierTransaction(dbi);
    private UpsertTransactionItem upsertItem = new UpsertTransactionItem(dbi);
    private DBSchemaCheck check = new DBSchemaCheck(dbi).ensureSchemaIsUptoDate();

    public void upsertTransaction (JsonNode txn, JsonNode order){
        String txnId = txn.get("transactionIdentity").asText();
        upsertTransaction.upsertTxn(txn,order);
        JsonNode items = txn.get("items");
        int idx = 0;
        for (JsonNode item : items) {
            upsertItem.upsertItem(txnId,idx++,item);
        }
    }

}
