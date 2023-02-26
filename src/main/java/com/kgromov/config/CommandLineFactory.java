package com.kgromov.config;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.cli.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommandLineFactory {

    public static CommandLine buildCommandLine(String... args) {
        Options options = new Options();

        Option tableParam = new Option("t", "table", true, "comma separated list of table names");
        tableParam.setRequired(true);
        options.addOption(tableParam);

        Option entityParam = new Option("e", "entity", true, "comma separated list of entity names");
        entityParam.setRequired(false);
        options.addOption(entityParam);

        Option packageParam = new Option("p", "package", true, "full qualified package name to scan entities");
        packageParam.setRequired(false);
        options.addOption(packageParam);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();

        try {
            return parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("", options);
            throw new RuntimeException();
        }

    }
}
