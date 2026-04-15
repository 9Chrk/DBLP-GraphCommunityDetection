package be.ulb.dblp.task2;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

/**
 * Calcule le diamètre orienté d'une composante dans son sous-graphe induit.
 */
public final class CommunityDiameter {

    private CommunityDiameter() {
    }

    public static int compute(DirectedGraph graph, Set<String> component) {
        int diameter = 0;

        for (String source : component) {
            Map<String, Integer> distances = bfsDistances(graph, source, component);
            for (Integer distance : distances.values()) {
                if (distance > diameter) diameter = distance;
            }
        }

        return diameter;
    }

    private static Map<String, Integer> bfsDistances(DirectedGraph graph, String source, Set<String> component) {
        Map<String, Integer> distance = new HashMap<>();
        Queue<String> queue = new ArrayDeque<>();

        distance.put(source, 0);
        queue.add(source);

        while (!queue.isEmpty()) {
            String current = queue.remove();
            int base = distance.get(current);

            for (String neighbor : graph.neighbors(current)) {
                if (!component.contains(neighbor)) continue;
                if (distance.containsKey(neighbor)) continue;

                distance.put(neighbor, base + 1);
                queue.add(neighbor);
            }
        }

        return distance;
    }
}
