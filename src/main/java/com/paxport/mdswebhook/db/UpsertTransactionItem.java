package com.paxport.mdswebhook.db;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.ImmutableList;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Update;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static com.paxport.mdswebhook.db.DBNames.*;
import static com.paxport.mdswebhook.db.SQLUtils.upsert;

/**
 * Insert or update rows in the database from the incoming
 * transaction item
 *
 */
public class UpsertTransactionItem {
    private final static Logger logger = LoggerFactory.getLogger(UpsertTransactionItem.class);

    private DBI dbi;

    public UpsertTransactionItem(DBI dbi) {
        this.dbi = dbi;
    }

    private final static List<String> INSERT_COLS = transactionItemInsertCols();
    private final static List<String> UPDATE_COLS = transactionItemUpdateCols();

    /**
     * Insert a new transaction or update it if it already exists
     * This uses Mysql's INSERT ON DUPLICATE KEY UPDATE
     * @param item
     */
    public void upsertItem(String txnIdentity, int idx, JsonNode item) {
        String sql = upsert(SCHEMA,
                TRANSACTION_ITEMS,
                INSERT_COLS,
                UPDATE_COLS
        );
        if ( logger.isDebugEnabled() ) {
            logger.debug("sql --> " + sql);
        }
        int updated = dbi.withHandle(handle -> {
            Update update = handle.createStatement(sql);
            prepareUpsert(update,txnIdentity,idx, item);
            return update.execute();
        });
        if ( logger.isDebugEnabled() ) {
            logger.debug("updated --> " + updated);
        }
    }

    private static List<String> transactionItemInsertCols() {
        List<String> insert = ImmutableList.of(ITEM_ID,TXN_ID,IDX,SERVICE,RELEVANT_DATE,
                DESCRIPTION,BASE_COST_AMOUNT,COST_AMOUNT_WITH_FEES,COST_CURRENCY);
        List<String> both = new ArrayList<>(insert);
        both.addAll(transactionItemUpdateCols());
        return ImmutableList.copyOf(both);
    }

    private static List<String> transactionItemUpdateCols() {
        return ImmutableList.of(PRICE_AMOUNT,PRICE_CURRENCY);
    }

    private void prepareUpsert(Update ps, String txnIdentity, int index, JsonNode item) throws SQLException {
        int idx = 0;

        // first set all the insert column values
        ps.bind(idx++,item.get("itemIdentity").asText());
        ps.bind(idx++,txnIdentity);
        ps.bind(idx++,index);
        ps.bind(idx++,item.get("service").asText());
        ps.bind(idx++, Date.valueOf(LocalDate.parse(item.get("relevantDate").asText())));
        ps.bind(idx++,item.get("description").asText());
        ps.bind(idx++,item.get("baseUnitCost").get("amount").asDouble());
        if ( item.hasNonNull("unitCostWithFees") ){
            ps.bind(idx++,item.get("unitCostWithFees").get("amount").asDouble());
        }
        else {
            ps.bind(idx++,(Double) null);
        }
        ps.bind(idx++,item.get("unitCostWithFees").get("currency").asText());
        idx = commonPrep(ps, idx, item);

        // now set all the update columns in the case that the txn is already in the table
        idx = commonPrep(ps, idx, item);
    }

    private int commonPrep(Update ps, int idx, JsonNode item) throws SQLException {
        if ( item.hasNonNull("unitPrice") ){
            ps.bind(idx++,item.get("unitPrice").get("amount").asDouble());
            ps.bind(idx++,item.get("unitPrice").get("currency").asText());
        }
        else {
            ps.bind(idx++,(Double) null);
            ps.bind(idx++, (String) null);
        }
        return idx;
    }

}
