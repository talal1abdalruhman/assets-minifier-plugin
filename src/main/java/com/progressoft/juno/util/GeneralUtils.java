package com.progressoft.juno.util;


import com.progressoft.juno.util.scanner.DirectoryScanner;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GeneralUtils {

    private GeneralUtils() {}

    public static String getMinificationResult(String name, File infile, File outfile) {
        long pre = infile.length();
        long post = outfile.length();
        long reduction = (long) (100.0 - (((double) post / (double) pre) * 100.0));
        return "Minified '" + name + "' " + pre + " -> " + post + " (" + reduction + "%)";
    }

    public static boolean isEnabledMinify(String minifyFlg) {
        return minifyFlg != null && minifyFlg.equalsIgnoreCase("true");
    }


    public static List<String> getScannedFileNamesList(String sourceDir, List<String> includes, List<String> excludes) {
        List<String> list = new ArrayList<>();
        DirectoryScanner scanner = new DirectoryScanner();
        scanner.setBasedir(sourceDir);
        scanner.setIncludes(includes.toArray(new String[0]));
        scanner.setExcludes(excludes.toArray(new String[0]));
        scanner.addDefaultExcludes();
        scanner.scan();
        Collections.addAll(list, scanner.getIncludedFiles());
        return list;
    }
}
