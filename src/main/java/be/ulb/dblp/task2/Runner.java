package be.ulb.dblp.task2;

import be.ulb.dblp.parsing.DblpPublicationGenerator;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;


/**
 * Exécute la Tâche 2 en traitement en flux, puis écrit les fichiers CSV de sortie.
 *
 * <p>Le runner lit le flux DBLP publication par publication, construit les compteurs
 * nécessaires pour les arêtes orientées, filtre ensuite le graphe selon le seuil demandé
 * et calcule les composantes fortement connexes ainsi que le diamètre des plus grandes.</p>
 */
public final class Runner {

    private static final int EDGE_THRESHOLD = 6;
    private static final int TOP_COMPONENT_COUNT = 10;

    private Runner() {
    }

    public static void run(Path xmlPath, Path dtdPath, Path outputDir, int reportEvery, long limit) throws Exception {
        Files.createDirectories(outputDir);

        long publicationCount = 0;
        PairCounter pairCounter = new PairCounter();

        // Le générateur DBLP lit le fichier XML en flux afin d'éviter de charger tout le jeu de données en mémoire.
        try (DblpPublicationGenerator generator = new DblpPublicationGenerator(xmlPath, dtdPath, 256)) {
            while (publicationCount < limit) {
                Optional<DblpPublicationGenerator.Publication> optional = generator.nextPublication();
                if (optional.isEmpty()) break;

                publicationCount++;
                pairCounter.processPublication(optional.get());

                if (reportEvery > 0 && publicationCount % reportEvery == 0) {
                    System.out.println("[Task2] publications=" + publicationCount);
                }
            }
        }

        // On transforme les comptes orientés en graphe filtré, puis on extrait les CFC.
        DirectedGraph graph = GraphBuilder.buildFilteredGraph(pairCounter.snapshot(), EDGE_THRESHOLD);
        List<Set<String>> components = Kosaraju.compute(graph);
        List<Result.ComponentSummary> top10 = computeTop10WithDiameter(graph, components);

        Result result = new Result(components, top10);

        // Chaque fichier CSV correspond à une vue différente du résultat final.
        writeComponentSizes(outputDir.resolve("task2_component_sizes.csv"), result.components());
        writeTop10(outputDir.resolve("task2_top10.csv"), result.top10());
        writeTop10Members(outputDir.resolve("task2_top10_members.csv"), result.top10());

        System.out.println("\nTask 2 completed after " + publicationCount + " publications.");
        System.out.println("Task 2 SCC count: " + result.components().size());
        System.out.println("Task 2 outputs written to: " + outputDir);
    }

    private static List<Result.ComponentSummary> computeTop10WithDiameter(DirectedGraph graph,
                                                                          List<Set<String>> components) {
        List<Result.ComponentSummary> sorted = new ArrayList<>(components.size());

        // On prépare d'abord des résumés sans diamètre pour pouvoir trier uniquement par taille.
        for (int i = 0; i < components.size(); i++) {
            Set<String> members = components.get(i);
            sorted.add(new Result.ComponentSummary(i, members.size(), -1, members));
        }

        sorted.sort((a, b) -> {
            int bySize = Integer.compare(b.size(), a.size());
            if (bySize != 0) return bySize;
            return Integer.compare(a.componentId(), b.componentId());
        });

        int topCount = Math.min(TOP_COMPONENT_COUNT, sorted.size());
        List<Result.ComponentSummary> top = new ArrayList<>(topCount);

        for (int i = 0; i < topCount; i++) {
            Result.ComponentSummary summary = sorted.get(i);
            // Le diamètre n'est calculé que pour les plus grandes composantes, car c'est la partie la plus coûteuse.
            int diameter = CommunityDiameter.compute(graph, summary.members());
            top.add(new Result.ComponentSummary(
                    summary.componentId(),
                    summary.size(),
                    diameter,
                    summary.members()
            ));
        }

        return top;
    }

    private static void writeComponentSizes(Path file, List<Set<String>> components) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
            writer.write("component_id,size");
            writer.newLine();

            for (int i = 0; i < components.size(); i++) {
                writer.write(i + "," + components.get(i).size());
                writer.newLine();
            }
        }
    }

    private static void writeTop10(Path file, List<Result.ComponentSummary> top10) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
            writer.write("rank,component_id,size,diameter");
            writer.newLine();

            for (int i = 0; i < top10.size(); i++) {
                Result.ComponentSummary summary = top10.get(i);
                writer.write((i + 1) + "," + summary.componentId() + "," + summary.size() + "," + summary.diameter());
                writer.newLine();
            }
        }
    }

    private static void writeTop10Members(Path file, List<Result.ComponentSummary> top10) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
            writer.write("component_id,author");
            writer.newLine();

            for (Result.ComponentSummary summary : top10) {
                List<String> members = new ArrayList<>(summary.members());
                members.sort(Comparator.naturalOrder());

                for (String author : members) {
                    writer.write(summary.componentId() + "," + escapeCsv(author));
                    writer.newLine();
                }
            }
        }
    }

    private static String escapeCsv(String value) {
        if (value == null) return "";
        if (!value.contains(",") && !value.contains("\"") && !value.contains("\n")) return value;

        String escaped = value.replace("\"", "\"\"");
        return "\"" + escaped + "\"";
    }
}
