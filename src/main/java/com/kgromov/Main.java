package com.kgromov;

import com.kgromov.config.CommandLineFactory;
import com.kgromov.config.DataSourceSettings;
import com.kgromov.model.TableMetadata;
import com.kgromov.service.ChangelogWriter;
import com.kgromov.service.LiquibaseService;
import com.kgromov.service.TableMetadataExtractor;
import org.apache.commons.cli.CommandLine;
import org.w3c.dom.Document;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class Main {
    public static void main(String[] args) {
        args = new String[]{"-t=reviews"};
        CommandLine commandLine = CommandLineFactory.buildCommandLine(args);
        String[] tableNames = commandLine.getOptionValue("table").split(",\\s*");

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            DataSourceSettings settings = DataSourceSettings.builder()
                    .uri(System.getenv("DB_URI"))
                    .dbName(System.getenv("DB_NAME"))
                    .user(System.getenv("DB_USER"))
                    .password(System.getenv("DB_PASSWORD"))
                    .build();
            TableMetadata tableMetadata = new TableMetadataExtractor().getTableMetadata(settings, tableNames[0]);
            LiquibaseService liquibaseService = new LiquibaseService();
            URL templateURI = Thread.currentThread().getContextClassLoader().getResource("migration-template.xml");
            Path templatePath = Path.of(templateURI.toURI());
            String changeLogFileName = "add" + tableMetadata.getName().substring(0, 1).toUpperCase() + tableMetadata.getName().substring(1) + "Audit.xml";
            Path changeLogPath = Paths.get(".").normalize()
                    .resolve("output")
                    .resolve("changelogs")
                    .resolve(changeLogFileName);
            Files.copy(templatePath, changeLogPath, REPLACE_EXISTING);
            Document changeLog = liquibaseService.buildCreateAuditTableChangeSet(tableMetadata, changeLogPath);
            ChangelogWriter changelogWriter = new ChangelogWriter();
            changelogWriter.writeToChangelogFile(changeLog, changeLogPath);
        } catch (ClassNotFoundException | URISyntaxException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
