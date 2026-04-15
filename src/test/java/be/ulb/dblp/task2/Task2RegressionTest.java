package be.ulb.dblp.task2;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class Task2RegressionTest {

    @Test
    void kosarajuShouldNotMergeAcyclicNodes() {
        DirectedGraph graph = new DirectedGraph();
        graph.addEdge("1", "0");
        graph.addEdge("2", "0");
        graph.addEdge("2", "1");

        List<Set<String>> components = Kosaraju.compute(graph);

        assertEquals(3, components.size());
        assertTrue(components.contains(Set.of("0")));
        assertTrue(components.contains(Set.of("1")));
        assertTrue(components.contains(Set.of("2")));
    }

    @Test
    void filteredGraphShouldKeepIsolatedNodesAfterThresholding() {
        Map<String, Map<String, Integer>> counts = Map.of(
                "A", Map.of("B", 5),
                "C", Map.of("D", 6)
        );

        DirectedGraph filteredGraph = GraphBuilder.buildFilteredGraph(counts, 6);

        assertEquals(Set.of("A", "B", "C", "D"), filteredGraph.nodes());
        assertEquals(Set.of(), filteredGraph.neighbors("A"));
        assertEquals(Set.of(), filteredGraph.neighbors("B"));
        assertEquals(Set.of("D"), filteredGraph.neighbors("C"));
        assertEquals(Set.of(), filteredGraph.neighbors("D"));
    }
}
