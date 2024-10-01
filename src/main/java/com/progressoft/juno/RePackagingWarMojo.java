package com.progressoft.juno;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.War;
import org.apache.tools.ant.types.FileSet;

import java.io.File;

@Mojo(name = "packaging", defaultPhase = LifecyclePhase.INSTALL)
public class RePackagingWarMojo extends AbstractMojo {

    @Parameter(property = "destinationFile", defaultValue = "target/${project.build.finalName}.war")
    private File destinationFile;

    @Parameter(property = "targetDirectory", defaultValue = "target/${project.build.finalName}")
    private File targetDirectory;

    public void execute() throws MojoExecutionException {
        getLog().info("Building war: " + destinationFile);

        try {
            Project project = new Project();
            project.init();
            War warTask = new War();
            warTask.setProject(project);
            warTask.setBasedir(targetDirectory);
            warTask.setDestFile(destinationFile);
            FileSet fileSet = new FileSet();
            fileSet.setDir(targetDirectory);
            warTask.addFileset(fileSet);
            warTask.execute();
            getLog().info("WAR repackaged successfully.");
        } catch (Exception e) {
            throw new MojoExecutionException("Failed to repackage the WAR file", e);
        }
    }
}

