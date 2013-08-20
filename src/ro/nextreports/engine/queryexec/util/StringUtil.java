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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

/**
 * @author Decebal Suiu
 */
public class StringUtil {

    private static final String SPACE = " ";
    private static final String COMMENT_MULTILINE_START = "/*";
    private static final String COMMENT_MULTILINE_END = "*/";
    private static final String COMMENT_SINGLELINE_START = "//";
    
    /**
     * Remove all blank lines from a string. A blank line is defined to be a
     * line where the only characters are whitespace. We always ensure that the
     * line contains a newline at the end.
     * 
     * @param text
     *            The string to strip blank lines from
     * @return The blank line stripped reply
     */
    public static String stripBlankLines(String text) {
        if (text == null) {
            return null;
        }

        try {
            StringBuffer output = new StringBuffer();
            BufferedReader in = new BufferedReader(new StringReader(text));
            boolean doneOneLine = false;
            while (true) {
                String line = in.readLine();
                if (line == null) {
                    break;
                }

                if (line.trim().length() > 0) {
                    output.append(line);
                    output.append('\n');
                    doneOneLine = true;
                }
            }

            if (!doneOneLine) {
                output.append('\n');
            }

            return output.toString();
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Remove all newline characters from a string.
     * 
     * @param text
     *            The string to strip newline characters from
     * @return The stripped reply
     */
    public static String stripNewlines(String text) {
        if (text == null) {
            return null;
        }

        try {
            StringBuffer output = new StringBuffer();

            BufferedReader in = new BufferedReader(new StringReader(text));
            while (true) {
                String line = in.readLine();
                if (line == null) {
                    break;
                }

                output.append(line);
                output.append(SPACE);
            }
            output.append('\n');

            return output.toString();
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }
    
    /**
     * Remove all the multi-line comments from a block of text
     * @param text The text to remove multi-line comments from
     * @return The multi-line comment free text
     */
    public static String stripMultiLineComments(String text) {
        if (text == null) {
            return null;
        }

        try {
            StringBuffer output = new StringBuffer();

            // Comment rules:
            /*/           This is still a comment
            /* /* */      // Comments do not nest
            // /* */      This is in a comment
            /* // */      // The second // is needed to make this a comment.

            // First we strip multi line comments. I think this is important:
            boolean inMultiLine = false;
            BufferedReader in = new BufferedReader(new StringReader(text));
            while (true) {
                String line = in.readLine();
                if (line == null) {
                    break;
                }

                if (!inMultiLine) {
                    // We are not in a multi-line comment, check for a start
                    int cstart = line.indexOf(COMMENT_MULTILINE_START);
                    if (cstart >= 0) {
                        // This could be a MLC on one line ...
                        int cend = line.indexOf(COMMENT_MULTILINE_END, cstart + COMMENT_MULTILINE_START.length());
                        if (cend >= 0) {
                            // A comment that starts and ends on one line
                            // BUG: you can have more than 1 multi-line comment on a line
                            line = line.substring(0, cstart) + SPACE + line.substring(cend + COMMENT_MULTILINE_END.length());
                        } else {
                            // A real multi-line comment
                            inMultiLine = true;
                            line = line.substring(0, cstart) + SPACE;
                        }
                    } else {
                        // We are not in a multi line comment and we havn't
                        // started one so we are going to ignore closing
                        // comments even if they exist.
                    }
                } else {
                    // We are in a multi-line comment, check for the end
                    int cend = line.indexOf(COMMENT_MULTILINE_END);
                    if (cend >= 0) {
                        // End of comment
                        line = line.substring(cend + COMMENT_MULTILINE_END.length());
                        inMultiLine = false;
                    } else {
                        // The comment continues
                        line = SPACE;
                    }
                }

                output.append(line);
                output.append('\n');
            }

            return output.toString();
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }
    
    /**
     * Remove all the single-line comments from a block of text
     * @param text The text to remove single-line comments from
     * @return The single-line comment free text
     */
    public static String stripSingleLineComments(String text) {
        if (text == null) {
            return null;
        }

        try {
            StringBuffer output = new StringBuffer();

            // First we strip multi line comments. I think this is important:
            BufferedReader in = new BufferedReader(new StringReader(text));
            while (true) {
                String line = in.readLine();
                if (line == null) {
                    break;
                }

                int cstart = line.indexOf(COMMENT_SINGLELINE_START);
                if (cstart >= 0) {
                    line = line.substring(0, cstart);
                }

                output.append(line);
                output.append('\n');
            }

            return output.toString();
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }
    
    /**
     * Remove any leading or trailing spaces from a line of code.
     * This function could be improved by making it strip unnecessary double
     * spaces, but since we would need to leave double spaces inside strings
     * this is not simple and since the benefit is small, we'll leave it for now
     * @param text The javascript program to strip spaces from.
     * @return The stripped program
     */
    public static String trimLines(String text) {
        if (text == null) {
            return null;
        }

        try {
            StringBuffer output = new StringBuffer();

            // First we strip multi line comments. I think this is important:
            BufferedReader in = new BufferedReader(new StringReader(text));
            while (true) {
                String line = in.readLine();
                if (line == null) {
                    break;
                }

                output.append(line.trim());
                output.append('\n');
            }

            return output.toString();
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }
    
    /**
     * Replaces all instances of the tab character in <code>text</code> with
     * the spaces.
     *
     * @param text The <code>java.lang.String</code> in which to replace tabs
     *        with spaces.
     * @return A <code>java.lang.String</code> just like <code>text</code>,
     *         but with spaces instead of tabs.
     */
    public static String replaceTabsWithSpaces(String text, int tabSize) {
        String tabText = "";
        for (int i = 0; i < tabSize; i++) {
            tabText += " ";
        }

        return text.replaceAll("\t", " ");
    }    
    
    /**
     * Only one space between words.
     */
    public static String deleteExcededSpaces(String text) {
        int size = text.length();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < size; i++) {
            char ch = text.charAt(i);
            if (ch == ' ') {
               if (i > 0) {
                   char ch2 = text.charAt(i-1);
                   if ((ch2 != ' ') && (i < size-1) && (!text.substring(i+1).trim().equals(""))) {
                       sb.append(ch);
                   }
               }
            } else {
               sb.append(ch);
            }
        }
        
        return sb.toString();
    }

}
