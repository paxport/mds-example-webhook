package com.paxport.mdswebhook.db;

import com.zaxxer.hikari.HikariDataSource;
import org.skife.jdbi.v2.DBI;
import static com.paxport.mdswebhook.Config.envOrSysProp;

import javax.sql.DataSource;

/**
 * Defines which DB we connect to
 *
 * Update this to point to your own mysql instance if you would like to
 *
 */
public class DBConnections {

    private final static String DB_URL = envOrSysProp("DB_URL", "jdbc:mysql://localhost:3306/mds");
    private final static String DB_USER = envOrSysProp("DB_USER", "mds");
    private final static String DB_PASSWORD = envOrSysProp("DB_PASSWORD", "mds");

    public static DataSource dataSource(){
        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl(DB_URL);
        ds.setUsername(DB_USER);
        ds.setPassword(DB_PASSWORD);
        return ds;
    }

    public static DBI dbi () {
        return new DBI(dataSource());
    }



}
