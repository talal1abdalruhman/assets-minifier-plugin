package com.progressoft.juno.util.scanner;

import java.io.File;
import java.util.Comparator;

public interface Scanner {
    void setIncludes(String[] includes);
    void setExcludes(String[] excludes);
    void addDefaultExcludes();
    void scan();
    String[] getIncludedFiles();
}
