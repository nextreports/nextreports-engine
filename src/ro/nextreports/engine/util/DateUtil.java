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

import java.util.Date;
import java.util.Calendar;
import java.sql.Timestamp;

/**
 * Created by IntelliJ IDEA.
 * User: mihai.panaitescu
 * Date: May 22, 2008
 * Time: 1:46:12 PM
 */
public class DateUtil {

    /**
     * Use only static methods
     */
    private DateUtil() {
    }

    /**
     * Compares dates <code>d1</code> and <code>d2</code> taking into
     * consideration only the year, month and day
     * @param d1 the first date
     * @param d2 the second date
     * @return <code>true</code> if <code>d1</code> is after <code>d2</code>,
     * <code>false</code> otherwise
     * @see java.util.Calendar#after(java.lang.Object)
     * @see #before(java.util.Date , java.util.Date)
     * @see #compare(java.util.Date , java.util.Date)
     */
    public static boolean after(Date d1, Date d2) {
        d1 = floor(d1);
        Calendar c1 = Calendar.getInstance();
        c1.setTime(d1);

        d2 = floor(d2);
        Calendar c2 = Calendar.getInstance();
        c2.setTime(d2);

        return c1.after(c2);
    }

    /**
     * Compares dates <code>d1</code> and <code>d2</code> taking into
     * consideration only the year, month and day
     * @param d1 the first date
     * @param d2 the second date
     * @return <code>true</code> if <code>d1</code> is before <code>d2</code>,
     * <code>false</code> otherwise
     * @see Calendar#before(java.lang.Object)
     * @see #after(Date, Date)
     * @see #compare(Date, Date)
     */
    public static boolean before(Date d1, Date d2) {
        d1 = floor(d1);
        Calendar c1 = Calendar.getInstance();
        c1.setTime(d1);

        d2 = floor(d2);
        Calendar c2 = Calendar.getInstance();
        c2.setTime(d2);

        return c1.before(c2);
    }

    /**
     * Compares two dates taking into consideration only the year, month and day
     * @param d1 the first date
     * @param d2 the second date
     * @return a negative integer, zero, or a positive integer as
     * <code>d1</code> is less than, equal to, or greater than <code>d2</code>
     * @see java.util.Comparator
     * @see #after(Date, Date)
     * @see #before(Date, Date)
     */
    public static int compare(Date d1, Date d2) {
        Calendar c1 = Calendar.getInstance();
        c1.setTime(d1);
        Calendar c2 = Calendar.getInstance();
        c2.setTime(d2);

        if (c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR)) {
            if (c1.get(Calendar.MONTH) == c2.get(Calendar.MONTH)) {
                return c1.get(Calendar.DAY_OF_MONTH) - c2.get(Calendar.DAY_OF_MONTH);
            }
            return c1.get(Calendar.MONTH) - c2.get(Calendar.MONTH);
        }
        return c1.get(Calendar.YEAR) - c2.get(Calendar.YEAR);
    }

    /** Get the year of the date
     *
     * @param date date
     * @return year of the date
     */
    public static int getYear(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.get(Calendar.YEAR);
    }

    /** Get the month of the date
     *
     * @param date date
     * @return month of the date
     */
    public static int getMonth(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.get(Calendar.MONTH);
    }

    /** Get the day of year of the date
     *
     * @param date date
     * @return day of year of the date
     */
    public static int getDayOfYear(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.get(Calendar.DAY_OF_YEAR);
    }

    /** Get the day of month of the date
     *
     * @param date date
     * @return day of month of the date
     */
    public static int getDayOfMonth(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.get(Calendar.DAY_OF_MONTH);
    }

    /** Get the day of week of the date
     *
     * @param date date
     * @return day of week of the date
     */
    public static int getDayOfWeek(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.get(Calendar.DAY_OF_WEEK);
    }

    /** Get the hour of the date
     *
     * @param date date
     * @return hour of the date
     */
    public static int getHour(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.get(Calendar.HOUR_OF_DAY);
    }

    /** Get the minute of the date
     *
     * @param date date
     * @return minute of the date
     */
    public static int getMinute(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.get(Calendar.MINUTE);
    }

    /** Get the second of the date
     *
     * @param date date
     * @return second of the date
     */
    public static int getSecond(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.get(Calendar.SECOND);
    }

    /**
     * Rounds a date to hour 0, minute 0, second 0 and millisecond 0
     * @param d the date
     * @return the rounded date
     */
    public static Date floor(Date d) {
        Calendar c = Calendar.getInstance();
        c.setTime(d);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTime();
    }

    /**
     * Rounds a date to hour 23, minute 59, second 59 and millisecond 999
     * @param d the date
     * @return the rounded date
     */
    public static Date ceil(Date d) {
        Calendar c = Calendar.getInstance();
        c.setTime(d);
        c.set(Calendar.HOUR_OF_DAY, 23);
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.SECOND, 59);
        c.set(Calendar.MILLISECOND, 999);
        return c.getTime();
    }

    /** Test to see if two dates are in the same day of year
     *
     * @param dateOne first date
     * @param dateTwo second date
     * @return true if the two dates are in the same day of year
     */
    public static boolean sameDay(Date dateOne, Date dateTwo) {
        if ((dateOne == null) || (dateTwo == null)) {
            return false;
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(dateOne);
        int year = cal.get(Calendar.YEAR);
        int day = cal.get(Calendar.DAY_OF_YEAR);

        cal.setTime(dateTwo);
        int year2 = cal.get(Calendar.YEAR);
        int day2 = cal.get(Calendar.DAY_OF_YEAR);

        return ( (year == year2) && (day == day2) );
    }

    /** Test to see if two dates are in the same week
     *
     * @param dateOne first date
     * @param dateTwo second date
     * @return true if the two dates are in the same week
     */
    public static boolean sameWeek(Date dateOne, Date dateTwo) {
        if ((dateOne == null) || (dateTwo == null)) {
            return false;
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(dateOne);
        int year = cal.get(Calendar.YEAR);
        int week = cal.get(Calendar.WEEK_OF_YEAR);

        cal.setTime(dateTwo);
        int year2 = cal.get(Calendar.YEAR);
        int week2 = cal.get(Calendar.WEEK_OF_YEAR);

        return ( (year == year2) && (week == week2) );
    }

    /** Test to see if two dates are in the same month
     *
     * @param dateOne first date
     * @param dateTwo second date
     * @return true if the two dates are in the same month
     */
    public static boolean sameMonth(Date dateOne, Date dateTwo) {
        if ((dateOne == null) || (dateTwo == null)) {
            return false;
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(dateOne);
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);

        cal.setTime(dateTwo);
        int year2 = cal.get(Calendar.YEAR);
        int month2 = cal.get(Calendar.MONTH);

        return ( (year == year2) && (month == month2) );
    }

    /** Test to see if two dates are in the same hour of day
     *
     * @param dateOne first date
     * @param dateTwo second date
     * @return true if the two dates are in the same hour of day
     */
    public static boolean sameHour(Date dateOne, Date dateTwo) {
        if ((dateOne == null) || (dateTwo == null)) {
            return false;
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(dateOne);
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_YEAR);
        int hour = cal.get(Calendar.HOUR_OF_DAY);

        cal.setTime(dateTwo);
        int year2 = cal.get(Calendar.YEAR);
        int month2 = cal.get(Calendar.MONTH);
        int day2 = cal.get(Calendar.DAY_OF_YEAR);
        int hour2 = cal.get(Calendar.HOUR_OF_DAY);

        return ( (year == year2) && (month == month2) && (day == day2) &&
                 (hour == hour2));
    }

    /** Get number of days between two dates
     *
     *  @param first first date
     *  @param second second date
     *  @return number of days if first date less than second date,
     *       0 if first date is bigger than second date,
     *       1 if dates are the same
     *
     */
      public static int getNumberOfDays(Date first, Date second)
      {
          Calendar c = Calendar.getInstance();
          int result = 0;
          int compare = first.compareTo(second);
          if (compare > 0) return 0;
          if (compare == 0) return 1;

          c.setTime(first);
          int firstDay = c.get(Calendar.DAY_OF_YEAR);
          int firstYear = c.get(Calendar.YEAR);
          int firstDays = c.getActualMaximum(Calendar.DAY_OF_YEAR);

          c.setTime(second);
          int secondDay = c.get(Calendar.DAY_OF_YEAR);
          int secondYear = c.get(Calendar.YEAR);

          // if dates in the same year
          if (firstYear == secondYear)
          {
              result = secondDay-firstDay+1;
          }

          // different years
          else
          {
              // days from the first year
              result += firstDays - firstDay + 1;

              // add days from all years between the two dates years
              for (int i = firstYear+1; i< secondYear; i++)
              {
                  c.set(i,0,0);
                  result += c.getActualMaximum(Calendar.DAY_OF_YEAR);
              }

              // days from last year
              result += secondDay;
          }

          return result;
      }

    /** Get elapsedtime between two dates
     *
     *  @param first first date
     *  @param second second date
     *  @return null if first date is after second date
     *          an integer array of three elemets ( days, hours minutes )
     */
     public static int[] getElapsedTime(Date first, Date second) {
         if (first.compareTo(second) == 1 ) {
            return null;
         }
         int difDays = 0;
         int difHours = 0;
         int difMinutes = 0;

         Calendar c = Calendar.getInstance();
         c.setTime(first);
         int h1 = c.get(Calendar.HOUR_OF_DAY);
         int m1 = c.get(Calendar.MINUTE);

         c.setTime(second);
         int h2 = c.get(Calendar.HOUR_OF_DAY);
         int m2 = c.get(Calendar.MINUTE);

         if (sameDay(first, second)) {
             difHours = h2 - h1;
         } else {
             difDays = getNumberOfDays(first, second)-1;
             if (h1 >= h2) {
                difDays--;
                difHours = (24 - h1) + h2;
             } else {
                difHours = h2 - h1;
             }
         }

         if (m1 >= m2) {
             difHours--;
             difMinutes = (60 - m1) + m2;
         } else {
             difMinutes = m2 - m1;
         }

         int[] result = new int[3];
         result[0] = difDays;
         result[1] = difHours;
         result[2] = difMinutes;
         return result;
     }

    /** Add minutes to a date
     *
     * @param d date
     * @param minutes minutes
     * @return new date
     */
    public static Date addMinutes(Date d, int minutes) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        cal.add(Calendar.MINUTE, minutes);
        return cal.getTime();
    }
    
    /** Set minutes to a date
    *
    * @param d date
    * @param minutes minutes
    * @return new date
    */
   public static Date setMinutes(Date d, int minutes) {
       Calendar cal = Calendar.getInstance();
       cal.setTime(d);
       cal.set(Calendar.MINUTE, minutes);
       return cal.getTime();
   }

    /** Add hours to a date
     *
     * @param d date
     * @param hours hours
     * @return new date
     */
    public static Date addHours(Date d, int hours) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        cal.add(Calendar.HOUR_OF_DAY, hours);
        return cal.getTime();
    }
    
    /** Set hours to a date
    *
    * @param d date
    * @param hours hours
    * @return new date
    */
   public static Date setHours(Date d, int hours) {
       Calendar cal = Calendar.getInstance();
       cal.setTime(d);
       cal.set(Calendar.HOUR_OF_DAY, hours);
       return cal.getTime();
   }

    /** Add days to a date
     *
     * @param d date
     * @param days days
     * @return new date
     */
    public static Date addDays(Date d, int days) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        cal.add(Calendar.DAY_OF_YEAR, days);
        return cal.getTime();
    }

    /** Add weeks to a date
     *
     * @param d date
     * @param weeks weeks
     * @return new date
     */
    public static Date addWeeks(Date d, int weeks) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        cal.add(Calendar.WEEK_OF_YEAR, weeks);
        return cal.getTime();
    }

    /** Add months to a date
     *
     * @param d date
     * @param months months
     * @return new date
     */
    public static Date addMonths(Date d, int months) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        cal.add(Calendar.MONTH, months);
        return cal.getTime();
    }

    /**
     * Get last day from a month
     * @param date date
     * @return last day from a month
     */
    public static int getLastDayOfMonth(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.getActualMaximum(Calendar.DATE);
    }

    /**
     * Get a date from a timestamp
     * @param timestamp time stamp
     * @return date from a timestamp
     */
    public static Date getFromTimestamp(Timestamp timestamp) {
        if (timestamp == null) {
            return null;
        }
        return new Date(timestamp.getTime());
    }
    
    /**
     * Get first date from last week
     * @param d date
     * @return first date from last week
     */
    public static Date getFirstDayFromLastWeek(Date d) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        cal.add(Calendar.WEEK_OF_YEAR, -1);
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }
    
    /**
     * Get last date from last week
     * @param d date
     * @return last date from last week
     */
    public static Date getLastDayFromLastWeek(Date d) {
        Calendar cal = Calendar.getInstance();        
        cal.setTime(d);        
        // depends on Locale (if a week starts on Monday or on Sunday)
        if (cal.getFirstDayOfWeek() == Calendar.MONDAY) {
        	cal.add(Calendar.WEEK_OF_YEAR, -1);
        } 
        cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);        	
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        return cal.getTime();
    }
    
    /**
     * Get first date from current week
     * @param d date
     * @return first date from current week
     */
    public static Date getFirstDayFromCurrentWeek(Date d) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);        
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }
    
    /**
     * Get last date from current week
     * @param d date
     * @return last date from current week
     */
    public static Date getLastDayFromCurrentWeek(Date d) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);        
        // depends on Locale (if a week starts on Monday or on Sunday)
        if (cal.getFirstDayOfWeek() == Calendar.SUNDAY) {
        	cal.add(Calendar.WEEK_OF_YEAR, 1);  
        }	
        cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        return cal.getTime();
    }
    
    /**
     * Get first date from last month
     * @param d date
     * @return first date from last month
     */
    public static Date getFirstDayFromLastMonth(Date d) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);                
        cal.add(Calendar.MONTH, -1);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }
    
    /**
     * Get last date from last month
     * @param d date
     * @return last date from last month
     */
    public static Date getLastDayFromLastMonth(Date d) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);        
        cal.add(Calendar.MONTH, -1);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DATE));
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        return cal.getTime();
    }
    
    /**
     * Get first date from current month
     * @param d date
     * @return first date from current month
     */
    public static Date getFirstDayFromCurrentMonth(Date d) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);                        
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }
    
    /**
     * Get last date from current month
     * @param d date
     * @return last date from current month
     */
    public static Date getLastDayFromCurrentMonth(Date d) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);                
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DATE));
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        return cal.getTime();
    }
    
    /**
     * Get first date from last year
     * @param d date
     * @return first date from last year
     */
    public static Date getFirstDayFromLastYear(Date d) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);                
        cal.add(Calendar.YEAR, -1);
        cal.set(Calendar.MONTH, Calendar.JANUARY);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }
    
    /**
     * Get last date from last year
     * @param d date
     * @return last date from last year
     */
    public static Date getLastDayFromLastYear(Date d) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);        
        cal.add(Calendar.YEAR, -1);
        cal.set(Calendar.MONTH, Calendar.DECEMBER);
        cal.set(Calendar.DAY_OF_MONTH, 31);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        return cal.getTime();
    }
    
    /**
     * Get first date from current year
     * @param d date
     * @return first date from current year
     */
    public static Date getFirstDayFromCurrentYear(Date d) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);                        
        cal.set(Calendar.MONTH, Calendar.JANUARY);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }
    
    /**
     * Get last date from current year
     * @param d date
     * @return last date from current year
     */
    public static Date getLastDayFromCurrentYear(Date d) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);                
        cal.set(Calendar.MONTH, Calendar.DECEMBER);
        cal.set(Calendar.DAY_OF_MONTH, 31);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        return cal.getTime();
    }
    
    /**
     * Get date with n unitType before
     * @param d date
     * @param n number of units
     * @param unitType unit type : one of  Calendar.DAY_OF_YEAR, Calendar.WEEK_OF_YEAR, Calendar.MONTH, Calendar.YEAR;
     * @return
     */
    public static Date getLastNDay(Date d, int n, int unitType) {
    	Calendar cal = Calendar.getInstance();
        cal.setTime(d); 
        cal.add(unitType, -n);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

}
