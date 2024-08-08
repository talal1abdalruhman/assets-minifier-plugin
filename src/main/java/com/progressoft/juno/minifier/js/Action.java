package com.progressoft.juno.minifier.js;

enum Action {
    /**
     * Output FirstChar, copy SecondChar to FirstChar, get the next SecondChar
     */
    OUTPUT_COPY_GET,

    /**
     * Copy SecondChar to FirstChar, get the next SecondChar
     */
    COPY_GET,

    /**
     * Get the next SecondChar
     */
    GET;
}
