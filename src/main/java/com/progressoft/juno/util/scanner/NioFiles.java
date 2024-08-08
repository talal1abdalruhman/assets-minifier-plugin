package com.progressoft.juno.util.scanner;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.PosixFilePermission;
import java.util.HashSet;
import java.util.Set;


@SuppressWarnings("Since15")
public class NioFiles {
    public static boolean isSymbolicLink(File file) {
        return Files.isSymbolicLink(file.toPath());
    }

    public static void chmod(File file, int mode) throws IOException {
        Path path = file.toPath();
        if (!Files.isSymbolicLink(path)) {
            Files.setPosixFilePermissions(path, getPermissions(mode));
        }
    }

    @SuppressWarnings({"OctalInteger", "MagicNumber"})
    private static Set<PosixFilePermission> getPermissions(int mode) {
        Set<PosixFilePermission> perms = new HashSet<>();
        // add owners permission
        if ((mode & 0400) > 0) {
            perms.add(PosixFilePermission.OWNER_READ);
        }
        if ((mode & 0200) > 0) {
            perms.add(PosixFilePermission.OWNER_WRITE);
        }
        if ((mode & 0100) > 0) {
            perms.add(PosixFilePermission.OWNER_EXECUTE);
        }
        // add group permissions
        if ((mode & 0040) > 0) {
            perms.add(PosixFilePermission.GROUP_READ);
        }
        if ((mode & 0020) > 0) {
            perms.add(PosixFilePermission.GROUP_WRITE);
        }
        if ((mode & 0010) > 0) {
            perms.add(PosixFilePermission.GROUP_EXECUTE);
        }
        // add others permissions
        if ((mode & 0004) > 0) {
            perms.add(PosixFilePermission.OTHERS_READ);
        }
        if ((mode & 0002) > 0) {
            perms.add(PosixFilePermission.OTHERS_WRITE);
        }
        if ((mode & 0001) > 0) {
            perms.add(PosixFilePermission.OTHERS_EXECUTE);
        }
        return perms;
    }

    public static long getLastModified(File file) throws IOException {
        BasicFileAttributes basicFileAttributes = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
        return basicFileAttributes.lastModifiedTime().toMillis();
    }

    public static File readSymbolicLink(File symlink) throws IOException {
        Path path = Files.readSymbolicLink(symlink.toPath());
        return path.toFile();
    }

    public static File createSymbolicLink(File symlink, File target) throws IOException {
        Path link = symlink.toPath();
        if (Files.exists(link, LinkOption.NOFOLLOW_LINKS)) {
            Files.delete(link);
        }
        link = Files.createSymbolicLink(link, target.toPath());
        return link.toFile();
    }

    public static boolean deleteIfExists(File file) throws IOException {
        return Files.deleteIfExists(file.toPath());
    }

    public static File copy(File source, File target) throws IOException {
        Path copy = Files.copy(
                source.toPath(),
                target.toPath(),
                StandardCopyOption.REPLACE_EXISTING,
                StandardCopyOption.COPY_ATTRIBUTES);
        return copy.toFile();
    }
}
