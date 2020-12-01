package tk.mgdev;

import java.util.*;
import java.util.regex.Pattern;

public class SearchResults {
    private final List<SearchResult> searchResults;
    private final String content;
    private final String pattern;
    private int currentResultIndex;

    private SearchResults(String pattern, String content, boolean useRegEx) {
        searchResults = new ArrayList<>();
        this.pattern = pattern;
        this.content = content;
        currentResultIndex = -1;
        generateMatchResults(useRegEx);
    }

    public static SearchResults createEmpty() {
        return new SearchResults("EmptyPattern", "EmptyContent", false);
    }

    public static SearchResults of(String pattern, String content, boolean useRegEx) {
        return new SearchResults(pattern, content, useRegEx);
    }

    public boolean isEmpty() {
        return searchResults.isEmpty();
    }

    private void generateMatchResults(boolean useRegEx) {
        if ("".equals(pattern) || "".equals(content) || pattern == null || content == null) {
            return;
        }
        if (useRegEx) {
            generateMatchResultsUsingRegEx();
        } else {
            generateMatchResultsUsingPlainText();
        }
    }

    private void generateMatchResultsUsingRegEx() {
        Pattern.compile(pattern)
                .matcher(content)
                .results()
                .forEach(matchResult ->
                        searchResults.add(SearchResult.of(matchResult.start(), matchResult.end())));
    }

    private void generateMatchResultsUsingPlainText() {
        int startIndex = 0;
        while (content.indexOf(pattern, startIndex) != -1) {
            startIndex = content.indexOf(pattern, startIndex);
            int endIndex = startIndex + pattern.length();
            searchResults.add(SearchResult.of(startIndex, endIndex));
            startIndex++;
        }
    }

    public SearchResult nextResult() {
        if (searchResults.isEmpty()) {
            throw new NoSuchElementException("No matches found");
        }
        currentResultIndex = ++currentResultIndex % searchResults.size();
        return searchResults.get(currentResultIndex);
    }

    public SearchResult previousResult() {
        if (searchResults.isEmpty()) {
            throw new NoSuchElementException("No matches found");
        }
        currentResultIndex = --currentResultIndex >= 0 ?
                currentResultIndex : searchResults.size() - 1;
        return searchResults.get(currentResultIndex);
    }

    @Override
    public String toString() {
        return searchResults.toString();
    }
}
