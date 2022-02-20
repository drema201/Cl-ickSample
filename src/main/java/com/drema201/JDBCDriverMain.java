package com.drema201;



import com.clickhouse.jdbc.ClickHouseDataSource;

import java.sql.*;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

public class JDBCDriverMain {
    public static final String HOST = "34.123.235.222";

    public static void main(String[] args) throws ExecutionException, InterruptedException, SQLException {
        String url = "jdbc:ch://" + HOST + "/shardonly";
        Properties properties = new Properties();
// optionally set connection properties
        properties.setProperty("client_name", "Agent #1");


        ClickHouseDataSource dataSource = new ClickHouseDataSource(url, properties);
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("select * from dist_test")) {
            while (rs.next()) {
                System.out.println("str = " + rs.getInt(1));
            }
            conn.setAutoCommit(false);
            PreparedStatement insStatement = conn.prepareStatement("INSERT INTO dist_test (id, event_time) VALUES (?, now())");
            for (int ki=1;ki<10000; ki++) {
                insStatement.setInt(1,ki);
                insStatement.addBatch();
            }
            int[] res = insStatement.executeBatch();
            conn.commit();
        }
    }

}
