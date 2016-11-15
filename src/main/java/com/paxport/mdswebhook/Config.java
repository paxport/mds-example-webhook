package com.paxport.mdswebhook;


import com.paxport.mdswebhook.db.DBConnections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Config {


    private final static Logger logger = LoggerFactory.getLogger(Config.class);

    public static String envOrSysProp(String key, String defaultValue) {
        String var = System.getenv(key);
        if ( var == null ) {
            var =  System.getProperty(key,defaultValue);
        }
        logger.info("Using " + key + " --> " + var);
        return var;
    }

}
