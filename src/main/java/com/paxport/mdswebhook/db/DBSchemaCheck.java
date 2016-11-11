package com.paxport.mdswebhook.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Check that the DB Schema is up to data and if it is not then update it
 * by executing any missing schema scripts
 */
public class DBSchemaCheck {

    private final static Logger logger = LoggerFactory.getLogger(DBSchemaCheck.class);

    private JdbcTemplate template;

    @Autowired
    public DBSchemaCheck(JdbcTemplate template){
        this.template = template;
    }

    public DBSchemaCheck(DataSource dataSource) {
        this(new JdbcTemplate(dataSource));
    }

    public void ensureSchemaIsUptoDate(){
        int currentLevel = getCurrentSchemaLevel();

        List<Resource> scriptsToRun = findSchemaFilesAndOrderThem().stream()
                .filter(f -> getLevelFromFilename(f.getFilename()) > currentLevel)
                .collect(Collectors.toList());

        if ( !scriptsToRun.isEmpty() ) {
            try {
                try (Connection con =  template.getDataSource().getConnection() ){
                    for (Resource script : scriptsToRun) {
                        logger.info("About to execute the following schema update script: " + script.getFilename() );
                        ScriptUtils.executeSqlScript(con,script);
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private int getLevelFromFilename(String filename) {
        int start = filename.indexOf('_') + 1;
        int end = filename.indexOf('.');
        return Integer.parseInt(filename.substring(start,end));
    }

    public int getCurrentSchemaLevel(){
        String sql = "SELECT max(level) FROM mds.schema_level";
        logger.info("sql: " + sql);
        int level = 0;
        try {
            level = template.queryForObject(sql,Integer.class);
            logger.info("Schema is at level " + level);
        } catch (DataAccessException e) {
            logger.info("no schema found probs: " + e.getMessage());
        }
        return level;
    }

    public List<Resource> findSchemaFilesAndOrderThem(){
        try {
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            Resource[] files = resolver.getResources("classpath:com/paxport/mds/db/schema/schema_*.sql");
            List<Resource> list = Arrays.asList(files);
            Collections.sort(list, (o1, o2) -> o1.getFilename().compareTo(o2.getFilename()));
            return list;
        } catch (IOException e) {
            throw new RuntimeException("failed to find mds schema files", e);
        }
    }

    public static void main (String[] args){
        if ( args.length != 3 ) {
            System.out.println("usage for DBSchemaCheck: <jdbc_url> <user> <pass>");
            System.exit(1);
        }
        try {
            SingleConnectionDataSource ds = new SingleConnectionDataSource(args[0],args[1],args[2],true);
            DBSchemaCheck check = new DBSchemaCheck(ds);
            check.ensureSchemaIsUptoDate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
