package be.ulb.dblp.task1;

import be.ulb.dblp.parsing.DblpPublicationGenerator;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Runs task 1 in online mode while parsing DBLP.
 */
public final class Task1Runner {

    private Task1Runner() {
    }

    public static void run(Path xmlPath, Path dtdPath, Path outputDir, int reportEvery) throws Exception {
        Files.createDirectories(outputDir);

        Task1CommunityTracker tracker = new Task1CommunityTracker();
        long publicationCount = 0L;

        try (DblpPublicationGenerator generator = new DblpPublicationGenerator(xmlPath, dtdPath, 256)) {
            while (true) {
                Optional<DblpPublicationGenerator.Publication> optional = generator.nextPublication();
                if (optional.isEmpty()) {
                    break;
                }

                publicationCount++;
                tracker.processPublication(optional.get());

                if (reportEvery > 0 && publicationCount % reportEvery == 0) {
                    printProgress(publicationCount, tracker);
                }
            }
        }

        writeHistogram(outputDir.resolve("community_size_histogram.csv"), tracker.histogramSnapshot());
        printProgress(publicationCount, tracker);
        System.out.println("Task 1 histogram written to: " + outputDir.resolve("community_size_histogram.csv"));
    }

    private static void printProgress(long publicationCount, Task1CommunityTracker tracker) {
        List<Integer> top10 = tracker.topCommunitySizes(10);
        System.out.println("[Task1] publications=" + publicationCount
                + " communities=" + tracker.communityCount()
                + " top10=" + top10);
    }

    private static void writeHistogram(Path file, Map<Integer, Integer> histogram) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
            writer.write("taille;nombre_de_communautes");
            writer.newLine();

            for (Map.Entry<Integer, Integer> entry : histogram.entrySet()) {
                writer.write(entry.getKey() + ";" + entry.getValue());
                writer.newLine();
            }
        }
    }
}
