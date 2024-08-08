package com.progressoft.juno.minifier;

import com.progressoft.juno.minifier.exception.MinificationException;
import java.io.Reader;
import java.io.Writer;

public abstract class AbstractMinifier implements GeneralMinifier {

    private final Reader reader;

    protected AbstractMinifier(Reader reader) {
        this.reader = reader;
    }

    protected Reader reader() {
        return reader;
    }

    @Override
    public abstract void minify(Writer writer) throws MinificationException;
}
