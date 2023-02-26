package com.kgromov.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Constraint {
    private final String name;
    private final String sourceTable;
    private final String sourceColumn;
    private final String targetTable;
    private final String targetColumn;
}
