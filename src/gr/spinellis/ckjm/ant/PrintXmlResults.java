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

import gr.spinellis.ckjm.CkjmOutputHandler;
import gr.spinellis.ckjm.ClassMetrics;

import java.io.PrintStream;

/**
 * XML output formatter
 *
 * @author Julien Rentrop
 */
public class PrintXmlResults implements CkjmOutputHandler {
    private final PrintStream printStream;

    public PrintXmlResults(PrintStream printStream) {
        this.printStream = printStream;
    }

    public void printHeader() {
        printStream.println("<?xml version=\"1.0\"?>");
        printStream.println("<ckjm>");
    }

    public void handleClass(String name, ClassMetrics classMetrics) {
        printStream.printf("<class>\n" +
                "<name>%s</name>\n" +
                "<wmc>%d</wmc>\n" +
                "<dit>%d</dit>\n" +
                "<noc>%d</noc>\n" +
                "<cbo>%d</cbo>\n" +
                "<rfc>%d</rfc>\n" +
                "<lcom>%d</lcom>\n" +
                "<ca>%d</ca>\n" +
                "<npm>%d</npm>\n" +
                "</class>\n",
                name, classMetrics.getWmc(), classMetrics.getDit(), classMetrics.getNoc(),
                classMetrics.getCbo(), classMetrics.getRfc(), classMetrics.getLcom(),
                classMetrics.getCa(), classMetrics.getNpm());
    }

    public void printFooter() {
        printStream.println("</ckjm>");
    }
}

