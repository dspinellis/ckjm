/*
 * $Id: \\dds\\src\\Research\\ckjm.RCS\\src\\gr\\spinellis\\ckjm\\ant\\CkjmTask.java,v 1.1 2005/05/11 20:48:32 dds Exp $
 *
 * (C) Copyright 2005 Diomidis Spinellis, Julien Rentrop
 *
 * Permission to use, copy, and distribute this software and its
 * documentation for any purpose and without fee is hereby granted,
 * provided that the above copyright notice appear in all copies and that
 * both that copyright notice and this permission notice appear in
 * supporting documentation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND WITHOUT ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE IMPLIED WARRANTIES OF
 * MERCHANTIBILITY AND FITNESS FOR A PARTICULAR PURPOSE.
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

/**
 * Ant task definition for the CKJM metrics tool.
 *
 * @version $Revision: 1.1 $
 * @author Julien Rentrop
 */
public class CkjmTask extends MatchingTask {
    private File outputFile;

    private File classDir;

    private String format;

    public CkjmTask() {
        this.format = "plain";
    }

    /**
     * Sets the format of the output file.
     *
     * @param format
     *            the format of the output file. Allowable values are 'plain' or
     *            'xml'.
     */
    public void setFormat(String format) {
        this.format = format;

    }

    /**
     * Sets the outputfile
     *
     * @param outputfile
     *            Location of outputfile
     */
    public void setOutputfile(File outputfile) {
        this.outputFile = outputfile;
    }

    /**
     * Sets the dir which contains the class files that will be analyzed
     *
     * @param outputfile
     *            Location of outputfile
     */
    public void setClassdir(File classDir) {
        this.classDir = classDir;
    }

    /**
     * Executes the CKJM Ant Task. This method redirects the output of the CKJM
     * tool to a file. When XML format is used it will buffer the output and
     * translate it to the XML format.
     *
     * @throws BuildException
     *             if an error occurs.
     */
    public void execute() throws BuildException {
        if (classDir == null) {
            throw new BuildException("classdir attribute must be set!");
        }
        if (!classDir.exists()) {
            throw new BuildException("classdir does not exist!");
        }
        if (!classDir.isDirectory()) {
            throw new BuildException("classdir is not a directory!");
        }

        DirectoryScanner ds = super.getDirectoryScanner(classDir);

        String files[] = ds.getIncludedFiles();
        if (files.length == 0) {
            log("No class files in specified directory " + classDir);
        } else {
            for (int i = 0; i < files.length; i++) {
                files[i] = classDir.getPath() + File.separatorChar + files[i];
            }

            try {
                OutputStream outputStream = new FileOutputStream(outputFile);

                if (format.equals("xml")) {
                    PrintXmlResults outputXml = new PrintXmlResults(
                            new PrintStream(outputStream));

                    outputXml.printHeader();
                    MetricsFilter.runMetrics(files, outputXml);
                    outputXml.printFooter();
                } else {
                    PrintPlainResults outputPlain = new PrintPlainResults(
                            new PrintStream(outputStream));
                    MetricsFilter.runMetrics(files, outputPlain);
                }

                outputStream.close();

            } catch (IOException ioe) {
                throw new BuildException("Error file handling: "
                        + ioe.getMessage());
            }
        }
    }
}
