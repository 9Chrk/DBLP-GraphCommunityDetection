package be.ulb.dblp.task2;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Task2AlgorithmsTest {

    @Test
    void kosarajuShouldReturnThreeSingletonsOnDagExample() {
        DirectedGraph graph = new DirectedGraph();
        graph.addEdge("1", "0");
        graph.addEdge("2", "0");
        graph.addEdge("2", "1");

        List<Set<String>> components = KosarajuScc.compute(graph);
        Set<Set<String>> asSet = components.stream().map(Set::copyOf).collect(Collectors.toSet());

        assertEquals(Set.of(Set.of("0"), Set.of("1"), Set.of("2")), asSet);
    }

    @Test
    void graphBuilderShouldKeepVerticesEvenWithoutKeptEdges() {
        Map<String, Map<String, Integer>> counts = new HashMap<>();
        counts.put("A", new HashMap<>(Map.of("B", 5)));
        counts.put("C", new HashMap<>(Map.of("D", 6)));

        DirectedGraph graph = Task2GraphBuilder.buildFilteredGraph(counts, 6);

        assertEquals(Set.of("A", "B", "C", "D"), graph.nodes());
        assertEquals(Set.of("D"), graph.neighbors("C"));
        assertEquals(Set.of(), graph.neighbors("A"));
        assertEquals(Set.of(), graph.neighbors("B"));
    }
}
