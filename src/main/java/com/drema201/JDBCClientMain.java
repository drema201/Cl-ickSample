package com.drema201;

import com.clickhouse.client.*;

import java.util.concurrent.ExecutionException;

public class JDBCClientMain {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ClickHouseProtocol preferredProtocol = ClickHouseProtocol.HTTP;
// you'll have to parse response manually if use different format
        ClickHouseFormat preferredFormat = ClickHouseFormat.RowBinaryWithNamesAndTypes;

// connect to localhost, use default port of the preferred protocol
        ClickHouseNode server = ClickHouseNode.builder().host("35.225.242.102").port(preferredProtocol).build();

        try (ClickHouseClient client = ClickHouseClient.newInstance(preferredProtocol);
             ClickHouseResponse response = client.connect(server)
                     .format(preferredFormat)
                     .query("select * from numbers(:limit)")
                     .params(1000).execute().get()) {
            // or resp.stream() if you prefer stream API
            for (ClickHouseRecord record : response.records()) {
                int num = record.getValue(0).asInteger();
                String str = record.getValue(0).asString();
                System.out.println("str = " + str);
            }

            ClickHouseResponseSummary summary = response.getSummary();
            long totalRows = summary.getReadRows();
        }
    }

}
