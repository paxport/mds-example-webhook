package com.paxport.mdswebhook.db;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.ImmutableList;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Update;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.paxport.mdswebhook.db.DBNames.*;
import static com.paxport.mdswebhook.db.SQLUtils.upsert;

/**
 * Insert or update rows in the database from the incoming
 * transaction data which includes a list of child items
 *
 */
public class UpsertSupplierTransaction {
    private final static Logger logger = LoggerFactory.getLogger(UpsertSupplierTransaction.class);

    private DBI dbi;

    private final static List<String> INSERT_COLS = supplierTransactionInsertCols();
    private final static List<String> UPDATE_COLS = supplierTransactionUpdateCols();

    public UpsertSupplierTransaction(DBI dbi) {
        this.dbi = dbi;
    }

    /**
     * Insert a new transaction or update it if it already exists
     * This uses Mysql's INSERT ON DUPLICATE KEY UPDATE
     * @param txn
     */
    public void upsertTxn(JsonNode txn, JsonNode order) {
        String sql = upsert(SCHEMA,
                SUPPLIER_TRANSACTIONS,
                INSERT_COLS,
                UPDATE_COLS
        );
        if ( logger.isDebugEnabled() ) {
            logger.debug("sql --> " + sql);
        }
        int updated = dbi.withHandle(handle -> {
            Update update = handle.createStatement(sql);
            prepareUpsert(update,txn,order);
            return update.execute();
        });
        if ( logger.isDebugEnabled() ) {
            logger.debug("updated --> " + updated);
        }
    }

    private static List<String> supplierTransactionInsertCols() {
        List<String> insert = ImmutableList.of(TXN_ID,CREATED_AT,TARGET,AGENT_ID,SUPPLIER,SYSTEM,TYPE,
                MOST_RELEVANT_DATE,COST_AMOUNT,COST_CURRENCY,PAX_NAME);
        List<String> both = new ArrayList<>(insert);
        both.addAll(supplierTransactionUpdateCols());
        return ImmutableList.copyOf(both);
    }

    private static List<String> supplierTransactionUpdateCols() {
        return ImmutableList.of(STATUS,ORDER_ID,BOOKING_REFERENCE,UPDATED_AT,
                FAILURE_REASON,PRICE_AMOUNT,PRICE_CURRENCY,REQUEST_ID,TRACING_ID,LOGICAL_SESSION_ID,
                EXTERNAL_PAYMENT_REFERENCE);
    }

    private void prepareUpsert(Update update, JsonNode txn, JsonNode order) throws SQLException {
        int idx = 0;

        // first set all the insert column values
        update.bind(idx++,txn.get("transactionIdentity").asText());

        update.bind(idx++, Timestamp.from(ZonedDateTime.parse(txn.get("createdAt").asText()).toInstant()));
        update.bind(idx++,order.get("trackingInfo").get("target").asText());
        if ( order.get("trackingInfo").hasNonNull("agentId") ){
            update.bind(idx++,order.get("trackingInfo").get("agentId").asText(null));
        }
        else{
            update.bind(idx++,(String)null);
        }
        update.bind(idx++,txn.get("supplier").asText());
        update.bind(idx++,txn.get("system").asText());
        update.bind(idx++,txn.get("type").asText());
        update.bind(idx++, Date.valueOf(LocalDate.parse(txn.get("mostRelevantDate").asText())));
        update.bind(idx++,txn.get("totalCost").get("amount").asDouble());
        update.bind(idx++,txn.get("totalCost").get("currency").asText());
        update.bind(idx++,txn.get("paxName").asText(null));
        idx = commonPrep(update, txn, order, idx);

        // now set all the update columns in the case that the txn is already in the table
        idx = commonPrep(update, txn, order, idx);
    }

    private int commonPrep(Update ps, JsonNode txn, JsonNode order, int idx) throws SQLException {
        ps.bind(idx++,txn.get("status").asText());
        ps.bind(idx++,order.get("identifier").asText());
        ps.bind(idx++,txn.get("bookingReference").asText(null));
        String updatedAt = txn.get("updatedAt").asText(null);
        if (updatedAt != null ){
            ps.bind(idx++, Timestamp.from(ZonedDateTime.parse(updatedAt).toInstant()));
        }
        else {
            ps.bind(idx++,(Timestamp)null);
        }
        if ( txn.hasNonNull("failureReason") ){
            ps.bind(idx++,txn.get("failureReason").asText(null));
        }
        else{
            ps.bind(idx++,(String)null);
        }
        if ( !txn.hasNonNull("totalPrice") ) {
            JsonNode totalPrice = txn.get("totalPrice");
            ps.bind(idx++,totalPrice.get("amount").asDouble());
            ps.bind(idx++,totalPrice.get("currency").asText());
        }
        else {
            ps.bind(idx++,(Double)null);
            ps.bind(idx++,(String)null);
        }
        JsonNode ti = order.get("trackingInfo");
        ps.bind(idx++,ti.hasNonNull("requestId") ? ti.get("requestId").asText() : null);
        ps.bind(idx++,ti.hasNonNull("internalTracingId") ? ti.get("internalTracingId").asText() : null);
        ps.bind(idx++,ti.hasNonNull("logicalSessionId") ? ti.get("logicalSessionId").asText() : null);
        ps.bind(idx++,ti.hasNonNull("externalPaymentReference") ? ti.get("externalPaymentReference").asText() : null);
        return idx;
    }

}
