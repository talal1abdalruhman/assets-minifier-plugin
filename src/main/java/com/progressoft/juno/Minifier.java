package com.progressoft.juno;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import com.progressoft.juno.minifier.GeneralMinifier;
import com.progressoft.juno.minifier.css.CSSMinifier;
import com.progressoft.juno.minifier.exception.MinificationException;
import com.progressoft.juno.minifier.js.JSMinifier;
import com.progressoft.juno.util.scanner.DirectoryScanner;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;


@Mojo(name = "minify", defaultPhase = LifecyclePhase.PROCESS_RESOURCES)
public class Minifier extends AbstractMojo {

    @Parameter(property = "sourceDir", required = true)
    private String sourceDir;

    @Parameter(property = "targetDir", required = true)
    private String targetDir;

    @Parameter(property = "jsIncludes", defaultValue = "**/*.js")
    private List<String> jsIncludes;

    @Parameter(property = "jsExcludes")
    private List<String> jsExcludes;

    @Parameter(property = "cssIncludes", defaultValue = "**/*.css")
    private List<String> cssIncludes;

    @Parameter(property = "cssExcludes")
    private List<String> cssExcludes;

    @Parameter(property = "minifyJs", defaultValue = "true")
    private String minifyJs;

    @Parameter(property = "minifyCss", defaultValue = "true")
    private String minifyCss;

    private List<String> jsFilenames;

    private List<String> cssFilenames;

    private List<String> jsFilenames() {
        if (jsFilenames == null && isEnabledMinify(minifyJs)) {
            jsFilenames = filenameList(jsIncludes, jsExcludes);
        } else {
            jsFilenames = new ArrayList<>();
        }
        return jsFilenames;
    }

    private List<String> cssFilenames() {
        if (cssFilenames == null && isEnabledMinify(minifyCss)) {
            cssFilenames = filenameList(cssIncludes, cssExcludes);
        } else {
            cssFilenames = new ArrayList<>();
        }
        return cssFilenames;
    }

    private boolean isEnabledMinify(String minifyFlg) {
        return minifyFlg != null && minifyFlg.equalsIgnoreCase("true");
    }

    private List<String> filenameList(List<String> includes, List<String> excludes) {
        List<String> list = new ArrayList<>();
        DirectoryScanner scanner = new DirectoryScanner();
        scanner.setBasedir(sourceDir);
        scanner.setIncludes(includes.toArray(new String[0]));
        scanner.setExcludes(excludes.toArray(new String[0]));
        scanner.addDefaultExcludes();
        scanner.scan();
        for (String s : scanner.getIncludedFiles()) {
            list.add(s);
        }
        return list;
    }

    @Override
    public void execute() throws MojoFailureException {
        minify(JSMinifier.class, jsFilenames());
        minify(CSSMinifier.class, cssFilenames());
    }

    private void minify(Class<? extends GeneralMinifier> minifierClass, List<String> filenames) throws MojoFailureException {
        for (String s : filenames) {
            try {
                File infile = new File(sourceDir, s);
                File outfile = new File(targetDir, s);
                Constructor<? extends GeneralMinifier> constructor = minifierClass.getConstructor(Reader.class);
                GeneralMinifier minifier = constructor.newInstance(new InputStreamReader(Files.newInputStream(infile.toPath()), StandardCharsets.UTF_8));
                Files.createDirectories(outfile.toPath().getParent());
                minifier.minify(new OutputStreamWriter(Files.newOutputStream(outfile.toPath()), StandardCharsets.UTF_8));
                logMinificationResult(s, infile, outfile);
            } catch (MinificationException | IOException | NoSuchMethodException | SecurityException
                     | InstantiationException | IllegalAccessException | IllegalArgumentException
                     | InvocationTargetException e) {
                throw new MojoFailureException("Unable to minify resources.");
            }
        }
    }

    private void logMinificationResult(String name, File infile, File outfile) {
        long pre = infile.length();
        long post = outfile.length();
        long reduction = (long) (100.0 - (((double) post / (double) pre) * 100.0));
        getLog().info("Minified '" + name + "' " + pre + " -> " + post + " (" + reduction + "%)");
    }
}