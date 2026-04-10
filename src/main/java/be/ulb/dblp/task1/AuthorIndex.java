package be.ulb.dblp.task1;

import java.util.HashMap;
import java.util.Map;

/**
 * Maps author names to compact integer ids.
 */
public final class AuthorIndex {

    public record LookupResult(int id, boolean created) {}

    private final Map<String, Integer> authorToId;

    public AuthorIndex() {
        this.authorToId = new HashMap<>(1 << 20);
    }

    public LookupResult getOrCreateId(String author, DynamicUnionFind unionFind) {
        Integer existing = authorToId.get(author);
        if (existing != null) {
            return new LookupResult(existing, false);
        }

        int id = unionFind.addElement();
        authorToId.put(author, id);
        return new LookupResult(id, true);
    }
}
