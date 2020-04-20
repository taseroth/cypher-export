package org.faboo.demo.cypherexport;


import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.StatementResult;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class Exporter {

    private static final String OPTIONS = "{streamStatements:true,batchSize:500,cypherFormat:'%s', format:'%s'}";
    public static final String STATEMENT = "call apoc.export.cypher.all(null,%s) yield cypherStatements";
    private final Driver driver;

    public Exporter(Driver driver) {
        this.driver = driver;
    }


    public void exportTo(String fileName, String cypherFormat, String format) {

        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(fileName), StandardCharsets.UTF_8))) {

            try(Session session = driver.session()) {

                int resultSets = session.readTransaction(tx -> {
                    String query = String.format(STATEMENT, String.format(OPTIONS, cypherFormat, format));
                    System.out.println("running the following query:\n" + query);
                    StatementResult result = tx.run(query);
                    int cnt = 0;
                    while (result.hasNext()) {
                        try {
                            writer.write(result.next().get("cypherStatements").asString());
                            writer.newLine();
                            cnt++;
                            System.out.print( "\r" + "*".repeat(cnt));
                        } catch (IOException e) {
                            System.err.println("error writing to file: "+ e.getMessage() );
                            return 0;
                        }
                    }
                    return cnt;
                });
                System.out.println();
                System.out.printf("exported %d result sets to file %s\n", resultSets, fileName);
            }

        } catch (IOException e) {
            System.err.println("error writing to file: "+ e.getMessage() );
        }

    }
}
