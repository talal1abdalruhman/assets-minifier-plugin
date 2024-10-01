package com.progressoft.juno;

import com.progressoft.juno.util.FileUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.io.IOException;

@Mojo(name = "copy-minified-files", defaultPhase = LifecyclePhase.INSTALL)
public class CopyMinifiedFilesMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project.build.directory}/${project.build.finalName}-min")
    private File minifiedDirectory;

    @Parameter(defaultValue = "${project.build.directory}/${project.build.finalName}")
    private File outputDirectory;

    public void execute() throws MojoExecutionException {
        getLog().info("Copying minified files from: " + minifiedDirectory+ "\nto: " + outputDirectory);

        if (!minifiedDirectory.exists()) {
            throw new MojoExecutionException("Minified directory does not exist: " + minifiedDirectory.getAbsolutePath());
        }
        if (!outputDirectory.exists() && !outputDirectory.mkdirs()) {
            throw new MojoExecutionException("Could not create output directory: " + outputDirectory.getAbsolutePath());
        }
        int filesCount = 0;
        try {
            filesCount = FileUtils.copyDirectoryStructure(minifiedDirectory, outputDirectory);
        } catch (IOException e) {
            throw new MojoExecutionException("Error copying minified files", e);
        }

        getLog().info(filesCount +" minified files copied successfully.");
    }
}

