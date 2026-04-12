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
 * Exécute la Tâche 1 pendant le parsing DBLP en traitement en flux.
 *
 * <p>Le runner lit les publications une par une, met à jour les communautés en ligne
 * et écrit à la fin l'histogramme des tailles dans le répertoire de sortie.</p>
 */
public final class Runner {

    private Runner() {
    }

    public static void run(Path xmlPath, Path dtdPath, Path outputDir, int reportEvery, long limit) throws Exception {
        Files.createDirectories(outputDir);

        long publicationCount = 0;
        CommunityTracker tracker = new CommunityTracker();

        // Le générateur DBLP fournit les publications une par une, sans charger tout le fichier en mémoire.
        try (DblpPublicationGenerator generator = new DblpPublicationGenerator(xmlPath, dtdPath, 256)) {
            while (publicationCount < limit) {
                Optional<DblpPublicationGenerator.Publication> optional = generator.nextPublication();

                if (optional.isEmpty()) break;

                publicationCount++;
                tracker.processPublication(optional.get());

                // On affiche périodiquement l'état intermédiaire pour prouver le traitement en ligne.
                if (reportEvery > 0 && publicationCount % reportEvery == 0) {
                    printProgress(publicationCount, tracker);
                }
            }
        }

        // L'histogramme final est écrit une fois le parsing terminé.
        writeHistogram(outputDir.resolve("community_size_histogram.csv"), tracker.histogramSnapshot());

        // Affichage final
        System.out.println("\n\nFinal state after processing " + publicationCount + " publications:");
        printProgress(publicationCount, tracker);

        System.out.println("\n\nTask 1 histogram written to: " + outputDir.resolve("community_size_histogram.csv"));
    }


    // ----------- Méthodes utilitaires (affichage et sauvegarde) -----------

    /**
     * Affiche un état intermédiaire simple sur le terminal.
     */
    private static void printProgress(long publicationCount, CommunityTracker tracker) {
        List<Integer> top10 = tracker.topCommunitySizes(10);

        System.out.println("[Task1] publications=" + publicationCount
                + " communities=" + tracker.communityCount()
                + " top10=" + top10);
    }

    /**
     * Écrit l'histogramme des tailles dans un fichier CSV séparé par point-virgule.
     */
    private static void writeHistogram(Path file, Map<Integer, Integer> histogram) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
            writer.write("taille;nombre_de_communautes");
            writer.newLine();

            // Chaque ligne représente une taille de communauté et le nombre de fois où elle apparaît.
            for (Map.Entry<Integer, Integer> entry : histogram.entrySet()) {
                writer.write(entry.getKey() + ";" + entry.getValue());
                writer.newLine();
            }
        }
    }
}
