package be.ulb.dblp.task1;

import be.ulb.dblp.parsing.DblpPublicationGenerator;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


/**
 * Suit en ligne les communautés du graphe non orienté de co-publication.
 *
 * <p>À chaque publication, la classe nettoie la liste des auteur.rice.s, les transforme
 * en identifiants compacts, puis fusionne les composantes connexes concernées dans
 * le {@code Union-Find}. En parallèle, elle maintient un histogramme des tailles des
 * communautés.</p>
 */
public final class CommunityTracker {

    // Instances utilisées
    private final AuthorIndex authorIndex;
    private final DynamicUnionFind unionFind;
    private final TreeMap<Integer, Integer> sizeHistogram;

    public CommunityTracker() {
        this.authorIndex = new AuthorIndex();
        this.unionFind = new DynamicUnionFind();
        this.sizeHistogram = new TreeMap<>();
    }

    /**
     * Intègre une publication au suivi des communautés.
     *
     * <p>Les noms sont d'abord normalisés et dé dupliqué afin d'éviter de compter
     * plusieurs fois la même auteur.rice dans une publication.</p>
     *
     * @param publication la publication lue par le générateur DBLP
     */
    public void processPublication(DblpPublicationGenerator.Publication publication) {
        if (publication == null || publication.authors == null || publication.authors.isEmpty()) return;

        // On supprime les doublons tout en conservant l'ordre d'apparition des auteur.rice.s.
        LinkedHashSet<String> uniqueAuthors = new LinkedHashSet<>(publication.authors.size());

        for (String author : publication.authors) {
            if (author == null) continue;

            String normalized = author.trim();
            if (!normalized.isEmpty()) uniqueAuthors.add(normalized);
        }

        // Si la publication ne contient que des auteur.rice.s vides ou null, on l'ignore.
        if (uniqueAuthors.isEmpty()) return;

        // On convertit ensuite chaque nom en identifiant compact pour travailler avec le Union-Find.
        List<Integer> ids = new ArrayList<>(uniqueAuthors.size());

        for (String author : uniqueAuthors) {
            AuthorIndex.LookupResult result = authorIndex.getOrCreateId(author, unionFind);
            ids.add(result.id());

            // Un singleton de taille 1 apparaît à chaque nouvelle auteur.rice.
            if (result.created()) addToHistogram(1, 1);
        }

        // Il n'existe pas de co-autheurs.
        if (ids.size() == 1) return;

        int pivot = ids.get(0);
        for (int i = 1; i < ids.size(); i++) {
            // Le premier auteur sert de point d'ancrage pour fusionner tous les co-auteurs.
            mergeComponents(pivot, ids.get(i));
        }
    }

    /**
     * Fusionne les composantes contenant les deux identifiants donnés.
     *
     * <p>Les tailles sont retirées de l'histogramme avant la fusion, puis la taille
     * de la nouvelle composante est ajoutée.</p>
     */
    private void mergeComponents(int a, int b) {
        int rootA = unionFind.find(a);
        int rootB = unionFind.find(b);

        if (rootA == rootB) return;

        int sizeA = unionFind.componentSizeByRoot(rootA);
        int sizeB = unionFind.componentSizeByRoot(rootB);

        // On retire les deux anciennes tailles avant d'enregistrer la composante fusionnée.
        removeFromHistogram(sizeA, 1);
        removeFromHistogram(sizeB, 1);

        unionFind.union(rootA, rootB);

        int mergedSize = sizeA + sizeB;
        addToHistogram(mergedSize, 1);
    }

    /** Ajoute ou retire une occurrence d'une taille dans l'histogramme. */
    private void addToHistogram(int size, int delta) {
        sizeHistogram.merge(size, delta, Integer::sum);
    }

    /**
     * Retire une occurrence d'une taille dans l'histogramme et supprime l'entrée si besoin.
     */
    private void removeFromHistogram(int size, int delta) {
        Integer current = sizeHistogram.get(size);

        if (current == null) return;
        int next = current - delta;

        //
        if (next <= 0) {sizeHistogram.remove(size);} else {sizeHistogram.put(size, next);}
    }

    /**
     * Retourne le nombre courant de communautés.
     *
     * @return le nombre de composantes connexes connues à cet instant
     */
    public int communityCount() {
        return unionFind.componentCount();
    }

    /**
     * Retourne les tailles des plus grandes communautés observées.
     *
     * @param limit le nombre maximal de tailles à retourner
     * @return une liste ordonnée des plus grandes tailles, du plus grand au plus petit
     */
    public List<Integer> topCommunitySizes(int limit) {
        List<Integer> top = new ArrayList<>(Math.max(0, limit));

        if (limit <= 0) return top;

        for (Map.Entry<Integer, Integer> entry : sizeHistogram.descendingMap().entrySet()) {
            int size = entry.getKey();
            int count = entry.getValue();

            // Si plusieurs communautés ont la même taille, on la répète autant de fois que nécessaire.
            for (int i = 0; i < count && top.size() < limit; i++) {
                top.add(size);
            }

            if (top.size() >= limit) break;
        }
        return top;
    }

    /**
     * Fournit une copie de l'histogramme courant pour l'écriture des résultats.
     *
     * @return une copie triée par taille de communauté
     */
    public TreeMap<Integer, Integer> histogramSnapshot() {
        return new TreeMap<>(sizeHistogram);
    }
}
