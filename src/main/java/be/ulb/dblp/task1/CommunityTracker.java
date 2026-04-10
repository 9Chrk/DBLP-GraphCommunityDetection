package be.ulb.dblp.task1;

import be.ulb.dblp.parsing.DblpPublicationGenerator;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Online tracker for connected components in the undirected co-authorship graph.
 */
public final class CommunityTracker {

    private final AuthorIndex authorIndex;
    private final DynamicUnionFind unionFind;
    private final TreeMap<Integer, Integer> sizeHistogram;

    public CommunityTracker() {
        this.authorIndex = new AuthorIndex();
        this.unionFind = new DynamicUnionFind();
        this.sizeHistogram = new TreeMap<>();
    }

    public void processPublication(DblpPublicationGenerator.Publication publication) {
        if (publication == null || publication.authors == null || publication.authors.isEmpty()) {
            return;
        }

        LinkedHashSet<String> uniqueAuthors = new LinkedHashSet<>(publication.authors.size());
        for (String author : publication.authors) {
            if (author == null) {
                continue;
            }
            String normalized = author.trim();
            if (!normalized.isEmpty()) {
                uniqueAuthors.add(normalized);
            }
        }

        if (uniqueAuthors.isEmpty()) {
            return;
        }

        List<Integer> ids = new ArrayList<>(uniqueAuthors.size());
        for (String author : uniqueAuthors) {
            AuthorIndex.LookupResult result = authorIndex.getOrCreateId(author, unionFind);
            ids.add(result.id());
            if (result.created()) {
                addToHistogram(1, 1);
            }
        }

        if (ids.size() == 1) {
            return;
        }

        int pivot = ids.get(0);
        for (int i = 1; i < ids.size(); i++) {
            mergeComponents(pivot, ids.get(i));
        }
    }

    private void mergeComponents(int a, int b) {
        int rootA = unionFind.find(a);
        int rootB = unionFind.find(b);

        if (rootA == rootB) {
            return;
        }

        int sizeA = unionFind.componentSizeByRoot(rootA);
        int sizeB = unionFind.componentSizeByRoot(rootB);

        removeFromHistogram(sizeA, 1);
        removeFromHistogram(sizeB, 1);

        unionFind.union(rootA, rootB);

        int mergedSize = sizeA + sizeB;
        addToHistogram(mergedSize, 1);
    }

    private void addToHistogram(int size, int delta) {
        sizeHistogram.merge(size, delta, Integer::sum);
    }

    private void removeFromHistogram(int size, int delta) {
        Integer current = sizeHistogram.get(size);
        if (current == null) {
            return;
        }
        int next = current - delta;
        if (next <= 0) {
            sizeHistogram.remove(size);
        } else {
            sizeHistogram.put(size, next);
        }
    }

    public int communityCount() {
        return unionFind.componentCount();
    }

    public List<Integer> topCommunitySizes(int limit) {
        List<Integer> top = new ArrayList<>(Math.max(0, limit));
        if (limit <= 0) {
            return top;
        }

        for (Map.Entry<Integer, Integer> entry : sizeHistogram.descendingMap().entrySet()) {
            int size = entry.getKey();
            int count = entry.getValue();
            for (int i = 0; i < count && top.size() < limit; i++) {
                top.add(size);
            }
            if (top.size() >= limit) {
                break;
            }
        }

        return top;
    }

    public TreeMap<Integer, Integer> histogramSnapshot() {
        return new TreeMap<>(sizeHistogram);
    }
}
