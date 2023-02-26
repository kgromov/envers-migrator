package com.kgromov.service;

import com.kgromov.model.ColumnMetadata;
import com.kgromov.model.TableMetadata;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.nio.file.Path;

@Slf4j
public class LiquibaseService {

    @SneakyThrows
    public Document buildCreateAuditTableChangeSet(TableMetadata auditTableMetadata, Path changelogFile) {
        Document document = new ChangelogReader().readChangelog(changelogFile);
        Element databaseChangeLog = document.getDocumentElement();
        Element databaseChangeLog2 = (Element) document.getElementsByTagName("databaseChangeLog").item(0);

        Element changeSet = document.createElement("changeSet");
        changeSet.setAttribute("id", "Create " + auditTableMetadata.getName() + " table");
        changeSet.setAttribute("author", "");

        Element createTable = document.createElement("createTable");
        changeSet.setAttribute("name", auditTableMetadata.getName());

        auditTableMetadata.getColumns()
                .stream()
                .map(columnMetadata -> createColumnElement(document, columnMetadata))
                .forEach(createTable::appendChild);

        changeSet.appendChild(createTable);
        databaseChangeLog.appendChild(changeSet);
        return document;
    }

    public Document buildInitRevisionForAuditTableChangeSet(TableMetadata auditTableMetadata, Path changelogFile) {
        Document document = new ChangelogReader().readChangelog(changelogFile);
        Element databaseChangeLog = document.getDocumentElement();

        Element changeSet = document.createElement("changeSet");
        changeSet.setAttribute("id", "Add init revision for " + auditTableMetadata.getName() + " table");
        changeSet.setAttribute("author", "");

        Element sqlElement = document.createElement("sql");
        // TODO: build insert query
        String insertQuery = "";
        sqlElement.setTextContent(insertQuery);
        changeSet.appendChild(sqlElement);
        databaseChangeLog.appendChild(changeSet);
        return document;
    }

    private Element createColumnElement(Document document, ColumnMetadata columnMetadata) {
        Element column = document.createElement("column");
        column.setAttribute("name", columnMetadata.getName());
        column.setAttribute("type", columnMetadata.getType());
        return column;
    }
}
