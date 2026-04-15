package be.ulb.dblp.task2;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Graphe orienté simple avec adjacency et reverse adjacency.
 */
public final class DirectedGraph {

    private final Map<String, Set<String>> graph = new HashMap<>();
    private final Map<String, Set<String>> reverseGraph = new HashMap<>();

    public void ensureNode(String node) {
        graph.computeIfAbsent(node, ignored -> new HashSet<>());
        reverseGraph.computeIfAbsent(node, ignored -> new HashSet<>());
    }

    public void addEdge(String source, String target) {
        ensureNode(source);
        ensureNode(target);

        graph.get(source).add(target);
        reverseGraph.get(target).add(source);
    }

    public Set<String> nodes() {
        return graph.keySet();
    }

    public Set<String> neighbors(String node) {
        return graph.getOrDefault(node, Collections.emptySet());
    }

    public Set<String> reverseNeighbors(String node) {
        return reverseGraph.getOrDefault(node, Collections.emptySet());
    }
}
