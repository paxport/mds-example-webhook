package com.paxport.mdswebhook.db;

import org.apache.commons.io.IOUtils;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Script;
import org.skife.jdbi.v2.util.IntegerColumnMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

/**
 * Check that the DB Schema is up to data and if it is not then update it
 * by executing any missing schema scripts
 */
public class DBSchemaCheck {

    private final static Logger logger = LoggerFactory.getLogger(DBSchemaCheck.class);

    private DBI dbi;

    public DBSchemaCheck(DBI dbi){
        this.dbi = dbi;
    }

    public DBSchemaCheck ensureSchemaIsUptoDate(){
        int currentLevel = getCurrentSchemaLevel();

        List<String> scriptsToRun = findSchemaFilesAndOrderThem().stream()
                .filter(f -> getLevelFromFilename(f) > currentLevel)
                .collect(Collectors.toList());

        if ( !scriptsToRun.isEmpty() ) {
            try {
                for (String path : scriptsToRun) {
                    String scriptText = IOUtils.toString(DBSchemaCheck.class.getClassLoader().getResourceAsStream(path));
                    logger.info("About to execute SQL script:\n" + scriptText);
                    dbi.useHandle(handle -> {
                        Script script = handle.createScript(scriptText);
                        script.execute();
                    });
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return this;
    }

    private int getLevelFromFilename(String filename) {
        int start = filename.indexOf('_') + 1;
        int end = filename.indexOf('.');
        return Integer.parseInt(filename.substring(start,end));
    }

    public int getCurrentSchemaLevel(){
        String sql = "SELECT max(level) FROM mds.schema_level";
        logger.info("sql: " + sql);
        try {
            return dbi.withHandle(h -> h.createQuery("SELECT max(level) FROM mds.schema_level")
                    .map(IntegerColumnMapper.PRIMITIVE)
                    .first());
        } catch (Exception e) {
            if ( e.getMessage().contains("Table 'mds.schema_level' doesn't exist") ){
                logger.info("No schema table found so will create it...");
                return 0;
            }
            else {
                throw e;
            }
        }
    }

    public List<String> findSchemaFilesAndOrderThem(){
        try {

            final String resourceDir = "dbschema/";
            List<String> list = Arrays.asList(getResourceListing(DBSchemaCheck.class,resourceDir));
            list = list.stream().map(f -> resourceDir + f).collect(Collectors.toList());
            Collections.sort(list);
            return list;
        } catch (Exception e) {
            throw new RuntimeException("failed to find mds schema files", e);
        }
    }

    /**
     * List directory contents for a resource folder. Not recursive.
     * This is basically a brute-force implementation.
     * Works for regular files and also JARs.
     *
     * @author Greg Briggs
     * @param clazz Any java class that lives in the same place as the resources you want.
     * @param path Should end with "/", but not start with one.
     * @return Just the name of each member item, not the full paths.
     * @throws URISyntaxException
     * @throws IOException
     */
    String[] getResourceListing(Class clazz, String path) throws URISyntaxException, IOException {
        URL dirURL = clazz.getClassLoader().getResource(path);
        if (dirURL != null && dirURL.getProtocol().equals("file")) {
        /* A file path: easy enough */
            return new File(dirURL.toURI()).list();
        }

        if (dirURL == null) {
        /*
         * In case of a jar file, we can't actually find a directory.
         * Have to assume the same jar as clazz.
         */
            String me = clazz.getName().replace(".", "/")+".class";
            dirURL = clazz.getClassLoader().getResource(me);
        }

        if (dirURL.getProtocol().equals("jar")) {
        /* A JAR path */
            String jarPath = dirURL.getPath().substring(5, dirURL.getPath().indexOf("!")); //strip out only the JAR file
            JarFile jar = new JarFile(URLDecoder.decode(jarPath, "UTF-8"));
            Enumeration<JarEntry> entries = jar.entries(); //gives ALL entries in jar
            Set<String> result = new HashSet<>(); //avoid duplicates in case it is a subdirectory
            while(entries.hasMoreElements()) {
                String name = entries.nextElement().getName();
                if (name.startsWith(path)) { //filter according to the path
                    String entry = name.substring(path.length());
                    int checkSubdir = entry.indexOf("/");
                    if (checkSubdir >= 0) {
                        // if it is a subdirectory, we just return the directory name
                        entry = entry.substring(0, checkSubdir);
                    }
                    result.add(entry);
                }
            }
            return result.toArray(new String[result.size()]);
        }

        throw new UnsupportedOperationException("Cannot list files for URL "+dirURL);
    }

    public static void main (String[] args){
        try {
            DBI dbi = DBConnections.dbi();
            DBSchemaCheck check = new DBSchemaCheck(dbi);
            check.ensureSchemaIsUptoDate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
