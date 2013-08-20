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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class TimeShortcutType implements Serializable {	
	
	private int type;
	private int timeType;
	private int timeUnits;
	
	private static final long serialVersionUID = -3081236915067313721L;
	
	public static final int DAY_TYPE = Calendar.DAY_OF_YEAR;
	public static final int WEEK_TYPE = Calendar.WEEK_OF_YEAR;
	public static final int MONTH_TYPE = Calendar.MONTH;
	public static final int YEAR_TYPE = Calendar.YEAR;
	
	public static TimeShortcutType NONE = new TimeShortcutType(0);
	public static TimeShortcutType YESTERDAY = new TimeShortcutType(1);
	public static TimeShortcutType TODAY = new TimeShortcutType(2);
	public static TimeShortcutType PREVIOUS_WEEK = new TimeShortcutType(3);
	public static TimeShortcutType CURRENT_WEEK = new TimeShortcutType(4);
	public static TimeShortcutType PREVIOUS_MONTH = new TimeShortcutType(5);
	public static TimeShortcutType CURRENT_MONTH = new TimeShortcutType(6);
	public static TimeShortcutType PREVIOUS_YEAR = new TimeShortcutType(7);
	public static TimeShortcutType CURRENT_YEAR = new TimeShortcutType(8);
	
	public static TimeShortcutType LAST = new TimeShortcutType(-1);
	
	public TimeShortcutType(int type) {
		this.type = type;		
		this.timeType = 0;
		this.timeUnits = 0;
	}
	
	// last timeUnits for timeType (last 3 days, last 4 weeks, last 6 months, last 2 years) from now
	public TimeShortcutType(int timeType, int timeUnits) {
		this.type = -1;
		this.timeType = timeType;
		this.timeUnits = timeUnits;
	}
	
	public boolean isUnitsType() {
		return (type == -1);
	}
	
	public int getType() {
		return type;
	}
	
	public int getTimeType() {
		return timeType;
	}
	
	public int getTimeUnits() {
		return timeUnits;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + timeType;
		result = prime * result + timeUnits;
		result = prime * result + type;
		return result;
	}
	
	public Date[] getDates() {
		if (type == 0) {
			return new Date[0];
		}
		
		Date currentDate = new Date();		
		Date[] dates = new Date[2];
		switch (type) {
			case -1:
				dates[0] = DateUtil.getLastNDay(currentDate, timeUnits, timeType);
				dates[1] = DateUtil.ceil(currentDate);
				break;
			case 1:	
				// yesterday
				Date yesterday = DateUtil.addDays(currentDate, -1);
				dates[0] = DateUtil.floor(yesterday);
				dates[1] = DateUtil.ceil(yesterday);
				break;
			case 2 :
				// today
				dates[0] = DateUtil.floor(currentDate);
				dates[1] = DateUtil.ceil(currentDate);
				break;
			case 3:
				// last week
				dates[0] = DateUtil.getFirstDayFromLastWeek(currentDate);
				dates[1] = DateUtil.getLastDayFromLastWeek(currentDate);
				break;
			case 4:
				// current week
				dates[0] = DateUtil.getFirstDayFromCurrentWeek(currentDate);
				dates[1] = DateUtil.getLastDayFromCurrentWeek(currentDate);
				break;
			case 5:
				// last month
				dates[0] = DateUtil.getFirstDayFromLastMonth(currentDate);
				dates[1] = DateUtil.getLastDayFromLastMonth(currentDate);
				break;
			case 6:
				// current month
				dates[0] = DateUtil.getFirstDayFromCurrentMonth(currentDate);
				dates[1] = DateUtil.getLastDayFromCurrentMonth(currentDate);
				break;
			case 7:
				// last year
				dates[0] = DateUtil.getFirstDayFromLastYear(currentDate);
				dates[1] = DateUtil.getLastDayFromLastYear(currentDate);
				break;
			case 8:
				// current year
				dates[0] = DateUtil.getFirstDayFromCurrentYear(currentDate);
				dates[1] = DateUtil.getLastDayFromCurrentYear(currentDate);	
				break;
			default:
				dates[0] = dates[1] = currentDate;
				break;
		}
				
		return dates;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TimeShortcutType other = (TimeShortcutType) obj;
		if (timeType != other.timeType)
			return false;
		if (timeUnits != other.timeUnits)
			return false;
		if (type != other.type)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "TimeShortcutType [type=" + type + ", timeType=" + timeType + ", timeUnits=" + timeUnits + "]";
	}
	
	public static void main(String[] args) {
		Date[] date = TimeShortcutType.YESTERDAY.getDates();
		System.out.println("Yesterday = [" + date[0] + " , " + date[1] + "]");
		
		date = TimeShortcutType.TODAY.getDates();
		System.out.println("Today = [" + date[0] + " , " + date[1] + "]");
		
		date = TimeShortcutType.PREVIOUS_WEEK.getDates();
		System.out.println("Previous week = [" + date[0] + " , " + date[1] + "]");
		
		date = TimeShortcutType.CURRENT_WEEK.getDates();
		System.out.println("Current week = [" + date[0] + " , " + date[1] + "]");
		
		date = TimeShortcutType.PREVIOUS_MONTH.getDates();
		System.out.println("Previous month = [" + date[0] + " , " + date[1] + "]");
		
		date = TimeShortcutType.CURRENT_MONTH.getDates();
		System.out.println("Current month = [" + date[0] + " , " + date[1] + "]");
		
		date = TimeShortcutType.PREVIOUS_YEAR.getDates();
		System.out.println("Previous year = [" + date[0] + " , " + date[1] + "]");
		
		date = TimeShortcutType.CURRENT_YEAR.getDates();
		System.out.println("Current year = [" + date[0] + " , " + date[1] + "]");
		
		date = new TimeShortcutType(DAY_TYPE, 5).getDates();
		System.out.println("Last 5 days = [" + date[0] + " , " + date[1] + "]");
		
		date = new TimeShortcutType(WEEK_TYPE, 2).getDates();
		System.out.println("Last 2 weeks = [" + date[0] + " , " + date[1] + "]");
		
		date = new TimeShortcutType(MONTH_TYPE, 3).getDates();
		System.out.println("Last 3 months = [" + date[0] + " , " + date[1] + "]");
		
		date = new TimeShortcutType(YEAR_TYPE, 1).getDates();
		System.out.println("Last 1 years = [" + date[0] + " , " + date[1] + "]");
	}
	
	public static List<TimeShortcutType> getTypes() {
		List<TimeShortcutType> result = new ArrayList<TimeShortcutType>();
		result.add(NONE);
		result.add(YESTERDAY);
		result.add(TODAY);
		result.add(PREVIOUS_WEEK);
		result.add(CURRENT_WEEK);
		result.add(PREVIOUS_MONTH);
		result.add(CURRENT_MONTH);
		result.add(PREVIOUS_YEAR);
		result.add(CURRENT_YEAR);
		result.add(LAST);
		return result;
	}
	
	public static String getName(int type) {
		switch (type) {
			case -1: return "Last";
    		case 0: return "None";
    		case 1: return "Yesterday";
    		case 2: return "Today";
    		case 3: return "PreviousWeek";
    		case 4: return "CurrentWeek";
    		case 5: return "PreviousMonth";
    		case 6: return "CurrentMonth";
    		case 7: return "PreviousYear";
    		case 8: return "CurrentYear";    		
    		default: return "NA";
		}
	}
	
	public static String getTypeName(int typeUnit) {
		switch (typeUnit) {
			case DAY_TYPE: return "Days";
			case WEEK_TYPE: return "Weeks";
			case MONTH_TYPE: return "Months";
			case YEAR_TYPE: return "Years";
			default: return "NA";
		}		
	}
	
	public static int getTypeUnit(String typeName) {
		if ("Days".equals(typeName)) {
			return DAY_TYPE;
		} else if ("Weeks".equals(typeName)) {
			return WEEK_TYPE;
		} else if ("Months".equals(typeName)) {
			return MONTH_TYPE;
		} else if ("Years".equals(typeName)) {
			return YEAR_TYPE;
		} else { 
			return -1;		
		}
	}
	
	public static List<String> getTypeNames() {
		List<String> result = new ArrayList<String>();
		result.add("Days");
		result.add("Weeks");
		result.add("Months");
		result.add("Years");
		return result;
	}
	
	
}

