package com.progressoft.juno.util.scanner;

import com.progressoft.juno.util.SelectorUtils;
import com.progressoft.juno.util.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static com.progressoft.juno.util.Constants.DEFAULT_EXCLUDES;
import static com.progressoft.juno.util.Constants.REGEX_HANDLER_PREFIX;

public abstract class AbstractScanner implements Scanner {

    protected String[] includes;

    private MatchPatterns includesPatterns;

    protected String[] excludes;

    private MatchPatterns excludesPatterns;

    protected boolean isCaseSensitive = true;

    protected Comparator<String> filenameComparator;

    public void setCaseSensitive(boolean isCaseSensitive) {
        this.isCaseSensitive = isCaseSensitive;
    }

    protected static boolean matchPatternStart(String pattern, String str) {
        return SelectorUtils.matchPatternStart(pattern, str);
    }

    protected static boolean matchPatternStart(String pattern, String str, boolean isCaseSensitive) {
        return SelectorUtils.matchPatternStart(pattern, str, isCaseSensitive);
    }

    protected static boolean matchPath(String pattern, String str) {
        return SelectorUtils.matchPath(pattern, str);
    }

    protected static boolean matchPath(String pattern, String str, boolean isCaseSensitive) {
        return SelectorUtils.matchPath(pattern, str, isCaseSensitive);
    }

    public static boolean match(String pattern, String str) {
        return SelectorUtils.match(pattern, str);
    }

    protected static boolean match(String pattern, String str, boolean isCaseSensitive) {
        return SelectorUtils.match(pattern, str, isCaseSensitive);
    }


    @Override
    public void setIncludes(String[] includes) {
        if (includes == null) {
            this.includes = null;
        } else {
            final List<String> list = new ArrayList<String>(includes.length);
            for (String include : includes) {
                if (include != null) {
                    list.add(normalizePattern(include));
                }
            }
            this.includes = list.toArray(new String[0]);
        }
    }

    @Override
    public void setExcludes(String[] excludes) {
        if (excludes == null) {
            this.excludes = null;
        } else {
            final List<String> list = new ArrayList<String>(excludes.length);
            for (String exclude : excludes) {
                if (exclude != null) {
                    list.add(normalizePattern(exclude));
                }
            }
            this.excludes = list.toArray(new String[0]);
        }
    }

    private String normalizePattern(String pattern) {
        pattern = pattern.trim();

        if (pattern.startsWith(REGEX_HANDLER_PREFIX)) {
            if (File.separatorChar == '\\') {
                pattern = StringUtils.replace(pattern, "/", "\\\\");
            } else {
                pattern = StringUtils.replace(pattern, "\\\\", "/");
            }
        } else {
            pattern = pattern.replace(File.separatorChar == '/' ? '\\' : '/', File.separatorChar);

            if (pattern.endsWith(File.separator)) {
                pattern += "**";
            }
        }

        return pattern;
    }

    protected boolean isIncluded(String name) {
        return includesPatterns.matches(name, isCaseSensitive);
    }

    protected boolean isIncluded(String name, String[] tokenizedName) {
        return includesPatterns.matches(name, tokenizedName, isCaseSensitive);
    }

    protected boolean isIncluded(String name, char[][] tokenizedName) {
        return includesPatterns.matches(name, tokenizedName, isCaseSensitive);
    }

    protected boolean couldHoldIncluded(String name) {
        return includesPatterns.matchesPatternStart(name, isCaseSensitive);
    }

    protected boolean isExcluded(String name) {
        return excludesPatterns.matches(name, isCaseSensitive);
    }

    protected boolean isExcluded(String name, String[] tokenizedName) {
        return excludesPatterns.matches(name, tokenizedName, isCaseSensitive);
    }

    protected boolean isExcluded(String name, char[][] tokenizedName) {
        return excludesPatterns.matches(name, tokenizedName, isCaseSensitive);
    }

    @Override
    public void addDefaultExcludes() {
        int excludesLength = excludes == null ? 0 : excludes.length;
        String[] newExcludes;
        newExcludes = new String[excludesLength + DEFAULT_EXCLUDES.length];
        if (excludesLength > 0) {
            System.arraycopy(excludes, 0, newExcludes, 0, excludesLength);
        }
        for (int i = 0; i < DEFAULT_EXCLUDES.length; i++) {
            newExcludes[i + excludesLength] = DEFAULT_EXCLUDES[i].replace('/', File.separatorChar);
        }
        excludes = newExcludes;
    }

    protected void setupDefaultFilters() {
        if (includes == null) {
            // No includes supplied, so set it to 'matches all'
            includes = new String[1];
            includes[0] = "**";
        }
        if (excludes == null) {
            excludes = new String[0];
        }
    }

    protected void setupMatchPatterns() {
        includesPatterns = MatchPatterns.from(includes);
        excludesPatterns = MatchPatterns.from(excludes);
    }

//    @Override
//    public void setFilenameComparator(Comparator<String> filenameComparator) {
//        this.filenameComparator = filenameComparator;
//    }
}