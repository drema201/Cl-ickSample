package com.drema201;

/*
CREATE TABLE visits2
(
    VisitDate Date,
    EventType Nullable(String),
    UserID    Nullable(String)
 )
ENGINE = MergeTree()
PARTITION BY (toDate(VisitDate), EventType)
ORDER BY (EventType, VisitDate, intHash32(UserID));
*/


import com.clickhouse.jdbc.ClickHouseDataSource;

import java.sql.*;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

public class JDBCDriverMainVisit2 {
    public static final String HOST = "34.121.165.22";

    public static void main(String[] args) throws ExecutionException, InterruptedException, SQLException {
        String url = "jdbc:ch://" + HOST + "/default";
        Properties properties = new Properties();
// optionally set connection properties
        properties.setProperty("client_name", "Agent #2");


        ClickHouseDataSource dataSource = new ClickHouseDataSource(url, properties);
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("select VisitDate,count(1) from visits2 group by VisitDate")) {
            while (rs.next()) {
                System.out.println(" " + rs.getDate(1) + " " + rs.getInt(2));
            }
            conn.setAutoCommit(false);
            PreparedStatement insStatement = conn.prepareStatement("INSERT INTO visits2 (VisitDate, EventType, UserID) VALUES (addDays(toDate('1970-01-01'), ?), ?, generateUUIDv4())");
            for (int ki=1;ki<5000; ki++) {
//                insStatement.setDate(1,new Date(ki*1000000));
                insStatement.setInt(1,(ki/10000)+20000);
                String type ="";
                switch (ki/3) {case 0: type="TYpe1"; case 1: type="TYpe2"; case 2: type="TYpe2"; }
                insStatement.setString(2,type);
                insStatement.addBatch();
            }
            int[] res = insStatement.executeBatch();
            conn.commit();
        }
    }

}
