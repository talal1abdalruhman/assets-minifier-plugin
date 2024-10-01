package com.progressoft.juno.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;


public class FileUtils {

    public static int copyDirectoryStructure(File sourceDirectory, File destinationDirectory) throws IOException {
        return copyDirectoryStructure(sourceDirectory, destinationDirectory, destinationDirectory);
    }

    private static void copyFile(final File source, final File destination) throws IOException {
        if (!source.exists()) {
            final String message = "File " + source + " does not exist";
            throw new IOException(message);
        }

        if (source.getCanonicalPath().equals(destination.getCanonicalPath())) {
            return;
        }
        mkdirsFor(destination);
        doCopyFile(source, destination);
        if (source.length() != destination.length()) {
            String message = "Failed to copy full contents from " + source + " to " + destination;
            throw new IOException(message);
        }
    }

    private static void mkdirsFor(File destination) {
        File parentFile = destination.getParentFile();
        if (parentFile != null && !parentFile.exists()) {
            parentFile.mkdirs();
        }
    }

    private static void doCopyFile(File source, File destination) throws IOException {
        doCopyFileUsingNewIO(source, destination);
    }

    private static void doCopyFileUsingNewIO(File source, File destination) throws IOException {
        copy(source, destination);
    }

    private static File copy(File source, File target) throws IOException {
        Path copy = Files.copy(
                source.toPath(),
                target.toPath(),
                StandardCopyOption.REPLACE_EXISTING,
                StandardCopyOption.COPY_ATTRIBUTES);
        return copy.toFile();
    }

    private static void copyFileToDirectory(final File source, final File destinationDirectory) throws IOException {
        if (destinationDirectory.exists() && !destinationDirectory.isDirectory()) {
            throw new IllegalArgumentException("Destination is not a directory");
        }
        copyFile(source, new File(destinationDirectory, source.getName()));
    }

    private static int copyDirectoryStructure(File sourceDirectory, File destinationDirectory, File rootDestinationDirectory) throws IOException {
        validateDirectories(sourceDirectory, destinationDirectory);
        File[] files = sourceDirectory.listFiles();
        String sourcePath = sourceDirectory.getAbsolutePath();
        int filesCount = 0;
        for (File file : files) {
            if (file.equals(rootDestinationDirectory)) {
                continue;
            }
            String dest = file.getAbsolutePath();
            dest = dest.substring(sourcePath.length() + 1);
            File destination = new File(destinationDirectory, dest);

            if (file.isFile()) {
                destination = destination.getParentFile();
                copyFileToDirectory(file, destination);
                filesCount++;
            } else if (file.isDirectory()) {
                if (!destination.exists() && !destination.mkdirs()) {
                    throw new IOException("Could not create destination directory '" + destination.getAbsolutePath() + "'.");
                }
                filesCount += copyDirectoryStructure(file, destination, rootDestinationDirectory);
            } else {
                throw new IOException("Unknown file type: " + file.getAbsolutePath());
            }
        }
        return filesCount;
    }

    private static void validateDirectories(File sourceDirectory, File destinationDirectory) throws IOException {
        if (sourceDirectory == null) {
            throw new IOException("source directory can't be null.");
        }

        if (destinationDirectory == null) {
            throw new IOException("destination directory can't be null.");
        }

        if (sourceDirectory.equals(destinationDirectory)) {
            throw new IOException("source and destination are the same directory.");
        }

        if (!sourceDirectory.exists()) {
            throw new IOException("Source directory doesn't exists (" + sourceDirectory.getAbsolutePath() + ").");
        }
    }
}
