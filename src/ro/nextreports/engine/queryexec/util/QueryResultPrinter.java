/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ro.nextreports.engine.queryexec.util;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import ro.nextreports.engine.queryexec.QueryException;
import ro.nextreports.engine.queryexec.QueryResult;


/**
 * @author Decebal Suiu
 */
public class QueryResultPrinter {

    public static void printResult(QueryResult result) throws  QueryException {
        printResult(result, System.out);
    }
    
    /**
     * This method attempts to output the contents of a ResultSet in a textual
     * table. It relies on the ResultSetMetaData class, but a fair bit of the
     * code is simple string manipulation.
     */
    public static void printResult(QueryResult result, OutputStream output)
            throws QueryException {
        // Set up the output stream
        PrintWriter out = new PrintWriter(new OutputStreamWriter(output));

        // Get some "meta data" (column names, etc.) about the results
        ResultSetMetaData metadata;
        try {
            metadata = result.getResultSet().getMetaData();
        } catch (SQLException e) {
            throw new QueryException(e);
        }

        // Variables to hold important data about the table to be displayed
        int numcols;
        try {
            numcols = metadata.getColumnCount(); // how many columns
        } catch (SQLException e) {
            throw new QueryException(e);
        }
        String[] labels = new String[numcols]; // the column labels
        int[] colwidths = new int[numcols]; // the width of each
        int[] colpos = new int[numcols]; // start position of each
        int linewidth; // total width of table

        // Figure out how wide the columns are, where each one begins,
        // how wide each row of the table will be, etc.
        linewidth = 1; // for the initial '|'.
        for (int i = 0; i < numcols; i++) { // for each column
            colpos[i] = linewidth; // save its position
            int size;
            try {
                labels[i] = metadata.getColumnLabel(i + 1); // get its label
                // Get the column width. If the db doesn't report one, guess
                // 30 characters. Then check the length of the label, and use
                // it if it is larger than the column width
                size = metadata.getColumnDisplaySize(i + 1);
            } catch (SQLException e) {
                throw new QueryException(e);
            } 
            if (size == -1) {
                size = 30; // some drivers return -1...
            }
            int labelsize = labels[i].length();
            if (labelsize > size) {
                size = labelsize;
            }
            colwidths[i] = size + 1; // save the column the size
            linewidth += colwidths[i] + 2; // increment total size
        }

        // Create a horizontal divider line we use in the table.
        // Also create a blank line that is the initial value of each
        // line of the table
        StringBuffer divider = new StringBuffer(linewidth);
        StringBuffer blankline = new StringBuffer(linewidth);
        for (int i = 0; i < linewidth; i++) {
            divider.insert(i, '-');
            blankline.insert(i, " ");
        }
        // Put special marks in the divider line at the column positions
        for (int i = 0; i < numcols; i++) {
            divider.setCharAt(colpos[i] - 1, '+');
        }
        divider.setCharAt(linewidth - 1, '+');

        // Begin the table output with a divider line
        out.println(divider);

        // The next line of the table contains the column labels.
        // Begin with a blank line, and put the column names and column
        // divider characters "|" into it. overwrite() is defined below.
        StringBuffer line = new StringBuffer(blankline.toString());
        line.setCharAt(0, '|');
        for (int i = 0; i < numcols; i++) {
            int pos = colpos[i] + 1 + (colwidths[i] - labels[i].length()) / 2;
            overwrite(line, pos, labels[i]);
            overwrite(line, colpos[i] + colwidths[i], " |");
        }

        // Then output the line of column labels and another divider
        out.println(line);
        out.println(divider);

        // Now, output the table data. Loop through the QueryResult, using
        // the hasNext() method to get the rows one at a time. Obtain the
        // value of each column with nextValue(), and output it, much as
        // we did for the column labels above.
        while (result.hasNext()) {
            line = new StringBuffer(blankline.toString());
            line.setCharAt(0, '|');
            for (int i = 0; i < numcols; i++) {
                Object value = result.nextValue(i + 1);
                if (value != null) {
                    overwrite(line, colpos[i] + 1, value.toString().trim());
                } else {
//                    overwrite(line, colpos[i] + 1, value.toString().trim());
                }
                overwrite(line, colpos[i] + colwidths[i], " |");
            }
            out.println(line);
        }

        // Finally, end the table with one last divider line.
        out.println(divider);
        out.flush();
    }

    /**
     * This utility method is used when printing the table of results.
     */
    static void overwrite(StringBuffer sb, int pos, String s) {
        int len = s.length();
        for (int i = 0; i < len; i++) {
            sb.setCharAt(pos + i, s.charAt(i));
        }
    }

}
