package be.ulb.dblp.task2;

import java.util.List;
import java.util.Set;


/**
 * Résultat final de la Tâche 2.
 *
 * <p>Cette structure regroupe les composantes fortement connexes détectées dans le graphe
 * filtré ainsi que le classement des dix plus grandes composantes avec leur diamètre.</p>
 */
public record Result(
        List<Set<String>> components,
        List<ComponentSummary> top10
) {
    /**
     * Résumé d'une composante fortement connexe.
     *
     * <p>Le champ {@code diameter} vaut {@code -1} tant qu'il n'a pas encore été calculé.
     * Le champ {@code members} contient l'ensemble des auteurs appartenant à la composante.</p>
     */
    public record ComponentSummary(int componentId, int size, int diameter, Set<String> members) {}
}
