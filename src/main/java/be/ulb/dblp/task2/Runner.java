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
 * Exécute la Tâche 2 en traitement en flux, puis écrit les CSV de sortie.
 */
public final class Runner {

    private static final int EDGE_THRESHOLD = 6;

    private Runner() {
    }

    public static void run(Path xmlPath, Path dtdPath, Path outputDir, int reportEvery, long limit) throws Exception {
        Files.createDirectories(outputDir);

        long publicationCount = 0;
        Task2PairCounter pairCounter = new Task2PairCounter();

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

        DirectedGraph graph = Task2GraphBuilder.buildFilteredGraph(pairCounter.snapshot(), EDGE_THRESHOLD);
        List<Set<String>> components = KosarajuScc.compute(graph);
        List<Task2Result.ComponentSummary> top10 = computeTop10WithDiameter(graph, components, 10);

        Task2Result result = new Task2Result(components, top10);

        writeComponentSizes(outputDir.resolve("task2_component_sizes.csv"), result.components());
        writeTop10(outputDir.resolve("task2_top10.csv"), result.top10());
        writeTop10Members(outputDir.resolve("task2_top10_members.csv"), result.top10());

        System.out.println("\nTask 2 completed after " + publicationCount + " publications.");
        System.out.println("Task 2 SCC count: " + result.components().size());
        System.out.println("Task 2 outputs written to: " + outputDir);
    }

    private static List<Task2Result.ComponentSummary> computeTop10WithDiameter(DirectedGraph graph,
                                                                                List<Set<String>> components,
                                                                                int limit) {
        List<Task2Result.ComponentSummary> sorted = new ArrayList<>(components.size());

        for (int i = 0; i < components.size(); i++) {
            Set<String> members = components.get(i);
            sorted.add(new Task2Result.ComponentSummary(i, members.size(), -1, members));
        }

        sorted.sort((a, b) -> {
            int bySize = Integer.compare(b.size(), a.size());
            if (bySize != 0) return bySize;
            return Integer.compare(a.componentId(), b.componentId());
        });

        int topCount = Math.min(limit, sorted.size());
        List<Task2Result.ComponentSummary> top = new ArrayList<>(topCount);

        for (int i = 0; i < topCount; i++) {
            Task2Result.ComponentSummary summary = sorted.get(i);
            int diameter = CommunityDiameter.compute(graph, summary.members());
            top.add(new Task2Result.ComponentSummary(
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

    private static void writeTop10(Path file, List<Task2Result.ComponentSummary> top10) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
            writer.write("rank,component_id,size,diameter");
            writer.newLine();

            for (int i = 0; i < top10.size(); i++) {
                Task2Result.ComponentSummary summary = top10.get(i);
                writer.write((i + 1) + "," + summary.componentId() + "," + summary.size() + "," + summary.diameter());
                writer.newLine();
            }
        }
    }

    private static void writeTop10Members(Path file, List<Task2Result.ComponentSummary> top10) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
            writer.write("component_id,author");
            writer.newLine();

            for (Task2Result.ComponentSummary summary : top10) {
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
