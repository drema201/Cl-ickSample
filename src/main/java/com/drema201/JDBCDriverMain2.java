package com.drema201;



import com.clickhouse.jdbc.ClickHouseDataSource;

import java.sql.*;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

public class JDBCDriverMain2 {
    public static final String HOST = "34.123.235.222";

    public static void main(String[] args) throws ExecutionException, InterruptedException, SQLException {
        String url = "jdbc:ch://" + HOST + "/default";
        Properties properties = new Properties();
// optionally set connection properties
        properties.setProperty("client_name", "Agent #1");


        ClickHouseDataSource dataSource = new ClickHouseDataSource(url, properties);
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("select VisitDate,count(1) from visits group by VisitDate")) {
            while (rs.next()) {
                System.out.println(" " + rs.getDate(1) + " " + rs.getInt(2));
            }
            conn.setAutoCommit(false);
            PreparedStatement insStatement = conn.prepareStatement("INSERT INTO visits (VisitDate, Hour, ClientID) VALUES (addDays(toDate('1970-01-01'), ?), 1, generateUUIDv4())");
            for (int ki=1;ki<500000; ki++) {
//                insStatement.setDate(1,new Date(ki*1000000));
                insStatement.setInt(1,(ki/10000)+5000);
                insStatement.addBatch();
            }
            int[] res = insStatement.executeBatch();
            conn.commit();
        }
    }

}
