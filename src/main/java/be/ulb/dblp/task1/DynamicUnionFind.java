package be.ulb.dblp.task1;

import java.util.Arrays;

/**
 * Dynamic Union-Find (Disjoint Set Union) with:
 * - path compression
 * - union by size
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

    public int addElement() {
        ensureCapacity(elementCount + 1);
        int id = elementCount;
        parent[id] = id;
        size[id] = 1;
        elementCount++;
        componentCount++;
        return id;
    }

    public int find(int x) {
        int root = x;
        while (root != parent[root]) {
            root = parent[root];
        }

        int current = x;
        while (current != root) {
            int next = parent[current];
            parent[current] = root;
            current = next;
        }

        return root;
    }

    public boolean union(int a, int b) {
        int rootA = find(a);
        int rootB = find(b);

        if (rootA == rootB) {
            return false;
        }

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

    public int componentSize(int x) {
        int root = find(x);
        return size[root];
    }

    public int componentSizeByRoot(int root) {
        return size[root];
    }

    public int componentCount() {
        return componentCount;
    }

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
