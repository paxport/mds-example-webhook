package com.paxport.mdswebhook;


import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.IOException;

public class PersistTransactionsHandlerTest {

    private PersistTransactionsHandler handler = new PersistTransactionsHandler();

    @Test
    public void incomingOrderTest() throws IOException {
        String orderJson = IOUtils.toString(
                PersistTransactionsHandlerTest.class.getResourceAsStream("example-order.json")
        );

        handler.handleIncomingOrder(orderJson);
    }

}
