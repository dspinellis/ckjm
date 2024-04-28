/*
 * (C) Copyright 2005 Diomidis Spinellis, Julien Rentrop
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package gr.spinellis.ckjm.ant;

import gr.spinellis.ckjm.MetricsFilter;
import gr.spinellis.ckjm.PrintPlainResults;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.taskdefs.MatchingTask;
import org.apache.tools.ant.types.Path;

/**
 * Ant task definition for the CKJM metrics tool.
 *
 * @version $Revision: 1.3 $
 * @author Julien Rentrop
 */
public class CkjmTask extends MatchingTask {
    private static final String FORMAT_PLAIN = "plain";
    private static final String FORMAT_XML = "xml";

    private File outputFile;
    private File classDir;
    private Path extdirs;
    private String format;

    public CkjmTask() {
        this.format = FORMAT_PLAIN;
    }

    /**
     * Sets the format of the output file.
     *
     * @param format the format of the output file. Allowable values are 'plain' or 'xml'.
     */
    public void setFormat(String format) {
        this.format = format;
    }

    /**
     * Sets the output file.
     *
     * @param outputFile Location of the output file
     */
    public void setOutputfile(File outputFile) {
        this.outputFile = outputFile;
    }

    /**
     * Sets the directory which contains the class files that will be analyzed.
     *
     * @param classDir Location of class files
     */
    public void setClassdir(File classDir) {
        this.classDir = classDir;
    }

    /**
     * Sets the extension directories that will be used by CKJM.
     *
     * @param extdirs a path containing .jar files
     */
    public void setExtdirs(Path extdirs) {
        this.extdirs = extdirs;
    }

    /**
     * Gets the extension directories that will be used by CKJM.
     *
     * @return the extension directories as a path
     */
    public Path getExtdirs() {
        return extdirs;
    }

    /**
     * Adds a path to extdirs.
     *
     * @return a path to be modified
     */
    public Path createExtdirs() {
        if (extdirs == null) {
            extdirs = new Path(getProject());
        }
        return extdirs.createPath();
    }

    /**
     * Executes the CKJM Ant Task.
     *
     * @throws BuildException if an error occurs.
     */
    public void execute() {
        if (classDir == null) {
            throw new BuildException("classdir attribute must be set!");
        }
        if (!classDir.exists()) {
            throw new BuildException("classdir does not exist!");
        }
        if (!classDir.isDirectory()) {
            throw new BuildException("classdir is not a directory!");
        }

        if (extdirs != null && extdirs.size() > 0) {
            System.setProperty("java.ext.dirs", extdirs.toString());
        }

        DirectoryScanner ds = super.getDirectoryScanner(classDir);
        String[] files = ds.getIncludedFiles();
        if (files.length == 0) {
            log("No class files in specified directory " + classDir);
        } else {
            for (int i = 0; i < files.length; i++) {
                files[i] = new File(classDir, files[i]).getPath();
            }

            try {
                processMetrics(files, new FileOutputStream(outputFile));
            } catch (IOException ioe) {
                throw new BuildException("Error file handling: " + ioe.getMessage(), ioe);
            }
        }
    }

    private void processMetrics(String[] files, OutputStream outputStream) throws IOException {
        try (OutputStream os = outputStream) {
            if (FORMAT_XML.equals(format)) {
                PrintXmlResults outputXml = new PrintXmlResults(new PrintStream(os));
                outputXml.printHeader();
                MetricsFilter.runMetrics(files, outputXml);
                outputXml.printFooter();
            } else {
                PrintPlainResults outputPlain = new PrintPlainResults(new PrintStream(os));
                MetricsFilter.runMetrics(files, outputPlain);
            }
        }
    }
}