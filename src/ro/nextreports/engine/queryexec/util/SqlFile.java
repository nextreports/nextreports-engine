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
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Decebal Suiu
 */
public class SqlFile {

    private String fileName;
    
    public SqlFile(String fileName) {
        this.fileName = fileName;
    }
    
    public List<String> getSqlList() throws Exception {
        File file = new File(fileName);

//        System.out.println();
//        System.out.println("Loading the sql file '" + file.getAbsolutePath() + "' ...");
//        System.out.println();

        BufferedReader in = new BufferedReader(new FileReader(file), 8192);
        
        List<String> sqlList = new ArrayList<String>();
        String line = null;
        StringBuffer sb = new StringBuffer();
        while ((line = in.readLine()) != null) {
            if (!line.startsWith("--")) {
                if (line.trim().length() == 0) { // no new line
                    continue;
                }
                sb.append(line);
                
                if (line.indexOf(";") > 0) {
                    sb.deleteCharAt(sb.length() - 1); // delete ';'
//                    sb.append(System.getProperty("line.separator"));                    
                    sqlList.add(sb.toString());
                    
                    // reset
                    sb = new StringBuffer(); 
                } else {
                    sb.append(System.getProperty("line.separator"));
                }
            }
        }
        
        return sqlList;
    }
    
    public static void main(String[] args) {
        SqlFile sqlFile = new SqlFile("demo.sql");
        try {
            List sqlList = sqlFile.getSqlList();
            for (int i = 0; i < sqlList.size(); i++) {
                System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>");
                System.out.println(sqlList.get(i));
                System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}
