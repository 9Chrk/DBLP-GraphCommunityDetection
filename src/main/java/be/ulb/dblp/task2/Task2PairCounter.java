package be.ulb.dblp.task2;

import be.ulb.dblp.parsing.DblpPublicationGenerator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

/**
 * Compte en ligne les paires ordonnées A -> B pour la Tâche 2.
 */
public final class Task2PairCounter {

    private final Map<String, Map<String, Integer>> counts = new HashMap<>();

    public void processPublication(DblpPublicationGenerator.Publication publication) {
        if (publication == null || publication.authors == null || publication.authors.isEmpty()) return;

        LinkedHashSet<String> uniqueAuthors = new LinkedHashSet<>(publication.authors.size());

        for (String author : publication.authors) {
            if (author == null) continue;
            String normalized = author.trim();
            if (!normalized.isEmpty()) uniqueAuthors.add(normalized);
        }

        if (uniqueAuthors.size() < 2) return;

        List<String> ordered = new ArrayList<>(uniqueAuthors);
        String firstAuthor = ordered.get(0);

        Map<String, Integer> outgoing = counts.computeIfAbsent(firstAuthor, ignored -> new HashMap<>());
        for (int i = 1; i < ordered.size(); i++) {
            outgoing.merge(ordered.get(i), 1, Integer::sum);
        }
    }

    public Map<String, Map<String, Integer>> snapshot() {
        Map<String, Map<String, Integer>> copy = new HashMap<>();

        for (Map.Entry<String, Map<String, Integer>> entry : counts.entrySet()) {
            copy.put(entry.getKey(), new HashMap<>(entry.getValue()));
        }

        return copy;
    }
}
