package com.kgromov.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class TableMetadata {
    private final String name;
    private final String sourceName;
    private final List<ColumnMetadata> columns;
}
