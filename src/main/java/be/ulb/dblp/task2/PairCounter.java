package be.ulb.dblp.task2;

import be.ulb.dblp.parsing.DblpPublicationGenerator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;


/**
 * Compte en ligne les paires ordonnées A -> B pour la Tâche 2.
 *
 * <p>Chaque publication est nettoyée, les auteurs en double sont supprimés tout en gardant
 * leur ordre d'apparition, puis les comptes sont mis à jour pour les couples
 * {@code premierAuteur -> autresAuteurs}.</p>
 */
public final class PairCounter {

    private final Map<String, Map<String, Integer>> counts = new HashMap<>();

    /**
     * Traite une publication et met à jour les comptes des paires orientées.
     *
     * <p>Les auteurs sont d'abord normalisés avec {@code trim()}, puis les valeurs nulles
     * et vides sont ignorées. Le {@code LinkedHashSet} conserve l'ordre d'apparition tout en
     * supprimant les doublons.</p>
     */
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

    /**
     * Retourne une copie défensive de l'état courant des comptes.
     */
    public Map<String, Map<String, Integer>> snapshot() {
        Map<String, Map<String, Integer>> copy = new HashMap<>();

        for (Map.Entry<String, Map<String, Integer>> entry : counts.entrySet()) {
            // Chaque sous-carte est recopiée pour éviter toute modification externe de l'état interne.
            copy.put(entry.getKey(), new HashMap<>(entry.getValue()));
        }

        return copy;
    }
}
