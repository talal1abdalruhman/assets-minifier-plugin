package com.progressoft.juno.util.scanner;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MatchPatterns {
    private final MatchPattern[] patterns;

    private MatchPatterns(MatchPattern[] patterns) {
        this.patterns = patterns;
    }

    public boolean matches(String name, boolean isCaseSensitive) {
        String[] tokenized = MatchPattern.tokenizePathToString(name, File.separator);
        return matches(name, tokenized, isCaseSensitive);
    }

    public boolean matches(String name, String[] tokenizedName, boolean isCaseSensitive) {
        char[][] tokenizedNameChar = new char[tokenizedName.length][];
        for (int i = 0; i < tokenizedName.length; i++) {
            tokenizedNameChar[i] = tokenizedName[i].toCharArray();
        }
        return matches(name, tokenizedNameChar, isCaseSensitive);
    }

    public boolean matches(String name, char[][] tokenizedNameChar, boolean isCaseSensitive) {
        for (MatchPattern pattern : patterns) {
            if (pattern.matchPath(name, tokenizedNameChar, isCaseSensitive)) {
                return true;
            }
        }
        return false;
    }

    public boolean matchesPatternStart(String name, boolean isCaseSensitive) {
        for (MatchPattern includesPattern : patterns) {
            if (includesPattern.matchPatternStart(name, isCaseSensitive)) {
                return true;
            }
        }
        return false;
    }

    public static MatchPatterns from(String... sources) {
        final int length = sources.length;
        MatchPattern[] result = new MatchPattern[length];
        for (int i = 0; i < length; i++) {
            result[i] = MatchPattern.fromString(sources[i]);
        }
        return new MatchPatterns(result);
    }

    private static MatchPattern[] getMatchPatterns(Iterable<String> items) {
        List<MatchPattern> result = new ArrayList<>();
        for (String string : items) {
            result.add(MatchPattern.fromString(string));
        }
        return result.toArray(new MatchPattern[0]);
    }
}
