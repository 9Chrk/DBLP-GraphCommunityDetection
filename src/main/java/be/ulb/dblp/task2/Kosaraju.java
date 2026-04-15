package be.ulb.dblp.task2;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;


/**
 * Implémentation simple de Kosaraju pour les CFC.
 *
 * <p>L'algorithme effectue d'abord un parcours sur le graphe renversé pour établir un ordre
 * de finition, puis un second parcours sur le graphe original pour regrouper les sommets
 * appartenant à une même composante fortement connexe.</p>
 */
public final class Kosaraju {

    private Kosaraju() {
    }

    public static List<Set<String>> compute(DirectedGraph graph) {
        List<String> finishOrder = new ArrayList<>();
        Set<String> visited = new HashSet<>();

        // Première passe : on calcule l'ordre de finition sur le graphe renversé.
        for (String node : graph.nodes()) {
            if (!visited.contains(node)) {
                dfsFinishOrder(node, graph, visited, finishOrder);
            }
        }

        List<Set<String>> components = new ArrayList<>();
        Set<String> assigned = new HashSet<>();

        // Deuxième passe : on récupère les composantes dans l'ordre inverse de finition.
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
        Deque<NodeFrame> stack = new ArrayDeque<>();

        stack.push(new NodeFrame(start, graph.reverseNeighbors(start).iterator()));
        visited.add(start);

        while (!stack.isEmpty()) {
            NodeFrame frame = stack.peek();
            if (frame.iterator.hasNext()) {
                String neighbor = frame.iterator.next();
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    stack.push(new NodeFrame(neighbor, graph.reverseNeighbors(neighbor).iterator()));
                }
            } else {
                stack.pop();
                finishOrder.add(frame.node);
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

            // Cette fois, on suit les successeurs du graphe original pour remplir la composante.
            for (String neighbor : graph.neighbors(node)) {
                if (!assigned.contains(neighbor)) {
                    assigned.add(neighbor);
                    stack.push(neighbor);
                }
            }
        }
    }

    private static final class NodeFrame {
        private final String node;
        private final Iterator<String> iterator;

        private NodeFrame(String node, Iterator<String> iterator) {
            this.node = node;
            this.iterator = iterator;
        }
    }
}
