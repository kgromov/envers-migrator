package com.kgromov.service;

import com.kgromov.model.ColumnMetadata;
import com.kgromov.model.TableMetadata;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import static java.util.stream.Collectors.joining;

@Slf4j
@RequiredArgsConstructor
public class LiquibaseService {
    private static final String INSERT_AS_MODIFIED_PLACEHOLDER = "%s, (%s IS NOT NULL) AS %s";
    private final TableMetadata auditTableMetadata;

    // TODO: decouple classes:
    //  LiquibaseService = varargs of changeLog transformations;
    //  ChangeSetBuilder/Appender with e.g. RevisionChangeSetBuilder implementation
    @SneakyThrows
    public Document buildCreateAuditTableChangeSet(Document changeLog) {
        Element databaseChangeLog = changeLog.getDocumentElement();
        Element changeSet = changeLog.createElement("changeSet");
        changeSet.setAttribute("id", "Create " + auditTableMetadata.getName() + " table");
        changeSet.setAttribute("author", "");

        Element createTable = changeLog.createElement("createTable");
        changeSet.setAttribute("name", auditTableMetadata.getName());

        auditTableMetadata.getColumns()
                .stream()
                .map(columnMetadata -> createColumnElement(changeLog, columnMetadata))
                .forEach(createTable::appendChild);

        changeSet.appendChild(createTable);
        databaseChangeLog.appendChild(changeSet);
        return changeLog;
    }

    public Document buildInitRevisionForAuditTableChangeSet(Document changeLog) {
        Element databaseChangeLog = changeLog.getDocumentElement();

        Element changeSet = changeLog.createElement("changeSet");
        changeSet.setAttribute("id", "Add init revision for " + auditTableMetadata.getName() + " table");
        changeSet.setAttribute("author", "");

        Element sqlElement = changeLog.createElement("sql");
        String insertQuery = buildInsertQuery(auditTableMetadata).toString();
        log.info("Add init revision query = {}", insertQuery);
        sqlElement.setTextContent(insertQuery);
        changeSet.appendChild(sqlElement);
        databaseChangeLog.appendChild(changeSet);
        return changeLog;
    }

    public Document buildPKForAuditTableChangeSet(Document changeLog) {
        return changeLog;
    }

    private StringBuilder buildInsertQuery(TableMetadata auditTableMetadata) {
        return new StringBuilder()
                .append("INSERT INTO ")
                .append(auditTableMetadata.getName())
                .append('(')
                .append(auditTableMetadata.getColumns().stream().map(ColumnMetadata::getName).collect(joining(", ")))
                .append(')')
                .append(" SELECT ")
                .append(auditTableMetadata.getColumns()
                        .stream()
                        .filter(columnMetadata -> !columnMetadata.isModifiedColumn())
                        .map(column -> {
                            String columnName = column.getName();
                            if (column.isPrimaryKey()) {
                                return columnName;
                            }
                            String modifiedColumnName = columnName.replace("_id", "") + "Modified";
                            return String.format(INSERT_AS_MODIFIED_PLACEHOLDER, columnName, columnName, modifiedColumnName);
                        })
                        .collect(joining(", "))
                )
                .append(" FROM ").append(auditTableMetadata.getSourceName());
    }

    private Element createColumnElement(Document document, ColumnMetadata columnMetadata) {
        Element column = document.createElement("column");
        column.setAttribute("name", columnMetadata.getName());
        column.setAttribute("type", columnMetadata.getType());
        return column;
    }
}
