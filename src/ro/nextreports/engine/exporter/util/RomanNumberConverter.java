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
package ro.nextreports.engine.exporter.util;

public class RomanNumberConverter {

	private final static String[] LETTERS = { "M", "CM", "D", "CD", "C", "XC",
			"L", "XL", "X", "IX", "V", "IV", "I" };
	private final static int[] VALUES = { 1000, 900, 500, 400, 100, 90, 50, 40,
			10, 9, 5, 4, 1 };

	public static String romanize(int value) {
		String roman = "";
		int n = value;
		for (int i = 0; i < LETTERS.length; i++) {
			while (n >= VALUES[i]) {
				roman += LETTERS[i];
				n -= VALUES[i];
			}
		}
		return roman;
	}

	public static int numberize(String roman) {
		int start = 0, value = 0;
		for (int i = 0; i < LETTERS.length; i++) {
			while (roman.startsWith(LETTERS[i], start)) {
				value += VALUES[i];
				start += LETTERS[i].length();
			}
		}
		return start == roman.length() ? value : -1;
	}

	public static boolean isRoman(String roman) {
		return roman.equals(romanize(numberize(roman)));
	}

}
