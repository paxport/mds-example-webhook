package com.paxport.mdswebhook.db;

import com.zaxxer.hikari.HikariDataSource;
import org.skife.jdbi.v2.DBI;

import javax.sql.DataSource;


public class DBConnections {

    public DataSource dataSource(){
        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl("jdbc:mysql://localhost:3306/mds");
        ds.setUsername("mds");
        ds.setPassword("mds");
        return ds;
    }


    public DBI dbi () {
        return new DBI(dataSource());
    }

}
