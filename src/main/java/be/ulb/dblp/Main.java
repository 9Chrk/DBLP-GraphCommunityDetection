package be.ulb.dblp;

import be.ulb.dblp.task1.Runner;

import java.nio.file.Path;


/**
 * Entry point for the DBLP Community Analysis tool.
 *
 * <p>Usage:
 * <pre>
 *   java -jar dblp-community-analysis.jar &lt;dblp.xml.gz&gt; &lt;dblp.dtd&gt; [--reportEvery=100000] [--outputDir=results/task1]
 * </pre>
 */
public class Main {

    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("Usage: java -jar dblp-community-analysis.jar <dblp.xml.gz> <dblp.dtd> [--reportEvery=100000] [--limit=1000000] [--outputDir=results/task1]");
            System.exit(1);
        }

        Path xmlPath = Path.of(args[0]);
        Path dtdPath = Path.of(args[1]);

        // Constantes par défaut
        long limit = Long.MAX_VALUE;
        int reportEvery = 100000;
        Path outputDir = Path.of("results", "task1");

        for (int i = 2; i < args.length; i++) {
            String arg = args[i];

            if (arg.startsWith("--reportEvery=")) {
                reportEvery = Integer.parseInt(arg.substring("--reportEvery=".length()));
            }
            else if (arg.startsWith("--limit=")) {
                limit = Long.parseLong(arg.substring("--limit=".length()));
            }
            else if (arg.startsWith("--outputDir=")) {
                outputDir = Path.of(arg.substring("--outputDir=".length()));
            }
        }

        System.setProperty("jdk.xml.entityExpansionLimit", "0");
        System.setProperty("jdk.xml.totalEntitySizeLimit", "0");
        System.setProperty("jdk.xml.maxGeneralEntitySizeLimit", "0");
        System.setProperty("jdk.xml.maxParameterEntitySizeLimit", "0");

        System.out.println("DBLP Community Analysis - Task 1");
        System.out.println("  XML        : " + xmlPath);
        System.out.println("  DTD        : " + dtdPath);
        System.out.println("  reportEvery: " + reportEvery);
        System.out.println("  limit      : " + limit);
        System.out.println("  outputDir  : " + outputDir);
        System.out.println();

        try {
            Runner.run(xmlPath, dtdPath, outputDir, reportEvery, limit);
            System.out.println("Done.");
        }
        catch (Exception e) {
            System.err.println("Task 1 failed: " + e.getMessage());
            e.printStackTrace(System.err);
            System.exit(2);
        }
    }
}
