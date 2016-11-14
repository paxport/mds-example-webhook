package com.paxport.mdswebhook.db;

import com.zaxxer.hikari.HikariDataSource;
import org.skife.jdbi.v2.DBI;

import javax.sql.DataSource;

/**
 * Defines which DB we connect to
 *
 * Update this to point to your own mysql instance if you would like to
 *
 */
public class DBConnections {

    public static DataSource dataSource(){
        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl("jdbc:mysql://localhost:3306/mds");
        ds.setUsername("mds");
        ds.setPassword("mds");
        return ds;
    }


    public static DBI dbi () {
        return new DBI(dataSource());
    }

}
