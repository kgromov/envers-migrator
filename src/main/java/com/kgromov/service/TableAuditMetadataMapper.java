package com.kgromov.service;

import com.kgromov.model.ColumnMetadata;
import com.kgromov.model.TableMetadata;

import java.util.ArrayList;
import java.util.List;

public class TableAuditMetadataMapper {

    public TableMetadata mapFromTable(TableMetadata tableMetadata) {
        List<ColumnMetadata> columns = new ArrayList<>();
        tableMetadata.getColumns().forEach(column -> {
            columns.add(column);
            ColumnMetadata modifiedColumn = mapFromSourceColumn(column);
            columns.add(modifiedColumn);
        });
        return TableMetadata.builder()
                .name(tableMetadata.getName() + "Audit")
                .columns(columns)
                .build();
    }

    public ColumnMetadata mapFromSourceColumn(ColumnMetadata columnMetadata) {
        return ColumnMetadata.builder()
                .name(columnMetadata.getName().replace("_id", "").concat("Modified"))
                .type("boolean")
                .build();
    }
}
