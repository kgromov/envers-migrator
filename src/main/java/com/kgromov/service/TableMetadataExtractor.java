package com.kgromov.service;

import com.kgromov.config.DataSourceSettings;
import com.kgromov.model.ColumnMetadata;
import com.kgromov.model.TableMetadata;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static java.util.Optional.ofNullable;

@Slf4j
public class TableMetadataExtractor {
    private static final String COLUMNS_QUERY = "SELECT * FROM information_schema.COLUMNS \n" +
            "WHERE table_schema='%s' \n" +
            "AND table_name='%s'";

    public TableMetadata getTableMetadata(DataSourceSettings settings, String tableName) {
        try (Connection connection = DriverManager.getConnection(settings.getConnectionString(), settings.getUser(), settings.getPassword())) {
            return extractWithQuery(settings, tableName, connection);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private TableMetadata extractWithQuery(DataSourceSettings settings, String tableName, Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(String.format(COLUMNS_QUERY, settings.getDbName(), tableName))) {
            List<ColumnMetadata> columns = new ArrayList<>();
            while (resultSet.next()) {
                boolean isPK = ofNullable(resultSet.getString("COLUMN_KEY")).map(key -> key.equals("PRI")).orElse(false);
                String columnName = resultSet.getString("COLUMN_NAME");
                String datatype = resultSet.getString("DATA_TYPE");
                String columnType = resultSet.getString("COLUMN_TYPE");
                String isNullable = resultSet.getString("IS_NULLABLE");

                ColumnMetadata baseColumn = ColumnMetadata.builder()
                        .name(columnName)
                        .type(columnType)
                        .primaryKey(isPK)
                        .build();
                columns.add(baseColumn);

                if (!isPK) {
                    ColumnMetadata auditColumn = ColumnMetadata.builder()
                            .name(columnName.replace("_id", "") + "Modified")
                            .type("boolean")
                            .modifiedColumn(true)
                            .build();
                    columns.add(auditColumn);
                }
            }
            TableMetadata tableMetadata = TableMetadata.builder()
                    .name(tableName + "Audit")
                    .sourceName(tableName)
                    .columns(columns)
                    .build();
            return tableMetadata;
        }
    }

    private TableMetadata extractWithMetadata(DataSourceSettings settings, String tableName, Connection connection) throws SQLException {
        DatabaseMetaData metaData = connection.getMetaData();
        try (ResultSet resultSet = metaData.getColumns(null, settings.getDbName(), tableName, null)) {
         /*   ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
            int columnCount = resultSetMetaData.getColumnCount();
            List<String> columnNames = Stream.iterate(0, i -> i)
                    .limit(columnCount - 1)
                    .map(i -> getColumnName(resultSetMetaData, i))
                    .collect(Collectors.toList());
            log.info("ALl column names: {} from metadata", columnNames);
          */
            List<ColumnMetadata> columns = new ArrayList<>();
            while (resultSet.next()) {
                String columnName = resultSet.getString("COLUMN_NAME");
                String columnSize = resultSet.getString("COLUMN_SIZE");
                String datatype = resultSet.getString("DATA_TYPE");
                String isNullable = resultSet.getString("IS_NULLABLE");
                String isAutoIncrement = resultSet.getString("IS_AUTOINCREMENT");

                ColumnMetadata column = ColumnMetadata.builder()
                        .name(columnName)
                        .type(datatype)
                        .build();
                columns.add(column);
            }
            return TableMetadata.builder()
                    .name(tableName)
                    .columns(columns)
                    .build();
        }
    }

    private static String getColumnName(ResultSetMetaData resultSetMetaData, Integer columnIndex) {
        try {
            return resultSetMetaData.getColumnName(columnIndex);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
