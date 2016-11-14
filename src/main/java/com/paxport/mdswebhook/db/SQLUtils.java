package com.paxport.mdswebhook.db;


import java.util.List;

public class SQLUtils {

    public static String insertInto(String schema, String tableName, String... columns) {

        StringBuilder builder = new StringBuilder("INSERT INTO ").append(getTableName(schema, tableName)).append(" (");
        for (String column : columns) {
            builder.append(column).append(',');
        }
        builder.deleteCharAt(builder.length()-1).append(") VALUES (");
        for (String column : columns) {
            builder.append("?,");
        }
        builder.deleteCharAt(builder.length()-1).append(");");
        return builder.toString();
    }

    public static String update(String schema, String tableName, String whereColumn, String... columns) {
        StringBuilder builder = new StringBuilder("UPDATE ").append(getTableName(schema, tableName)).append(" SET ");
        for (String column : columns) {
            builder.append(column).append("=?,");
        }
        return builder.deleteCharAt(builder.length()-1).append(" WHERE ").append(whereColumn).append("=?;").toString();
    }

    public static String upsert(String schema, String tableName, List<String> insertColumns, List<String> updateColumns) {
        StringBuilder builder = new StringBuilder("INSERT INTO ").append(getTableName(schema, tableName)).append(" (");
        for (String column : insertColumns) {
            builder.append(column).append(',');
        }
        builder.deleteCharAt(builder.length()-1).append(") VALUES (");
        for (String column : insertColumns) {
            builder.append("?,");
        }
        builder.deleteCharAt(builder.length()-1).append(") ON DUPLICATE KEY UPDATE ");
        for (String column : updateColumns) {
            builder.append(column).append(" = ? ,");
        }
        builder.deleteCharAt(builder.length()-1).append(";");
        return builder.toString();
    }

    public static String selectWhere(String schema, String tableName, String operator, String... columns) {
        StringBuilder builder = new StringBuilder("SELECT  * FROM ").append(getTableName(schema, tableName)).append(" WHERE ");
        for (String column : columns) {
            builder.append(column).append(" = ? ").append(operator);
        }
        return builder.toString();
    }

    public static String setParameters(String sql, String... params) {
        for (String param : params) {
            sql = sql.replaceFirst("\\?","'" + param + "'");
        }
        return sql;
    }

    public static String getTableName(String schema, String tableName) {
        StringBuilder builder = new StringBuilder();
        if (schema != null && !schema.trim().isEmpty()) {
            builder.append(schema).append('.');
        }
        return builder.append(tableName).toString();
    }

}
