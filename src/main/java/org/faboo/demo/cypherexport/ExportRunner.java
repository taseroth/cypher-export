package org.faboo.demo.cypherexport;

import org.apache.commons.cli.*;
import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Config;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;

public class ExportRunner {


    public static void main(String[] args) {

        Options options = null;
        try {
            options = new Options();

            options.addOption(Option.builder("url").hasArg().required(true).desc("URL of the Neo4j installation").build());
            options.addOption(Option.builder("user").hasArg().required().desc("username for authentication").build());
            options.addOption(Option.builder("passwd").hasArg().required().desc("password for authentication").build());
            options.addOption(Option.builder("file").hasArg().required().desc("filename to stream Cypher to").build());
            options.addOption(Option.builder("cypherFormat").hasArg().desc("cypher format to use, can be 'create', 'updateAll, 'addStructure' or 'updateStructure'. Defaults to 'updateAll'").build());
            options.addOption(Option.builder("format").hasArg().desc("export format to use, can be 'cypher-shell', 'neo4j-shell', 'plain'. Defaults to 'plain'").build());
            CommandLineParser parser = new DefaultParser();
            CommandLine line = parser.parse(options, args);

            try (Driver driver = GraphDatabase.driver(
                        line.getOptionValue("url"),
                        AuthTokens.basic(line.getOptionValue("user"), line.getOptionValue("passwd")))) {

                Exporter exporter = new Exporter(driver);
                exporter.exportTo(line.getOptionValue("file"),
                        line.getOptionValue("cypherFormat", "updateAll"),
                        line.getOptionValue("format", "plain"));
            }

        } catch (ParseException e) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp(ExportRunner.class.getSimpleName(), options);
        }
    }



}
