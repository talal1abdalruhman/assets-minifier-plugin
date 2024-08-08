package com.progressoft.juno.minifier;

import com.progressoft.juno.minifier.exception.MinificationException;

import java.io.Writer;

public interface GeneralMinifier {
    void minify(Writer writer) throws MinificationException;
}