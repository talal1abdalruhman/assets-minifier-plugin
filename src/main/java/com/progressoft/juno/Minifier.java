package com.progressoft.juno;

import com.progressoft.juno.minifier.GeneralMinifier;
import com.progressoft.juno.minifier.css.CSSMinifier;
import com.progressoft.juno.minifier.exception.MinificationException;
import com.progressoft.juno.minifier.js.JSMinifier;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Collections;
import java.util.List;

import static com.progressoft.juno.util.GeneralUtils.*;


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

    @Override
    public void execute() throws MojoFailureException {
        validateMinifyParameters();
        minify(JSMinifier.class, getJsFilenames());
        minify(CSSMinifier.class, getCssFilenames());
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
                getLog().info(getMinificationResult(s, infile, outfile));
            } catch (MinificationException | IOException | NoSuchMethodException | SecurityException
                     | InstantiationException | IllegalAccessException | IllegalArgumentException
                     | InvocationTargetException e) {
                throw new MojoFailureException("Unable to minify resources.");
            }
        }
    }

    private List<String> getJsFilenames() {
        if (jsFilenames == null && isEnabledMinify(minifyJs)) {
            jsFilenames = getScannedFileNamesList(sourceDir, jsIncludes, jsExcludes);
        } else {
            jsFilenames = Collections.emptyList();
        }
        return jsFilenames;
    }

    private List<String> getCssFilenames() {
        if (cssFilenames == null && isEnabledMinify(minifyCss)) {
            cssFilenames = getScannedFileNamesList(sourceDir, cssIncludes, cssExcludes);
        } else {
            cssFilenames = Collections.emptyList();
        }
        return cssFilenames;
    }

    private void validateMinifyParameters() throws MojoFailureException {
        validateMinifyFlag(minifyJs, "minifyJs");
        validateMinifyFlag(minifyCss, "minifyCss");
    }
}