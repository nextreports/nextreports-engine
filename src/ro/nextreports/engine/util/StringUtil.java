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
package ro.nextreports.engine.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.FileReader;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Time;
import java.sql.Timestamp;

import com.thoughtworks.xstream.core.util.Base64Encoder;

import ro.nextreports.engine.exporter.util.RomanNumberConverter;
import ro.nextreports.engine.exporter.util.StyleFormatConstants;
import ro.nextreports.engine.i18n.I18nLanguage;
import ro.nextreports.engine.i18n.I18nString;
import ro.nextreports.engine.i18n.I18nUtil;
import ro.nextreports.engine.queryexec.IdName;


/**
 * Created by IntelliJ IDEA.
 * User: mihai.panaitescu
 * Date: Jun 8, 2006
 * Time: 6:05:51 PM
 */
public class StringUtil {
	
	public static String BLOB = "blob";

    public static String capitalize(String str) {
        StringBuilder sb = new StringBuilder();
        char ch;
        char prevCh;
        int i;
        prevCh = '.';  // Prime the loop with any non-letter character.
        for (i = 0; i < str.length(); i++) {
            ch = str.charAt(i);
            if (Character.isLetter(ch) && !Character.isLetter(prevCh)) {
                sb.append(Character.toUpperCase(ch));
            } else {
                sb.append(ch);
            }
            prevCh = ch;
        }
        return sb.toString();
    }
    
    public static void replaceInFile(File file, String oldText, String newText) {
    	replaceInFile(file, oldText, newText, true);
    }

    public static void replaceInFile(File file, String oldText, String newText, boolean casesensitive) {
        Pattern p;
        if (casesensitive) {
        	p = Pattern.compile(oldText);
        } else {
        	p = Pattern.compile(oldText, Pattern.CASE_INSENSITIVE);
        }
        Matcher m = p.matcher(getContents(file));
        StringBuffer sb = new StringBuffer();
        boolean result = m.find();
        while (result) {
            m.appendReplacement(sb, newText);
            result = m.find();
        }
        m.appendTail(sb);
        setContents(file, sb.toString());
    }

    public static String replace(String text, String oldText, String newText) {
        if (newText == null) {
            return text;
        }
        Pattern p = Pattern.compile(oldText);
        Matcher m = p.matcher(text);
        StringBuffer sb = new StringBuffer();
        boolean result = m.find();
        while (result) {
            m.appendReplacement(sb, newText);
            result = m.find();
        }
        m.appendTail(sb);
        return sb.toString();
    }

    public static String getContents(File aFile) {
        //...checks on aFile are elided
        StringBuilder contents = new StringBuilder();

        try {
            //use buffering, reading one line at a time
            //FileReader always assumes default encoding is OK!
            BufferedReader input = new BufferedReader(new FileReader(aFile));
            try {
                String line = null; //not declared within while loop
                /*
                * readLine is a bit quirky :
                * it returns the content of a line MINUS the newline.
                * it returns null only for the END of the stream.
                * it returns an empty String if two newlines appear in a row.
                */
                while ((line = input.readLine()) != null) {
                    contents.append(line);
                    contents.append(System.getProperty("line.separator"));
                }
            }
            finally {
                input.close();
            }
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }

        return contents.toString();
    }

    public static void setContents(File file, String content) {
        try {
            Writer output = new BufferedWriter(new FileWriter(file));
            try {
                //FileWriter always assumes default encoding is OK!
                output.write(content);
            }
            finally {
                output.close();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static String getFirstDuplicateValue(List<String> values) {
        String message = null;
        int size = values.size();
        for (int i = 0; i < size; i++) {
            String col1 = values.get(i);
            for (int j = i + 1; j < size; j++) {
                String col2 = values.get(j);
                if (col1.equals(col2)) {
                    message = col1;
                    return message;
                }
            }
        }
        return message;
    }

    public static boolean isFileName(String s) {

        if (s  == null) {
            return false;
        }

        if (s.startsWith(" ") || s.startsWith(".")) {
            return false;
        }

        if (s.endsWith(" ") || s.endsWith(".")) {
            return false;
        }

        //String regex ="[a-zA-Z0-9\\-_()\\[\\] ]+";
        String regex = "[^\\\\/:*?\\\"<>|%]+";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(s);
        return matcher.matches();            
    }
    
    public static String getValueAsString(Object val, String pattern) {
    	return getValueAsString(val, pattern, null);
    }
    
    public static String getValueAsString(Object val, String pattern, I18nLanguage lang) {
    	return getValueAsString(val, pattern, lang, null);
    }

    public static String getValueAsString(Object val, String pattern, I18nLanguage lang, String parameterValueClass) {
        if (val == null) {
            return null;
        }                               
        
        if (val instanceof IdName) {
        	IdName in = (IdName)val;
        	if ( (in.getName() == null) || in.getId().equals(in.getName())) {
        		val = in.getId();
        	} else {
        		val = in.getName();
        	}
        }
        
        if (val instanceof String) {
            return (String) val;
        }
        
        // i18n pattern
        if ((pattern != null) && pattern.contains(I18nString.MARKUP)) {
        	if (lang != null) {
        		pattern = getI18nString(pattern, lang);
        	}
            		
        }
        
        if (val instanceof Number) {
            if (pattern == null) {                
                return NumberFormat.getNumberInstance().format(((Number) val).doubleValue());
            } else if (StyleFormatConstants.ROMAN_PATTERN.equals(pattern)) {
            	return RomanNumberConverter.romanize( ((Number)val ).intValue() );            
            } else {
                DecimalFormat df = new DecimalFormat(pattern);
                return df.format(((Number) val).doubleValue());
            }
        }
        // should be before Date (because Time extends Date)
        if (val instanceof Time) {        	
            if (pattern == null) {                
            	return DateFormat.getTimeInstance().format((Time)val);
            } else {
                Time d = (Time) val;
                SimpleDateFormat sfd = new SimpleDateFormat(pattern);
                return sfd.format(d);
            }
        }
                
        if (val instanceof Timestamp) {
            if (pattern == null) {                
            	return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format((Timestamp)val);
            } else {
                Timestamp d = (Timestamp) val;
                SimpleDateFormat sfd = new SimpleDateFormat(pattern);
                return sfd.format(d);
            }
        }
        
        if (val instanceof Date) {
            if (pattern == null) {
            	if (parameterValueClass != null) {            		
            		if (parameterValueClass.equals("java.sql.Time")) {
            			return new SimpleDateFormat("HH:mm:ss").format((Date)val);
            		} else if (parameterValueClass.equals("java.sql.Timestamp")) {
            			return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format((Date)val);
            		} else {
            			return new SimpleDateFormat("yyyy-MM-dd").format((Date)val);
            		}
            	} else {
            		return DateFormat.getDateInstance().format((Date)val);
            	}
            } else {            	
                SimpleDateFormat sfd = new SimpleDateFormat(pattern);
                return sfd.format((Date) val);
            }
        }
                
		if (val instanceof Clob) {
			Clob clob = (Clob) val;
			InputStream is = null;
			OutputStream os = null;
			try {
				is = clob.getAsciiStream();
				byte[] buffer = new byte[4096];
				os = new ByteArrayOutputStream();
				while (true) {
					int read = is.read(buffer);
					if (read == -1) {
						break;
					}
					os.write(buffer, 0, read);
				}				
				return os.toString();
			} catch (Exception ex) {
				ex.printStackTrace();
				return "clob exception";
			} finally {
				if (os != null) {
					try {
						os.close();
					} catch (IOException e) {					
						e.printStackTrace();
					}
				}
				if (is != null) {
					try {
						is.close();
					} catch (IOException e) {					
						e.printStackTrace();
					}
				}
			}
		}
		
		// if blob is image get the bytes as Base64 String
		if (val instanceof Blob) {
			Blob blob = (Blob) val;
			try {
				byte[] bytes = blob.getBytes(1, (int)blob.length());
				String hex = byteArrayToHex( bytes );
				if (isImage(hex)) {
					String ret =  encodeImage(bytes);										
					return ret;
				} else {					
					return BLOB;
				}
			} catch (Exception e) {
				e.printStackTrace();
			} 
		}
        
        return val.toString();
    }
    
    // functional compare for two strings as ignore-case text     
	public static boolean equalsText(String s1, String s2) {
		if (s1 == null) {
			if (s2 != null) {
				return false;
			}
		} else {
			if (s2 == null) {
				return false;
			}
		}
		if ((s1 != null) && (s2 != null)) {
			s1 = s1.replaceAll("\\s", "").toLowerCase();
			s2 = s2.replaceAll("\\s", "").toLowerCase();
			if (!s1.equals(s2)) {
				return false;
			}
		}
		return true;
	}
	
	public static PrefixSuffix parse(String s, String search) {		
		if (s == null) {
			return null;
		}
		int index = s.indexOf(search);
		if (index != -1) {			
			String prefix = "";
			String suffix = "";
			if (index > 0) {
				prefix = s.substring(0, index);
			}
			if (prefix.length() + search.length() < s.length()) {
				suffix = s.substring(index + search.length());
			}		
			return new PrefixSuffix(prefix, suffix);
		}
		return null;
	}
	
	 /**
     * Encodes the byte array into base64 string
     *
     * @param imageByteArray - byte array
     * @return String a {@link java.lang.String}
     */
    public static String encodeImage(byte[] imageByteArray) {
        return new Base64Encoder().encode(imageByteArray);
    }
     
    /**
     * Decodes the base64 string into byte array
     *
     * @param imageDataString - a {@link java.lang.String}
     * @return byte array
     */
    public static byte[] decodeImage(String imageDataString) {
        return new Base64Encoder().decode(imageDataString);
    }

    // PNGs start with: 89 50 4E 47 0D 0A 1A 0A ; see http://www.libpng.org/pub/png/spec/1.2/PNG-Structure.html
    // GIFs start with 47 49 46 38 37 61 (GIF87a) or 47 49 46 38 39 61 (GIF89a) ; see http://www.fileformat.info/format/gif/egff.htm
    // JPEGs start with FF D8 FF E0 xx xx 4A 46 49 46 00
    private static boolean isImage(String hexString) {
    	boolean isImage = false;
    	//png
    	if (hexString.startsWith("89504e470d0a1a0a")) {    		
    		isImage = true;
    	//gif	
    	} else if (hexString.startsWith("474946383761") || hexString.startsWith("474946383961")) {    		
    		isImage = true;
    	// jpeg	
    	} else if (hexString.startsWith("ffd8ffe0")) {    		
    		isImage = true;
    	}
    	return isImage;    	
    }
    
	private static String byteArrayToHex(byte[] a) {
		StringBuilder sb = new StringBuilder();
		for (byte b : a) {
			sb.append(String.format("%02x", b & 0xff));
		}	
		return sb.toString().toLowerCase();
	}
	
	public static String getI18nString(String s, I18nLanguage lang) {			
		int index = s.indexOf(I18nString.MARKUP);
		if (index == -1) {
			return s;
		} else {
			String s2 = s.substring(index + I18nString.MARKUP.length());
			int index2 = s2.indexOf(I18nString.MARKUP);
			if (index2 == -1) {
				return s;
			} else {				
				String key = s2.substring(0, index2);				
				if (lang == null) {
					return s;
				} else {
					String newValue = I18nUtil.getString(key, lang);
					if ("".equals(newValue.trim())) {
						return s;
					}
					return s.substring(0, index) + I18nUtil.getString(key, lang) + s2.substring(index2 + I18nString.MARKUP.length());
				}
			}
		}
	}
	
	public static String getI18nStringMultiple(String s, I18nLanguage lang) {		
		s = getI18nString(s, lang);
		while (s.contains(I18nString.MARKUP)) {
			String oldS = s;
			s = getI18nString(s, lang);
			// only start markup, we should exit
			if (oldS.equals(s)) {
				break;
			}
		}
		return s;		
	}
	
	public static String getKey(String s) {			
		int index = s.indexOf(I18nString.MARKUP);
		if (index == -1) {
			return null;
		} else {
			String s2 = s.substring(index + I18nString.MARKUP.length());
			int index2 = s2.indexOf(I18nString.MARKUP);
			if (index2 == -1) {
				return null;
			} else {				
				return s2.substring(0, index2);
			}
		}
	}
	
	public static List<String> getKeys(String s) {
		List<String> result = new ArrayList<String>();
		if (s == null) {
			return result;
		}
		int index = s.indexOf(I18nString.MARKUP);
		while (index != -1) {
			String s2 = s.substring(index + I18nString.MARKUP.length());
			int index2 = s2.indexOf(I18nString.MARKUP);
			if (index2 != -1) {							
				result.add(s2.substring(0, index2));
			} else {
				break;
			}
			s = s2.substring(index2 + I18nString.MARKUP.length());
			index = s.indexOf(I18nString.MARKUP);
		}		
		return result;
	}

}
