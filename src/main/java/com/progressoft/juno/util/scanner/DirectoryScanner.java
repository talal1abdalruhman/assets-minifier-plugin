package com.progressoft.juno.util.scanner;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class DirectoryScanner extends AbstractScanner {

    private static final String[] EMPTY_STRING_ARRAY = new String[0];

    protected File basedir;

    protected ArrayList<String> filesIncluded;

    protected ArrayList<String> filesNotIncluded;

    protected ArrayList<String> filesExcluded;

    protected ArrayList<String> dirsIncluded;

    protected ArrayList<String> dirsNotIncluded;

    protected ArrayList<String> dirsExcluded;

    protected ArrayList<String> filesDeselected;

    protected ArrayList<String> dirsDeselected;

    protected boolean haveSlowResults = false;

    private boolean followSymlinks = true;

    protected boolean everythingIncluded = true;

    private final char[][] tokenizedEmpty = MatchPattern.tokenizePathToCharArray("", File.separator);

    public DirectoryScanner() {}

    public void setBasedir(String basedir) {
        setBasedir(new File(basedir.replace('/', File.separatorChar).replace('\\', File.separatorChar)));
    }

    public void setBasedir(File basedir) {
        this.basedir = basedir;
    }

    public void setFollowSymlinks(boolean followSymlinks) {
        this.followSymlinks = followSymlinks;
    }

    public boolean isEverythingIncluded() {
        return everythingIncluded;
    }

    @Override
    public void scan() throws IllegalStateException {
        if (basedir == null) {
            throw new IllegalStateException("No basedir set");
        }
        if (!basedir.exists()) {
            throw new IllegalStateException("basedir " + basedir + " does not exist");
        }
        if (!basedir.isDirectory()) {
            throw new IllegalStateException("basedir " + basedir + " is not a directory");
        }

        setupDefaultFilters();
        setupMatchPatterns();

        filesIncluded = new ArrayList<String>();
        filesNotIncluded = new ArrayList<String>();
        filesExcluded = new ArrayList<String>();
        filesDeselected = new ArrayList<String>();
        dirsIncluded = new ArrayList<String>();
        dirsNotIncluded = new ArrayList<String>();
        dirsExcluded = new ArrayList<String>();
        dirsDeselected = new ArrayList<String>();

        if (isIncluded("", tokenizedEmpty)) {

            if (!isExcluded("", tokenizedEmpty)) {
                if (isSelected("", basedir)) {
                    dirsIncluded.add("");
                } else {
                    dirsDeselected.add("");
                }
            } else {
                dirsExcluded.add("");
            }
        } else {
            dirsNotIncluded.add("");
        }
        scandir(basedir, "", true);
    }

    protected void slowScan() {
        if (haveSlowResults) {
            return;
        }

        String[] excl = dirsExcluded.toArray(EMPTY_STRING_ARRAY);
        String[] notIncl = dirsNotIncluded.toArray(EMPTY_STRING_ARRAY);

        for (String anExcl : excl) {
            if (!couldHoldIncluded(anExcl)) {
                scandir(new File(basedir, anExcl), anExcl + File.separator, false);
            }
        }

        for (String aNotIncl : notIncl) {
            if (!couldHoldIncluded(aNotIncl)) {
                scandir(new File(basedir, aNotIncl), aNotIncl + File.separator, false);
            }
        }

        haveSlowResults = true;
    }

    protected void scandir(File dir, String vpath, boolean fast) {
        String[] newfiles = dir.list();

        if (newfiles == null) {
            newfiles = EMPTY_STRING_ARRAY;
        }

        if (!followSymlinks) {
            try {
                if (isParentSymbolicLink(dir, null)) {
                    for (String newfile : newfiles) {
                        String name = vpath + newfile;
                        File file = new File(dir, newfile);
                        if (file.isDirectory()) {
                            dirsExcluded.add(name);
                        } else {
                            filesExcluded.add(name);
                        }
                    }
                    return;
                }
            } catch (IOException ioe) {
                String msg = "IOException caught while checking for links!";
                System.err.println(msg);
            }
        }

        if (filenameComparator != null) {
            Arrays.sort(newfiles, filenameComparator);
        }

        for (String newfile : newfiles) {
            String name = vpath + newfile;
            char[][] tokenizedName = MatchPattern.tokenizePathToCharArray(name, File.separator);
            File file = new File(dir, newfile);
            if (file.isDirectory()) {

                if (isIncluded(name, tokenizedName)) {
                    if (!isExcluded(name, tokenizedName)) {
                        if (isSelected(name, file)) {
                            dirsIncluded.add(name);
                            if (fast) {
                                scandir(file, name + File.separator, fast);
                            }
                        } else {
                            everythingIncluded = false;
                            dirsDeselected.add(name);
                            if (fast && couldHoldIncluded(name)) {
                                scandir(file, name + File.separator, fast);
                            }
                        }

                    } else {
                        everythingIncluded = false;
                        dirsExcluded.add(name);
                        if (fast && couldHoldIncluded(name)) {
                            scandir(file, name + File.separator, fast);
                        }
                    }
                } else {
                    everythingIncluded = false;
                    dirsNotIncluded.add(name);
                    if (fast && couldHoldIncluded(name)) {
                        scandir(file, name + File.separator, fast);
                    }
                }
                if (!fast) {
                    scandir(file, name + File.separator, fast);
                }
            } else if (file.isFile()) {
                if (isIncluded(name, tokenizedName)) {
                    if (!isExcluded(name, tokenizedName)) {
                        if (isSelected(name, file)) {
                            filesIncluded.add(name);
                        } else {
                            everythingIncluded = false;
                            filesDeselected.add(name);
                        }
                    } else {
                        everythingIncluded = false;
                        filesExcluded.add(name);
                    }
                } else {
                    everythingIncluded = false;
                    filesNotIncluded.add(name);
                }
            }
        }
    }


    protected boolean isSelected(String name, File file) {
        return true;
    }

    @Override
    public String[] getIncludedFiles() {
        return filesIncluded.toArray(EMPTY_STRING_ARRAY);
    }

    public boolean isParentSymbolicLink(File parent, String name) throws IOException {
        return NioFiles.isSymbolicLink(parent);
    }
}
