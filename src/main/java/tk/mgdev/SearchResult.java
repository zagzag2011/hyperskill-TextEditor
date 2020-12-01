package tk.mgdev;

public class SearchResult {
    private final int startIndex;
    private final int endIndex;

    private SearchResult(int startIndex, int endIndex) {
        this.startIndex = startIndex;
        this.endIndex = endIndex;
    }

    public static SearchResult of(int startIndex, int endIndex) {
        return new SearchResult(startIndex, endIndex);
    }

    public int getStartIndex() {
        return startIndex;
    }

    public int getEndIndex() {
        return endIndex;
    }

    @Override
    public String toString() {
        return "{" + startIndex + "," + endIndex + "}";
    }
}
