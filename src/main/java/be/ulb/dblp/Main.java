package be.ulb.dblp;

import java.nio.file.Path;


/**
 * Point d'entrée de l'application d'analyse de la communauté DBLP.
 *
 * <p>Usage:
 * <pre>
 *   java -jar dblp-community-analysis.jar <dblp.xml.gz> <dblp.dtd> [--task=1|2] [--reportEvery=100000] [--limit=1000000] [--outputDir=results/task1]
 * </pre>
 */
public class Main {

    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("Usage: java -jar dblp-community-analysis.jar <dblp.xml.gz> <dblp.dtd> [--task=1|2] [--reportEvery=100000] [--limit=1000000] [--outputDir=results/task1]");
            System.exit(1);
        }

        // Data (dblp)
        Path xmlPath = Path.of(args[0]);
        Path dtdPath = Path.of(args[1]);

        // Paramètres d'exécution par défaut
        int task = 1;
        long limit = Long.MAX_VALUE;
        int reportEvery = 100000;
        Path outputDir = Path.of("results", "task1");

        // Récupération des paramètres d'exécution optionnels
        for (int i = 2; i < args.length; i++) {
            String arg = args[i];

            if (arg.startsWith("--task=")) {
                task = Integer.parseInt(arg.substring("--task=".length()));
            }
            else if (arg.startsWith("--reportEvery=")) {
                reportEvery = Integer.parseInt(arg.substring("--reportEvery=".length()));
            }
            else if (arg.startsWith("--limit=")) {
                limit = Long.parseLong(arg.substring("--limit=".length()));
            }
            else if (arg.startsWith("--outputDir=")) {
                outputDir = Path.of(arg.substring("--outputDir=".length()));
            }
        }

        if (task == 2 && Path.of("results", "task1").equals(outputDir)) {
            outputDir = Path.of("results", "task2");
        }

        // Supression des limites d'expansion d'entités XML pour éviter les erreurs de parsing sur le fichier DBLP.
        System.setProperty("jdk.xml.entityExpansionLimit", "0");
        System.setProperty("jdk.xml.totalEntitySizeLimit", "0");
        System.setProperty("jdk.xml.maxGeneralEntitySizeLimit", "0");
        System.setProperty("jdk.xml.maxParameterEntitySizeLimit", "0");

        // Affichage des paramètres d'exécution
        System.out.println("DBLP Community Analysis - Task " + task);
        System.out.println("  XML        : " + xmlPath);
        System.out.println("  DTD        : " + dtdPath);
        System.out.println("  reportEvery: " + reportEvery);
        System.out.println("  limit      : " + limit);
        System.out.println("  outputDir  : " + outputDir);
        System.out.println();

        // Lancement des tâches :
        try {
            if (task == 1) {
                be.ulb.dblp.task1.Runner.run(xmlPath, dtdPath, outputDir, reportEvery, limit);
            }
            else if (task == 2) {
                be.ulb.dblp.task2.Runner.run(xmlPath, dtdPath, outputDir, reportEvery, limit);
            }
            else {
                System.err.println("Unsupported task: " + task + " (expected 1 or 2)");
                System.exit(1);
            }

            System.out.println("Done.");
        }
        catch (Exception e) {
            System.err.println("Task " + task + " failed: " + e.getMessage());
            e.printStackTrace(System.err);
            System.exit(2);
        }
    }
}
