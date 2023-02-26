package com.kgromov.model;

import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
public class ColumnMetadata {
    private final String name;
    private final String type;
    private final Set<Constraint> constrains;
}
