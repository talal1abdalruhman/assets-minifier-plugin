package com.progressoft.juno.util;

import com.progressoft.juno.util.scanner.MatchPattern;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import static com.progressoft.juno.util.Constants.*;


public final class SelectorUtils {
    private static final SelectorUtils instance = new SelectorUtils();

    private SelectorUtils() {}


    public static SelectorUtils getInstance() {
        return instance;
    }

    public static boolean matchPatternStart(String pattern, String str) {
        return matchPatternStart(pattern, str, true);
    }

    public static boolean matchPatternStart(String pattern, String str, boolean isCaseSensitive) {
        if (isRegexPrefixedPattern(pattern)) {
            return true;
        } else {
            if (isAntPrefixedPattern(pattern)) {
                pattern = pattern.substring(
                        ANT_HANDLER_PREFIX.length(), pattern.length() - PATTERN_HANDLER_SUFFIX.length());
            }

            String altStr = str.replace('\\', '/');

            return matchAntPathPatternStart(pattern, str, File.separator, isCaseSensitive)
                    || matchAntPathPatternStart(pattern, altStr, "/", isCaseSensitive);
        }
    }

    public static boolean isAntPrefixedPattern(String pattern) {
        return pattern.length() > (ANT_HANDLER_PREFIX.length() + PATTERN_HANDLER_SUFFIX.length() + 1)
                && pattern.startsWith(ANT_HANDLER_PREFIX)
                && pattern.endsWith(PATTERN_HANDLER_SUFFIX);
    }

    @SuppressWarnings("SimplifiableIfStatement")
    public static boolean matchAntPathPatternStart(MatchPattern pattern, String str, String separator, boolean isCaseSensitive) {
        if (separatorPatternStartSlashMismatch(pattern, str, separator)) {
            return false;
        }

        return matchAntPathPatternStart(pattern.getTokenizedPathString(), str, separator, isCaseSensitive);
    }

    public static boolean matchAntPathPatternStart(String pattern, String str, String separator, boolean isCaseSensitive) {
        if (separatorPatternStartSlashMismatch(pattern, str, separator)) {
            return false;
        }

        String[] patDirs = tokenizePathToString(pattern, separator);
        return matchAntPathPatternStart(patDirs, str, separator, isCaseSensitive);
    }

    private static boolean separatorPatternStartSlashMismatch(String pattern, String str, String separator) {
        return str.startsWith(separator) != pattern.startsWith(separator);
    }

    private static boolean separatorPatternStartSlashMismatch(MatchPattern matchPattern, String str, String separator) {
        return str.startsWith(separator) != matchPattern.startsWith(separator);
    }

    public static boolean matchAntPathPatternStart(String[] patDirs, String str, String separator, boolean isCaseSensitive) {
        String[] strDirs = tokenizePathToString(str, separator);

        int patIdxStart = 0;
        int patIdxEnd = patDirs.length - 1;
        int strIdxStart = 0;
        int strIdxEnd = strDirs.length - 1;

        // up to first '**'
        while (patIdxStart <= patIdxEnd && strIdxStart <= strIdxEnd) {
            String patDir = patDirs[patIdxStart];
            if (patDir.equals("**")) {
                break;
            }
            if (!match(patDir, strDirs[strIdxStart], isCaseSensitive)) {
                return false;
            }
            patIdxStart++;
            strIdxStart++;
        }

        return strIdxStart > strIdxEnd || patIdxStart <= patIdxEnd;
    }

    public static boolean matchPath(String pattern, String str) {
        return matchPath(pattern, str, true);
    }

    public static boolean matchPath(String pattern, String str, boolean isCaseSensitive) {
        return matchPath(pattern, str, File.separator, isCaseSensitive);
    }

    public static boolean matchPath(String pattern, String str, String separator, boolean isCaseSensitive) {
        if (isRegexPrefixedPattern(pattern)) {
            String localPattern = pattern.substring(
                    REGEX_HANDLER_PREFIX.length(), pattern.length() - PATTERN_HANDLER_SUFFIX.length());

            return str.matches(localPattern);
        } else {
            String localPattern = isAntPrefixedPattern(pattern)
                    ? pattern.substring(ANT_HANDLER_PREFIX.length(), pattern.length() - PATTERN_HANDLER_SUFFIX.length())
                    : pattern;
            final String osRelatedPath = toOSRelatedPath(str, separator);
            final String osRelatedPattern = toOSRelatedPath(localPattern, separator);
            return matchAntPathPattern(osRelatedPattern, osRelatedPath, separator, isCaseSensitive);
        }
    }

    private static String toOSRelatedPath(String pattern, String separator) {
        if ("/".equals(separator)) {
            return pattern.replace("\\", separator);
        }
        if ("\\".equals(separator)) {
            return pattern.replace("/", separator);
        }
        return pattern;
    }

    public static boolean isRegexPrefixedPattern(String pattern) {
        return pattern.length() > (REGEX_HANDLER_PREFIX.length() + PATTERN_HANDLER_SUFFIX.length() + 1)
                && pattern.startsWith(REGEX_HANDLER_PREFIX)
                && pattern.endsWith(PATTERN_HANDLER_SUFFIX);
    }

    public static boolean matchAntPathPattern(
            MatchPattern matchPattern, String str, String separator, boolean isCaseSensitive) {
        if (separatorPatternStartSlashMismatch(matchPattern, str, separator)) {
            return false;
        }
        String[] patDirs = matchPattern.getTokenizedPathString();
        String[] strDirs = tokenizePathToString(str, separator);
        return matchAntPathPattern(patDirs, strDirs, isCaseSensitive);
    }

    public static boolean matchAntPathPattern(String pattern, String str, String separator, boolean isCaseSensitive) {
        if (separatorPatternStartSlashMismatch(pattern, str, separator)) {
            return false;
        }
        String[] patDirs = tokenizePathToString(pattern, separator);
        String[] strDirs = tokenizePathToString(str, separator);
        return matchAntPathPattern(patDirs, strDirs, isCaseSensitive);
    }

    public static boolean matchAntPathPattern(String[] patDirs, String[] strDirs, boolean isCaseSensitive) {
        int patIdxStart = 0;
        int patIdxEnd = patDirs.length - 1;
        int strIdxStart = 0;
        int strIdxEnd = strDirs.length - 1;

        // up to first '**'
        while (patIdxStart <= patIdxEnd && strIdxStart <= strIdxEnd) {
            String patDir = patDirs[patIdxStart];
            if (patDir.equals("**")) {
                break;
            }
            if (!match(patDir, strDirs[strIdxStart], isCaseSensitive)) {
                return false;
            }
            patIdxStart++;
            strIdxStart++;
        }
        if (strIdxStart > strIdxEnd) {
            // String is exhausted
            for (int i = patIdxStart; i <= patIdxEnd; i++) {
                if (!patDirs[i].equals("**")) {
                    return false;
                }
            }
            return true;
        } else {
            if (patIdxStart > patIdxEnd) {
                // String not exhausted, but pattern is. Failure.
                return false;
            }
        }

        // up to last '**'
        while (patIdxStart <= patIdxEnd && strIdxStart <= strIdxEnd) {
            String patDir = patDirs[patIdxEnd];
            if (patDir.equals("**")) {
                break;
            }
            if (!match(patDir, strDirs[strIdxEnd], isCaseSensitive)) {
                return false;
            }
            patIdxEnd--;
            strIdxEnd--;
        }
        if (strIdxStart > strIdxEnd) {
            // String is exhausted
            for (int i = patIdxStart; i <= patIdxEnd; i++) {
                if (!patDirs[i].equals("**")) {
                    return false;
                }
            }
            return true;
        }

        while (patIdxStart != patIdxEnd && strIdxStart <= strIdxEnd) {
            int patIdxTmp = -1;
            for (int i = patIdxStart + 1; i <= patIdxEnd; i++) {
                if (patDirs[i].equals("**")) {
                    patIdxTmp = i;
                    break;
                }
            }
            if (patIdxTmp == patIdxStart + 1) {
                // '**/**' situation, so skip one
                patIdxStart++;
                continue;
            }
            // Find the pattern between padIdxStart & padIdxTmp in str between
            // strIdxStart & strIdxEnd
            int patLength = (patIdxTmp - patIdxStart - 1);
            int strLength = (strIdxEnd - strIdxStart + 1);
            int foundIdx = -1;
            strLoop:
            for (int i = 0; i <= strLength - patLength; i++) {
                for (int j = 0; j < patLength; j++) {
                    String subPat = patDirs[patIdxStart + j + 1];
                    String subStr = strDirs[strIdxStart + i + j];
                    if (!match(subPat, subStr, isCaseSensitive)) {
                        continue strLoop;
                    }
                }

                foundIdx = strIdxStart + i;
                break;
            }

            if (foundIdx == -1) {
                return false;
            }

            patIdxStart = patIdxTmp;
            strIdxStart = foundIdx + patLength;
        }

        for (int i = patIdxStart; i <= patIdxEnd; i++) {
            if (!patDirs[i].equals("**")) {
                return false;
            }
        }

        return true;
    }

    public static boolean matchAntPathPattern(char[][] patDirs, char[][] strDirs, boolean isCaseSensitive) {
        int patIdxStart = 0;
        int patIdxEnd = patDirs.length - 1;
        int strIdxStart = 0;
        int strIdxEnd = strDirs.length - 1;

        // up to first '**'
        while (patIdxStart <= patIdxEnd && strIdxStart <= strIdxEnd) {
            char[] patDir = patDirs[patIdxStart];
            if (isDoubleStar(patDir)) {
                break;
            }
            if (!match(patDir, strDirs[strIdxStart], isCaseSensitive)) {
                return false;
            }
            patIdxStart++;
            strIdxStart++;
        }
        if (strIdxStart > strIdxEnd) {
            // String is exhausted
            for (int i = patIdxStart; i <= patIdxEnd; i++) {
                if (!isDoubleStar(patDirs[i])) {
                    return false;
                }
            }
            return true;
        } else {
            if (patIdxStart > patIdxEnd) {
                // String not exhausted, but pattern is. Failure.
                return false;
            }
        }

        // up to last '**'
        while (patIdxStart <= patIdxEnd && strIdxStart <= strIdxEnd) {
            char[] patDir = patDirs[patIdxEnd];
            if (isDoubleStar(patDir)) {
                break;
            }
            if (!match(patDir, strDirs[strIdxEnd], isCaseSensitive)) {
                return false;
            }
            patIdxEnd--;
            strIdxEnd--;
        }
        if (strIdxStart > strIdxEnd) {
            // String is exhausted
            for (int i = patIdxStart; i <= patIdxEnd; i++) {
                if (!isDoubleStar(patDirs[i])) {
                    return false;
                }
            }
            return true;
        }

        while (patIdxStart != patIdxEnd && strIdxStart <= strIdxEnd) {
            int patIdxTmp = -1;
            for (int i = patIdxStart + 1; i <= patIdxEnd; i++) {
                if (isDoubleStar(patDirs[i])) {
                    patIdxTmp = i;
                    break;
                }
            }
            if (patIdxTmp == patIdxStart + 1) {
                // '**/**' situation, so skip one
                patIdxStart++;
                continue;
            }
            // Find the pattern between padIdxStart & padIdxTmp in str between
            // strIdxStart & strIdxEnd
            int patLength = (patIdxTmp - patIdxStart - 1);
            int strLength = (strIdxEnd - strIdxStart + 1);
            int foundIdx = -1;
            strLoop:
            for (int i = 0; i <= strLength - patLength; i++) {
                for (int j = 0; j < patLength; j++) {
                    char[] subPat = patDirs[patIdxStart + j + 1];
                    char[] subStr = strDirs[strIdxStart + i + j];
                    if (!match(subPat, subStr, isCaseSensitive)) {
                        continue strLoop;
                    }
                }

                foundIdx = strIdxStart + i;
                break;
            }

            if (foundIdx == -1) {
                return false;
            }

            patIdxStart = patIdxTmp;
            strIdxStart = foundIdx + patLength;
        }

        for (int i = patIdxStart; i <= patIdxEnd; i++) {
            if (!isDoubleStar(patDirs[i])) {
                return false;
            }
        }

        return true;
    }

    private static boolean isDoubleStar(char[] patDir) {
        return patDir != null && patDir.length == 2 && patDir[0] == '*' && patDir[1] == '*';
    }

    public static boolean match(String pattern, String str) {
        return match(pattern, str, true);
    }

    public static boolean match(String pattern, String str, boolean isCaseSensitive) {
        char[] patArr = pattern.toCharArray();
        char[] strArr = str.toCharArray();
        return match(patArr, strArr, isCaseSensitive);
    }

    public static boolean match(char[] patArr, char[] strArr, boolean isCaseSensitive) {
        int patIdxStart = 0;
        int patIdxEnd = patArr.length - 1;
        int strIdxStart = 0;
        int strIdxEnd = strArr.length - 1;
        char ch;

        boolean containsStar = false;
        for (char aPatArr : patArr) {
            if (aPatArr == '*') {
                containsStar = true;
                break;
            }
        }

        if (!containsStar) {
            // No '*'s, so we make a shortcut
            if (patIdxEnd != strIdxEnd) {
                return false; // Pattern and string do not have the same size
            }
            for (int i = 0; i <= patIdxEnd; i++) {
                ch = patArr[i];
                if (ch != '?' && !equals(ch, strArr[i], isCaseSensitive)) {
                    return false; // Character mismatch
                }
            }
            return true; // String matches against pattern
        }

        if (patIdxEnd == 0) {
            return true; // Pattern contains only '*', which matches anything
        }

        // Process characters before first star
        while ((ch = patArr[patIdxStart]) != '*' && strIdxStart <= strIdxEnd) {
            if (ch != '?' && !equals(ch, strArr[strIdxStart], isCaseSensitive)) {
                return false; // Character mismatch
            }
            patIdxStart++;
            strIdxStart++;
        }
        if (strIdxStart > strIdxEnd) {
            // All characters in the string are used. Check if only '*'s are
            // left in the pattern. If so, we succeeded. Otherwise failure.
            for (int i = patIdxStart; i <= patIdxEnd; i++) {
                if (patArr[i] != '*') {
                    return false;
                }
            }
            return true;
        }

        // Process characters after last star
        while ((ch = patArr[patIdxEnd]) != '*' && strIdxStart <= strIdxEnd) {
            if (ch != '?' && !equals(ch, strArr[strIdxEnd], isCaseSensitive)) {
                return false; // Character mismatch
            }
            patIdxEnd--;
            strIdxEnd--;
        }
        if (strIdxStart > strIdxEnd) {
            // All characters in the string are used. Check if only '*'s are
            // left in the pattern. If so, we succeeded. Otherwise failure.
            for (int i = patIdxStart; i <= patIdxEnd; i++) {
                if (patArr[i] != '*') {
                    return false;
                }
            }
            return true;
        }

        // process pattern between stars. padIdxStart and patIdxEnd point
        // always to a '*'.
        while (patIdxStart != patIdxEnd && strIdxStart <= strIdxEnd) {
            int patIdxTmp = -1;
            for (int i = patIdxStart + 1; i <= patIdxEnd; i++) {
                if (patArr[i] == '*') {
                    patIdxTmp = i;
                    break;
                }
            }
            if (patIdxTmp == patIdxStart + 1) {
                // Two stars next to each other, skip the first one.
                patIdxStart++;
                continue;
            }
            // Find the pattern between padIdxStart & padIdxTmp in str between
            // strIdxStart & strIdxEnd
            int patLength = (patIdxTmp - patIdxStart - 1);
            int strLength = (strIdxEnd - strIdxStart + 1);
            int foundIdx = -1;
            strLoop:
            for (int i = 0; i <= strLength - patLength; i++) {
                for (int j = 0; j < patLength; j++) {
                    ch = patArr[patIdxStart + j + 1];
                    if (ch != '?' && !equals(ch, strArr[strIdxStart + i + j], isCaseSensitive)) {
                        continue strLoop;
                    }
                }

                foundIdx = strIdxStart + i;
                break;
            }

            if (foundIdx == -1) {
                return false;
            }

            patIdxStart = patIdxTmp;
            strIdxStart = foundIdx + patLength;
        }

        // All characters in the string are used. Check if only '*'s are left
        // in the pattern. If so, we succeeded. Otherwise failure.
        for (int i = patIdxStart; i <= patIdxEnd; i++) {
            if (patArr[i] != '*') {
                return false;
            }
        }
        return true;
    }

    private static boolean equals(char c1, char c2, boolean isCaseSensitive) {
        if (c1 == c2) {
            return true;
        }
        if (!isCaseSensitive) {
            // NOTE: Try both upper case and lower case as done by String.equalsIgnoreCase()
            if (Character.toUpperCase(c1) == Character.toUpperCase(c2)
                    || Character.toLowerCase(c1) == Character.toLowerCase(c2)) {
                return true;
            }
        }
        return false;
    }

    private static String[] tokenizePathToString(String path, String separator) {
        List<String> ret = new ArrayList<String>();
        StringTokenizer st = new StringTokenizer(path, separator);
        while (st.hasMoreTokens()) {
            ret.add(st.nextToken());
        }
        return ret.toArray(new String[0]);
    }
}
