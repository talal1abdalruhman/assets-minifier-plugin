package com.progressoft.juno.minifier.js;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public enum RegExpPrecedingChar {
    OPEN_PARENTHESIS('('),
    COMMA(','),
    EQUALS('='),
    COLON(':'),
    OPEN_BRACKET('['),
    EXCLAMATION_MARK('!'),
    AMPERSAND('&'),
    PIPE('|'),
    QUESTION_MARK('?'),
    OPEN_CURLY_BRACE('{'),
    CLOSE_CURLY_BRACE('}'),
    SEMICOLON(';'),
    NEWLINE('\n');

    private final char character;

    RegExpPrecedingChar(char character) {
        this.character = character;
    }

    public char getCharacter() {
        return character;
    }

    private static final Set<Character> VALID_CHARS;

    static {
        Set<Character> tempSet = new HashSet<>();
        for (RegExpPrecedingChar c : RegExpPrecedingChar.values()) {
            tempSet.add(c.getCharacter());
        }
        VALID_CHARS = Collections.unmodifiableSet(tempSet);
    }

    public static boolean isPrecedingCharForRegExp(int c) {
        return VALID_CHARS.contains((char) c);
    }
}
