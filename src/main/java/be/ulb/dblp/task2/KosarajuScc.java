package be.ulb.dblp.task2;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Implémentation simple de Kosaraju pour les CFC.
 */
public final class KosarajuScc {

    private KosarajuScc() {
    }

    public static List<Set<String>> compute(DirectedGraph graph) {
        List<String> finishOrder = new ArrayList<>();
        Set<String> visited = new HashSet<>();

        for (String node : graph.nodes()) {
            if (!visited.contains(node)) {
                dfsFinishOrder(node, graph, visited, finishOrder);
            }
        }

        List<Set<String>> components = new ArrayList<>();
        Set<String> assigned = new HashSet<>();

        for (int i = finishOrder.size() - 1; i >= 0; i--) {
            String node = finishOrder.get(i);
            if (assigned.contains(node)) continue;

            Set<String> component = new HashSet<>();
            dfsCollect(node, graph, assigned, component);
            components.add(component);
        }

        return components;
    }

    private static void dfsFinishOrder(String start, DirectedGraph graph, Set<String> visited, List<String> finishOrder) {
        Deque<Frame> stack = new ArrayDeque<>();

        stack.push(new Frame(start, false));
        visited.add(start);

        while (!stack.isEmpty()) {
            Frame frame = stack.pop();
            String node = frame.node();

            if (frame.expanded()) {
                finishOrder.add(node);
                continue;
            }

            // On re-empile le nœud marqué "expanded" pour l'ajouter en vrai postordre,
            // uniquement après l'exploration de tous ses voisins.
            stack.push(new Frame(node, true));

            for (String neighbor : graph.reverseNeighbors(node)) {
                if (visited.add(neighbor)) {
                    stack.push(new Frame(neighbor, false));
                }
            }
        }
    }

    private static void dfsCollect(String start, DirectedGraph graph, Set<String> assigned, Set<String> component) {
        Deque<String> stack = new ArrayDeque<>();
        stack.push(start);
        assigned.add(start);

        while (!stack.isEmpty()) {
            String node = stack.pop();
            component.add(node);

            for (String neighbor : graph.neighbors(node)) {
                if (!assigned.contains(neighbor)) {
                    assigned.add(neighbor);
                    stack.push(neighbor);
                }
            }
        }
    }

    private record Frame(String node, boolean expanded) {}
}
