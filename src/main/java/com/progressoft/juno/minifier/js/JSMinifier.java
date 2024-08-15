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

import static com.progressoft.juno.util.Constants.*;

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
        if (c >= SPACE || c == NEWLINE || c == EOF) {
            return c;
        }
        if (c == CARRIAGE_RETURN) {
            return NEWLINE;
        }
        return SPACE;
    }

    private int peek() throws IOException {
        int lookaheadChar = in.read();
        in.unread(lookaheadChar);
        return lookaheadChar;
    }

    private int next() throws IOException, UnterminatedCommentException {
        int c = get();
        if (c == FORWARD_SLASH) {
            switch (peek()) {
                case FORWARD_SLASH:
                    skipSingleLineComment();
                    return SPACE;
                case STAR:
                    skipMultiLineComment();
                    return SPACE;
                default:
                    return c;
            }
        }
        return c;
    }

    private void skipSingleLineComment() throws IOException {
        while (true) {
            int c = get();
            if (c <= NEWLINE) {
                break;
            }
        }
    }

    private void skipMultiLineComment() throws IOException, UnterminatedCommentException {
        get();
        while (true) {
            switch (get()) {
                case STAR:
                    if (peek() == FORWARD_SLASH) {
                        get();
                        return;
                    }
                    break;
                case EOF:
                    throw new UnterminatedCommentException();
                default:
            }
        }
    }

    private void handleStringLiteral(Writer out) throws IOException, UnterminatedStringLiteralException {
        while (true) {
            out.write(theFirstChar);
            theFirstChar = get();
            if (theFirstChar == theSecondChar) {
                break;
            }
            if (theFirstChar <= NEWLINE) {
                throw new UnterminatedStringLiteralException();
            }
            if (theFirstChar == BACKWARD_SLASH) {
                out.write(theFirstChar);
                theFirstChar = get();
            }
        }
    }

    private void handleRegExpLiteral(Writer out) throws IOException, UnterminatedRegExpLiteralException, UnterminatedCommentException {
        while (true) {
            theFirstChar = get();
            if (theFirstChar == FORWARD_SLASH) {
                break;
            } else if (theFirstChar == BACKWARD_SLASH) {
                out.write(theFirstChar);
                theFirstChar = get();
            } else if (theFirstChar <= NEWLINE) {
                throw new UnterminatedRegExpLiteralException();
            }
            out.write(theFirstChar);
        }
        theSecondChar = next();
    }

    private void doAction(Action action, Writer out) throws IOException, UnterminatedRegExpLiteralException,
            UnterminatedCommentException, UnterminatedStringLiteralException {
        switch (action) {
            case OUTPUT_COPY_GET:
                out.write(theFirstChar);
                // fall through
            case COPY_GET:
                theFirstChar = theSecondChar;
                if (theFirstChar == SINGLE_QUOTE || theFirstChar == DOUBLE_QUOTE) {
                    handleStringLiteral(out);
                }
                // fall through
            case GET:
                theSecondChar = next();
                if (theSecondChar == FORWARD_SLASH && isPrecedingCharForRegExp(theFirstChar)) {
                    out.write(theFirstChar);
                    out.write(theSecondChar);
                    handleRegExpLiteral(out);
                }
                break;
            default:
                throw new IllegalStateException("Unknown action: " + action);
        }
    }

    private boolean isPrecedingCharForRegExp(int c) {
        return RegExpPrecedingChar.isPrecedingCharForRegExp(c);
    }

    @Override
    public void minify(Writer writer) throws MinificationException, IOException {
        try {
            theFirstChar = NEWLINE;
            doAction(Action.GET, writer);
            while (theFirstChar != EOF) {
                switch (theFirstChar) {
                    case SPACE:
                        if (StringUtils.isAlphanumeric(theSecondChar)) {
                            doAction(Action.OUTPUT_COPY_GET, writer);
                        } else {
                            doAction(Action.COPY_GET, writer);
                        }
                        break;
                    case NEWLINE:
                        handleNewLine(writer);
                        break;
                    default:
                        handleDefaultCase(writer);
                        break;
                }
            }
        } catch (IOException | UnterminatedCommentException
                 | UnterminatedRegExpLiteralException | UnterminatedStringLiteralException e) {
            throw new MinificationException("Minification failed due to Exception.", e);
        } finally {
            writer.close();
        }
    }

    private void handleNewLine(Writer writer) throws IOException, UnterminatedRegExpLiteralException,
            UnterminatedCommentException, UnterminatedStringLiteralException {
        switch (theSecondChar) {
            case OPEN_CURLY:
            case OPEN_SQUARE:
            case OPEN_PARENTHESIS:
            case PLUS:
            case MINUS:
                doAction(Action.OUTPUT_COPY_GET, writer);
                break;
            case SPACE:
                doAction(Action.GET, writer);
                break;
            default:
                if (StringUtils.isAlphanumeric(theSecondChar)) {
                    doAction(Action.OUTPUT_COPY_GET, writer);
                } else {
                    doAction(Action.COPY_GET, writer);
                }
        }
    }

    private void handleDefaultCase(Writer writer) throws IOException, UnterminatedRegExpLiteralException,
            UnterminatedCommentException, UnterminatedStringLiteralException {
        switch (theSecondChar) {
            case SPACE:
                if (StringUtils.isAlphanumeric(theFirstChar)) {
                    doAction(Action.OUTPUT_COPY_GET, writer);
                } else {
                    doAction(Action.GET, writer);
                }
                break;
            case NEWLINE:
                switch (theFirstChar) {
                    case CLOSE_CURLY:
                    case CLOSE_SQUARE:
                    case CLOSE_PARENTHESIS:
                    case PLUS:
                    case MINUS:
                    case DOUBLE_QUOTE:
                    case SINGLE_QUOTE:
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
