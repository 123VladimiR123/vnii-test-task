package com.example.vniitesttask.triggers;

import lombok.extern.slf4j.Slf4j;
import org.h2.tools.TriggerAdapter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

@Slf4j
public class ReCountDetailSumTrigger extends TriggerAdapter {

    /*
        Triggers on details update, insert, delete
        counts new summary cost for master table
     */
    @Override
    public void fire(Connection connection, ResultSet oldResultSet, ResultSet newResultSet) throws SQLException {

        // counts sum if detail row was deleted
        if (oldResultSet != null) {
            fireSumUpdater(connection, oldResultSet);
        }

        // counts if detail was created or updated
        if (newResultSet != null) {
            fireSumUpdater(connection, newResultSet);
        }

    }

    private void fireSumUpdater(Connection connection, ResultSet resultSet) throws SQLException {

        // for non-duplicate queries
        Set<Long> masterNumberSet = new HashSet<>();

        // gets all unique master numbers from result set
        while (resultSet.next()) {
            masterNumberSet.add(resultSet.getLong("master_doc_number"));
        }

        // for every master number from set
        // executes query with counting sum of costs for current master number
        for (Long e : masterNumberSet) {
            log.info("Trying to update master #" + e + " sum");
            PreparedStatement statement = connection.prepareStatement("UPDATE master " +
                    "SET cost_value_sum = " +
                    "COALESCE((SELECT SUM(cost_value) FROM detail WHERE master_doc_number = ? GROUP BY master_doc_number), 0) WHERE doc_number = ?");

            statement.setLong(1, e);
            statement.setLong(2, e);

            statement.executeUpdate();

            log.info("Sum of master #" + e + " updated");
        }
    }
}
