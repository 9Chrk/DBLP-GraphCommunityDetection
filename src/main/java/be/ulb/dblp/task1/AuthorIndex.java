package be.ulb.dblp.task1;

import java.util.HashMap;
import java.util.Map;


/**
 * Indexe les noms d'auteur.rice.s pour les associer à des identifiants entiers compacts.
 *
 * <p>Cette structure sert à éviter de manipuler directement des chaînes de caractères
 * dans le {@code Union-Find}. Chaque nouveau nom reçoit un identifiant stable, réutilisé
 * ensuite pendant tout le traitement.</p>
 */
public final class AuthorIndex {

    /**
     * Résultat de la recherche d'un.e auteur.rice dans l'index.
     *
     * @param id l'identifiant associé à cette auteurrice
     * @param created {@code true} si l'identifiant vient d'être créé, {@code false} sinon
     */
    public record LookupResult(int id, boolean created) {}

    // Dictionnaire
    private final Map<String, Integer> authorToId;

    /**
     * Crée un index prêt à accueillir un grand nombre d'auteur.rice.s,
     * en initialisant le dictionnaire.
     */
    public AuthorIndex() {
        this.authorToId = new HashMap<>(1 << 20);
    }

    /**
     * Retourne l'identifiant associé à {@code author}, ou en crée un nouveau si besoin.
     *
     * @param author le nom normalisé de l'auteur.rice
     * @param unionFind la structure qui fournit les nouveaux identifiants compacts
     * @return le résultat indiquant l'identifiant utilisé et s'il a été créé
     */
    public LookupResult getOrCreateId(String author, DynamicUnionFind unionFind) {
        Integer existing = authorToId.get(author);

        if (existing != null) {
            // L'auteurrice est déjà connue : on réutilise son identifiant.
            return new LookupResult(existing, false);
        }

        // Nouvelle auteurrice : on crée un identifiant et on l'enregistre dans l'index.
        int id = unionFind.addElement();
        authorToId.put(author, id);
        return new LookupResult(id, true);
    }
}
