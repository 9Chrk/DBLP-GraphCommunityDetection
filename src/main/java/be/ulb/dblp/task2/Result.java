package be.ulb.dblp.task2;

import java.util.List;
import java.util.Set;

/**
 * Résultat final de la Tâche 2.
 */
public record Result(
        List<Set<String>> components,
        List<ComponentSummary> top10
) {
    public record ComponentSummary(int componentId, int size, int diameter, Set<String> members) {}
}
