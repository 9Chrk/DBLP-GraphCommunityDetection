package be.ulb.dblp.task2;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


/**
 * Graphe orienté simple avec listes d'adjacence et d'adjacence inverse.
 *
 * <p>Cette structure reste volontairement légère : elle sert uniquement aux traitements
 * de la Tâche 2 et expose les voisins directs ainsi que les prédécesseurs d'un sommet.</p>
 */
public final class DirectedGraph {

    private final Map<String, Set<String>> graph = new HashMap<>();
    private final Map<String, Set<String>> reverseGraph = new HashMap<>();

    /**
     * S'assure qu'un sommet existe dans les deux sens de parcours.
     */
    public void ensureNode(String node) {
        graph.computeIfAbsent(node, ignored -> new HashSet<>());
        reverseGraph.computeIfAbsent(node, ignored -> new HashSet<>());
    }

    /**
     * Ajoute une arête orientée du sommet source vers le sommet cible.
     */
    public void addEdge(String source, String target) {
        ensureNode(source);
        ensureNode(target);

        graph.get(source).add(target);
        reverseGraph.get(target).add(source);
    }

    /**
     * Retourne l'ensemble des sommets présents dans le graphe.
     */
    public Set<String> nodes() {
        return graph.keySet();
    }

    /**
     * Retourne les voisins sortants d'un sommet.
     */
    public Set<String> neighbors(String node) {
        return graph.getOrDefault(node, Collections.emptySet());
    }

    /**
     * Retourne les voisins entrants d'un sommet.
     */
    public Set<String> reverseNeighbors(String node) {
        return reverseGraph.getOrDefault(node, Collections.emptySet());
    }
}
