package be.ulb.dblp.task2;

import java.util.Map;


/**
 * Construit le graphe orienté filtré à partir des compteurs A -> B.
 *
 * <p>Une arête est conservée uniquement si le nombre de publications communes entre deux
 * auteurs atteint le seuil demandé.</p>
 */
public final class GraphBuilder {

    private GraphBuilder() {
    }

    public static DirectedGraph buildFilteredGraph(Map<String, Map<String, Integer>> counts, int threshold) {
        DirectedGraph graph = new DirectedGraph();

        for (Map.Entry<String, Map<String, Integer>> sourceEntry : counts.entrySet()) {
            String source = sourceEntry.getKey();
            graph.ensureNode(source);

            for (Map.Entry<String, Integer> targetEntry : sourceEntry.getValue().entrySet()) {
                String target = targetEntry.getKey();
                graph.ensureNode(target);

                // Le filtrage garde seulement les relations suffisamment fréquentes.
                if (targetEntry.getValue() >= threshold) {
                    graph.addEdge(source, target);
                }
            }
        }

        return graph;
    }
}
