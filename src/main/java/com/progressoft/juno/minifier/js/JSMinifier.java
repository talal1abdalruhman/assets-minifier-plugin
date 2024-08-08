package com.progressoft.juno.minifier.js;

import com.progressoft.juno.minifier.AbstractMinifier;
import com.progressoft.juno.minifier.exception.MinificationException;
import com.progressoft.juno.minifier.exception.UnterminatedCommentException;
import com.progressoft.juno.minifier.exception.UnterminatedRegExpLiteralException;
import com.progressoft.juno.minifier.exception.UnterminatedStringLiteralException;
import com.progressoft.juno.util.StringUtils;

import java.io.IOException;
import java.io.PushbackReader;
import java.io.Reader;
import java.io.Writer;

import static com.progressoft.juno.util.Constants.EOF;

public class JSMinifier extends AbstractMinifier {

    private final PushbackReader in;
    private int theFirstChar;
    private int theSecondChar;

    public JSMinifier(Reader reader) {
        super(reader);
        this.in = new PushbackReader(reader);
    }

    private int get() throws IOException {
        int c = in.read();
        if (c >= ' ' || c == '\n' || c == EOF) {
            return c;
        }
        if (c == '\r') {
            return '\n';
        }
        return ' ';
    }

    private int peek() throws IOException {
        int lookaheadChar = in.read();
        in.unread(lookaheadChar);
        return lookaheadChar;
    }

    private int next() throws IOException, UnterminatedCommentException {
        int c = get();
        if (c == '/') {
            switch (peek()) {
                case '/':
                    for (; ; ) {
                        c = get();
                        if (c <= '\n') {
                            return c;
                        }
                    }
                case '*':
                    get();
                    for (; ; ) {
                        switch (get()) {
                            case '*':
                                if (peek() == '/') {
                                    get();
                                    return ' ';
                                }
                                break;
                            case EOF:
                                throw new UnterminatedCommentException();
                        }
                    }
                default:
                    return c;
            }
        }
        return c;
    }

    private void doAction(Action action, Writer out) throws IOException, UnterminatedRegExpLiteralException,
            UnterminatedCommentException, UnterminatedStringLiteralException {
        switch (action) {
            case OUTPUT_COPY_GET:
                out.write(theFirstChar);
            case COPY_GET:
                theFirstChar = theSecondChar;
                if (theFirstChar == '\'' || theFirstChar == '"') {
                    for (; ; ) {
                        out.write(theFirstChar);
                        theFirstChar = get();
                        if (theFirstChar == theSecondChar) {
                            break;
                        }
                        if (theFirstChar <= '\n') {
                            throw new UnterminatedStringLiteralException();
                        }
                        if (theFirstChar == '\\') {
                            out.write(theFirstChar);
                            theFirstChar = get();
                        }
                    }
                }
            case GET:
                theSecondChar = next();
                if (theSecondChar == '/' && (theFirstChar == '(' || theFirstChar == ',' || theFirstChar == '=' || theFirstChar == ':' || theFirstChar == '[' || theFirstChar == '!'
                        || theFirstChar == '&' || theFirstChar == '|' || theFirstChar == '?' || theFirstChar == '{' || theFirstChar == '}' || theFirstChar == ';'
                        || theFirstChar == '\n')) {
                    out.write(theFirstChar);
                    out.write(theSecondChar);
                    for (; ; ) {
                        theFirstChar = get();
                        if (theFirstChar == '/') {
                            break;
                        } else if (theFirstChar == '\\') {
                            out.write(theFirstChar);
                            theFirstChar = get();
                        } else if (theFirstChar <= '\n') {
                            throw new UnterminatedRegExpLiteralException();
                        }
                        out.write(theFirstChar);
                    }
                    theSecondChar = next();
                }
                break;
            default:
                throw new IllegalStateException("Unknown action: " + action);
        }
    }

    @Override
    public void minify(Writer writer) throws MinificationException {
        try {
            theFirstChar = '\n';
            doAction(Action.GET, writer);
            while (theFirstChar != EOF) {
                switch (theFirstChar) {
                    case ' ':
                        if (StringUtils.isAlphanumeric(theSecondChar)) {
                            doAction(Action.OUTPUT_COPY_GET, writer);
                        } else {
                            doAction(Action.COPY_GET, writer);
                        }
                        break;
                    case '\n':
                        switch (theSecondChar) {
                            case '{':
                            case '[':
                            case '(':
                            case '+':
                            case '-':
                                doAction(Action.OUTPUT_COPY_GET, writer);
                                break;
                            case ' ':
                                doAction(Action.GET, writer);
                                break;
                            default:
                                if (StringUtils.isAlphanumeric(theSecondChar)) {
                                    doAction(Action.OUTPUT_COPY_GET, writer);
                                } else {
                                    doAction(Action.COPY_GET, writer);
                                }
                        }
                        break;
                    default:
                        switch (theSecondChar) {
                            case ' ':
                                if (StringUtils.isAlphanumeric(theFirstChar)) {
                                    doAction(Action.OUTPUT_COPY_GET, writer);
                                    break;
                                }
                                doAction(Action.GET, writer);
                                break;
                            case '\n':
                                switch (theFirstChar) {
                                    case '}':
                                    case ']':
                                    case ')':
                                    case '+':
                                    case '-':
                                    case '"':
                                    case '\'':
                                        doAction(Action.OUTPUT_COPY_GET, writer);
                                        break;
                                    default:
                                        if (StringUtils.isAlphanumeric(theFirstChar)) {
                                            doAction(Action.OUTPUT_COPY_GET, writer);
                                        } else {
                                            doAction(Action.GET, writer);
                                        }
                                }
                                break;
                            default:
                                doAction(Action.OUTPUT_COPY_GET, writer);
                                break;
                        }
                }
            }
        } catch (IOException | UnterminatedCommentException
                 | UnterminatedRegExpLiteralException | UnterminatedStringLiteralException e) {
            throw new MinificationException("Minification failed due to Exception.", e);
        } finally {
            try {
                writer.close();
            } catch (IOException e) {
                throw new MinificationException("Minification failed due to Exception.", e);
            }
        }
    }
}
