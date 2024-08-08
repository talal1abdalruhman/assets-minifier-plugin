package com.progressoft.juno.util;


public class StringUtils {

    private StringUtils() {}

    public static String replace(String text, char repl, char with) {
        return replace(text, repl, with, -1);
    }

    public static String replace(String text, String repl, String with) {
        return replace(text, repl, with, -1);
    }

    private static String replace(String text, char repl, char with, int max) {
        return replace(text, String.valueOf(repl), String.valueOf(with), max);
    }


    private static String replace(String text, String repl, String with, int max) {
        if ((text == null) || (repl == null) || (with == null) || (repl.isEmpty())) {
            return text;
        }

        StringBuilder buf = new StringBuilder(text.length());
        int start = 0, end;
        while ((end = text.indexOf(repl, start)) != -1) {
            buf.append(text, start, end).append(with);
            start = end + repl.length();

            if (--max == 0) {
                break;
            }
        }
        buf.append(text, start, text.length());
        return buf.toString();
    }

    public static boolean isAlphanumeric(final int c) {
        return ((c >= 'a' && c <= 'z') || (c >= '0' && c <= '9') || (c >= 'A' && c <= 'Z') || c == '_' || c == '$'
                || c == '\\' || c > 126);
    }
}
