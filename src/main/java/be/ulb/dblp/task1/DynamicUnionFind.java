package be.ulb.dblp.task1;

import java.util.Arrays;


/**
 * Implémentation dynamique d'un {@code Union-Find} ({@code Disjoint Set Union}).
 *
 * <p>Cette structure gère des identifiants ajoutés au fur et à mesure du traitement
 * et optimise les opérations grâce à la compression de chemin et à l'union par taille.</p>
 */
public final class DynamicUnionFind {

    private int[] parent;
    private int[] size;

    private int elementCount;
    private int componentCount;

    public DynamicUnionFind() {
        this.parent = new int[16];
        this.size = new int[16];
        this.elementCount = 0;
        this.componentCount = 0;
    }

    /**
     * Ajoute un nouvel élément et retourne son identifiant.
     *
     * @return l'identifiant du nouvel élément
     */
    public int addElement() {
        ensureCapacity(elementCount + 1);
        int id = elementCount;
        parent[id] = id;
        size[id] = 1;
        elementCount++;
        componentCount++;
        return id;
    }

    /**
     * Retourne la racine de l'ensemble contenant {@code x}.
     *
     * <p>Le chemin vers la racine est compressé pour accélérer les recherches futures.</p>
     *
     * @param x l'identifiant à localiser
     * @return la racine de sa composante
     */
    public int find(int x) {
        int root = x;
        while (root != parent[root]) {
            root = parent[root];
        }

        // Compression du chemin : tous les nœuds visités pointent directement vers la racine.
        int current = x;
        while (current != root) {
            int next = parent[current];
            parent[current] = root;
            current = next;
        }

        return root;
    }

    /**
     * Fusionne les ensembles contenant {@code a} et {@code b}.
     *
     * <p>Si les deux éléments sont déjà dans la même composante, rien n'est modifié.</p>
     *
     * @param a premier identifiant
     * @param b second identifiant
     * @return {@code true} si une fusion a eu lieu, {@code false} sinon
     */
    public boolean union(int a, int b) {
        int rootA = find(a);
        int rootB = find(b);

        if (rootA == rootB) {
            return false;
        }

        // On attache toujours la plus petite composante à la plus grande.
        if (size[rootA] < size[rootB]) {
            int tmp = rootA;
            rootA = rootB;
            rootB = tmp;
        }

        parent[rootB] = rootA;
        size[rootA] += size[rootB];
        componentCount--;
        return true;
    }

    /**
     * Retourne la taille de la composante contenant {@code x}.
     *
     * @param x l'identifiant recherché
     * @return la taille de sa composante
     */
    public int componentSize(int x) {
        int root = find(x);
        return size[root];
    }

    /**
     * Retourne la taille d'une composante à partir de sa racine.
     *
     * @param root la racine de la composante
     * @return la taille stockée pour cette racine
     */
    public int componentSizeByRoot(int root) {
        return size[root];
    }

    /**
     * Retourne le nombre courant de composantes connexes.
     *
     * @return le nombre de composantes
     */
    public int componentCount() {
        return componentCount;
    }

    /**
     * Garantit que les tableaux internes peuvent accueillir {@code required} éléments.
     */
    private void ensureCapacity(int required) {
        if (required <= parent.length) {
            return;
        }

        int newCapacity = parent.length;
        while (newCapacity < required) {
            newCapacity *= 2;
        }

        parent = Arrays.copyOf(parent, newCapacity);
        size = Arrays.copyOf(size, newCapacity);
    }
}
