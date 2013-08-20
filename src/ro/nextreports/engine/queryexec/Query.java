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
package ro.nextreports.engine.queryexec;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Scanner;

/**
 * @author Decebal Suiu
 */
public class Query {

    protected List<QueryChunk> chunks = new ArrayList<QueryChunk>();
    protected List<String> parameterNames = new ArrayList<String>();

    private String text;

    public Query(String text) {        
        setText(removeComments(text));
    }

    public QueryChunk[] getChunks() {
        if ((chunks == null) || (chunks.size() == 0)) {
            return new QueryChunk[0];
        }

        QueryChunk[] chunkArray = new QueryChunk[chunks.size()];
        chunks.toArray(chunkArray);
        return chunkArray;
    }

    public String[] getParameterNames() {
        if ((parameterNames == null) || (parameterNames.size() == 0)) {
            return new String[0];
        }

        String[] parameterNamesArray = new String[parameterNames.size()];
        parameterNames.toArray(parameterNamesArray);
        return parameterNamesArray;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        if (text == null) {
            return;
        }

        this.text = text;
        chunks = new ArrayList<QueryChunk>();
        parameterNames = new ArrayList<String>();

        int end = 0;
        StringBuffer textChunk = new StringBuffer();
        String parameterChunk = null;

        StringTokenizer st = new StringTokenizer(text, "$", true);
        String token = null;
        boolean wasDelim = false;
        while (st.hasMoreTokens()) {
            token = st.nextToken();
            if (token.equals("$")) {
                if (wasDelim) {
                    textChunk.append("$");
                }
                wasDelim = true;
            } else {
                if (token.startsWith("{") && wasDelim) {
                    end = token.indexOf('}');
                    if (end > 0) {
                        if (textChunk.length() > 0) {
                            addTextChunk(textChunk.toString());
                        }
                        parameterChunk = token.substring(1, end);
                        parameterNames.add(parameterChunk);
                        addParameterChunk(parameterChunk);
                        textChunk = new StringBuffer(token.substring(end + 1));
                    } else {
                        if (wasDelim) {
                            textChunk.append("$");
                        }
                        textChunk.append(token);
                    }
                } else {
                    if (wasDelim) {
                        textChunk.append("$");
                    }
                    textChunk.append(token);
                }

                wasDelim = false;
            }
        }
        if (wasDelim) {
            textChunk.append("$");
        }
        if (textChunk.length() > 0) {
            this.addTextChunk(textChunk.toString());
        }
    }

    private void addTextChunk(String text) {
        QueryChunk chunk = new QueryChunk();
        chunk.setType(QueryChunk.TEXT_TYPE);
        chunk.setText(text);
        this.chunks.add(chunk);
    }

    private void addParameterChunk(String text) {
        QueryChunk chunk = new QueryChunk();
        chunk.setType(QueryChunk.PARAMETER_TYPE);
        chunk.setText(text);
        this.chunks.add(chunk);
    }

    private String removeComments(String text) {
        if  (text == null) {
            return text;
        }
        Scanner scanner = new Scanner(text);
        StringBuilder sb = new StringBuilder();
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            int commentIndex = line.indexOf("--");
            if (commentIndex == -1) {
                sb.append(line);
                sb.append("\r\n");
            } else if (commentIndex > 0) {
                line = line.substring(0, commentIndex);
                if(!"".equals(line.trim())) {
                    sb.append(line);
                    sb.append("\r\n");
                }
            }       
        }
        sb.delete(sb.toString().length()-2, sb.toString().length());        
        scanner.close();
        return sb.toString();
    }

}
