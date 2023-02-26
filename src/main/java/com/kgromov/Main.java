package com.kgromov;

import com.kgromov.config.CommandLineFactory;
import org.apache.commons.cli.CommandLine;

public class Main {
    public static void main(String[] args) {
        CommandLine commandLine = CommandLineFactory.buildCommandLine(args);
        String[] tableNames = commandLine.getOptionValue("table").split(",");

    }
}
