package com.progressoft.juno.util.scanner;

import com.progressoft.juno.util.SelectorUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import static com.progressoft.juno.util.Constants.*;


public class MatchPattern {
    private final String source;

    private final String regexPattern;

    private final String[] tokenized;

    private final char[][] tokenizedChar;

    private MatchPattern(String source, String separator) {
        regexPattern = SelectorUtils.isRegexPrefixedPattern(source)
                ? source.substring(REGEX_HANDLER_PREFIX.length(),
                source.length() - PATTERN_HANDLER_SUFFIX.length())
                : null;
        this.source = SelectorUtils.isAntPrefixedPattern(source)
                ? source.substring(ANT_HANDLER_PREFIX.length(),
                source.length() - PATTERN_HANDLER_SUFFIX.length())
                : source;
        tokenized = tokenizePathToString(this.source, separator);
        tokenizedChar = new char[tokenized.length][];
        for (int i = 0; i < tokenized.length; i++) {
            tokenizedChar[i] = tokenized[i].toCharArray();
        }
    }

    boolean matchPath(String str, char[][] strDirs, boolean isCaseSensitive) {
        if (regexPattern != null) {
            return str.matches(regexPattern);
        } else {
            return SelectorUtils.matchAntPathPattern(getTokenizedPathChars(), strDirs, isCaseSensitive);
        }
    }

    public boolean matchPatternStart(String str, boolean isCaseSensitive) {
        if (regexPattern != null) {
            return true;
        } else {
            String altStr = str.replace('\\', '/');

            return SelectorUtils.matchAntPathPatternStart(this, str, File.separator, isCaseSensitive)
                    || SelectorUtils.matchAntPathPatternStart(this, altStr, "/", isCaseSensitive);
        }
    }

    public String[] getTokenizedPathString() {
        return tokenized;
    }

    public char[][] getTokenizedPathChars() {
        return tokenizedChar;
    }

    public boolean startsWith(String string) {
        return source.startsWith(string);
    }

    static String[] tokenizePathToString(String path, String separator) {
        List<String> ret = new ArrayList<String>();
        StringTokenizer st = new StringTokenizer(path, separator);
        while (st.hasMoreTokens()) {
            ret.add(st.nextToken());
        }
        return ret.toArray(new String[0]);
    }

    static char[][] tokenizePathToCharArray(String path, String separator) {
        String[] tokenizedName = tokenizePathToString(path, separator);
        char[][] tokenizedNameChar = new char[tokenizedName.length][];
        for (int i = 0; i < tokenizedName.length; i++) {
            tokenizedNameChar[i] = tokenizedName[i].toCharArray();
        }
        return tokenizedNameChar;
    }

    public static MatchPattern fromString(String source) {
        return new MatchPattern(source, File.separator);
    }
}
