package com.footballbettingcore.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateHelper {
    /*
     * Changing format of date methods
     */
    public static String turnDateToyyyyMMddString(Date date) {
        String[] partsOfDate = date.toString().split(" ");
        return partsOfDate[5] + "-" + turnMonthStringToNumb(partsOfDate[1]) + "-" + partsOfDate[2];
    }
    
    public static String turnDateToddMMyyyyString(Date date) {
        String[] partsOfDate = date.toString().split(" ");
        return partsOfDate[2] + "-" + turnMonthStringToNumb(partsOfDate[1]) + "-" + partsOfDate[5];
    }

    public static String getSqlDate(Date date) {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return fmt.format(date);
    }

    public static Date createDateFromSQL(String date) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String changeSqlDateToOddschecker(String date) {
        //Method will change sql dateString to one that is compatible with oddschecker. NOTE: will not add the timezone information.
        //oddschecker: 2018-12-21T20:00+00:00
        //sql: 2018-12-21 20:00:00
        //result: 2018-12-21T20:00
        return date.replace(' ', 'T').substring(0, date.length()-3);
    }
    
    public static String changeDateToOddsChecker(Date date) {
        //NOTE: will not add the timezone information
        String[] partsOfDate = date.toString().split(" ");
        return partsOfDate[5] + "-" + turnMonthStringToNumb(partsOfDate[1]) + "-" + partsOfDate[2] + "T" + partsOfDate[3].substring(0,partsOfDate[3].length()-3);
    }

    public static String removeOddsCheckerTimezone(String string) {
        return string.substring(0, string.length()-6);
    }
    
    public static Date getDateFromUnderstatDateString(String dateString) {
        //2018-08-10 21:45:00
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            return fmt.parse(dateString);
        } catch (ParseException e) {
            return null;
        }
    }
    
    public static Date getDateFromStandardToStringFormat(String dateString) {
        //Tue Jun 04 16:36:21 UTC 2019
        SimpleDateFormat fmt = new SimpleDateFormat("MMM-dd-yyyy HH:mm:ss");
        String[] partsOfDate = dateString.split(" ");
        String[] partsOfTime = partsOfDate[3].split(":");
        try {
            return fmt.parse(partsOfDate[1] + "-" + partsOfDate[2] + "-" + partsOfDate[5] + " " + partsOfTime[0] + ":" + partsOfTime[1] + ":" + partsOfTime[2]);
        } catch (ParseException e) {
            return null;
        }
    }
    
    
    /*
     * Editing date methods
     */
    public static Date removeTimeFromDate(Date date) {
        SimpleDateFormat fmt = new SimpleDateFormat("MMM-dd-yyyy");
        String[] partsOfDate = date.toString().split(" ");
        try {
            return fmt.parse(partsOfDate[1] + "-" + partsOfDate[2] + "-" + partsOfDate[5]);
        } catch (ParseException e) {
            return null;
        }
    }

    public static Date addXDaysToDate(Date date, int days) {
        SimpleDateFormat fmt = new SimpleDateFormat("MMM-dd-yyyy HH:mm:ss");
        String[] partsOfDate = date.toString().split(" ");
        String[] partsOfTime = partsOfDate[3].split(":");
        try {
            return fmt.parse(partsOfDate[1] + "-" + (Integer.parseInt(partsOfDate[2]) + days) + "-" + partsOfDate[5] + " " + partsOfTime[0] + ":" + partsOfTime[1] + ":" + partsOfTime[2]);
        } catch (ParseException e) {
            return null;
        }
    }

    public static Date subtractXDaysFromDate(Date date, int days) {
        return addXDaysToDate(date, -days);
    }

    public static Date addXMinsToDate(Date date, int mins) {
        SimpleDateFormat fmt = new SimpleDateFormat("MMM-dd-yyyy HH:mm:ss");
        String[] partsOfDate = date.toString().split(" ");
        String[] partsOfTime = partsOfDate[3].split(":");
        try {
            return fmt.parse(partsOfDate[1] + "-" + partsOfDate[2] + "-" + partsOfDate[5] + " " + partsOfTime[0] + ":" + (Integer.parseInt(partsOfTime[1])+mins) + ":" + partsOfTime[2]);
        } catch (ParseException e) {
            return null;
        }
    }

    public static Date subtractXminsFromDate(Date date, int mins) {
        return addXMinsToDate(date, -mins);
    }

    public static Date setTimeOfDate(Date date, int hours, int mins, int seconds) {
        if (hours > 23 || mins > 59 || seconds > 59) throw new IllegalArgumentException("Hours/mins/seconds must all be within normal ranges. Your values: " + hours + ":" + mins + ":" + seconds);
        SimpleDateFormat fmt = new SimpleDateFormat("MMM-dd-yyyy HH:mm:ss");
        String[] partsOfDate = date.toString().split(" ");
        try {
            return fmt.parse(partsOfDate[1] + "-" + partsOfDate[2] + "-" + partsOfDate[5] + " " + hours + ":" + mins + ":" + seconds);
        } catch (ParseException e) {
            return null;
        }
    }

    public static Date setMonthAndDay(Date date, int month, int day) {
        SimpleDateFormat fmt = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
        String[] partsOfDate = date.toString().split(" ");
        String[] partsOfTime = partsOfDate[3].split(":");
        try {
            return fmt.parse(month + "-" + day + "-" + partsOfDate[5] + " " + partsOfTime[0] + ":" + partsOfTime[1] + ":" + partsOfTime[2]);
        } catch (ParseException e) {
            return null;
        }
    }

    /*
     * Creating dates
     */
    public static Date createDateyyyyMMdd(String yyyy, String MM, String dd) {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");

        try {
            return fmt.parse(yyyy + "-" + MM + "-" + dd);
        } catch (ParseException e) {
            return null;
        }
    }

    public static Date createDateyyyyMMddHHmmss(String yyyy, String MM, String dd, String HH, String mm, String ss) {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            return fmt.parse(yyyy + "-" + MM + "-" + dd + " " + HH + ":" + mm + ":" + ss);
        } catch (ParseException e) {
            return null;
        }
    }

    /*
     * Date comparisons
     */
    public static int findMinutesToAddToDate1ToGetDate2(Date currentTime, Date futureTime) {
        if (futureTime.before(currentTime)) return 0;
        else {
            int minsDifference = 0;
            String[] currentPartsOfDate = currentTime.toString().split(" ");
            String[] currentPartsOfTime = currentPartsOfDate[3].split(":");
            String[] futurePartsOfDate = futureTime.toString().split(" ");
            String[] futurePartsOfTime = futurePartsOfDate[3].split(":");

            int currentHours = Integer.parseInt(currentPartsOfTime[0]), currentMinutes = Integer.parseInt(currentPartsOfTime[1]);
            int futureHours = Integer.parseInt(futurePartsOfTime[0]), futureMinutes = Integer.parseInt(futurePartsOfTime[1]);

            while (futureHours > currentHours) {
                minsDifference += 60;
                futureHours--;
            }
            minsDifference += (futureMinutes - currentMinutes);
            return minsDifference;
        }
    }

    public static boolean isBetweenDateStrings(String queryDate, String earliestDate, String latestDate) {
        try {
            Date query = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(queryDate);
            Date earliest = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(earliestDate);
            Date latest = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(latestDate);

            return (query.after(earliest) && query.before(latest));
        } catch (ParseException e) {
            System.out.println("INPUT STRING NOT IN CORRECT FORMAT");
            e.printStackTrace();
            return false;
        }
    }

    /*
     * Getters
     */
    public static int getStartYearForCurrentSeason() {
        return getEndingYearForSeason(new Date())-1;
    }

    public static int getEndingYearForSeason(Date currentDate) {
        String[] partsOfDate = currentDate.toString().split(" ");
        int year2Digits = Integer.parseInt(partsOfDate[5].substring(2,4));
        int month = Integer.parseInt(turnMonthStringToNumb(partsOfDate[1]));
        if (month >= 8) return year2Digits+1;
        else return year2Digits;
    }

    public static int getMonthOfDate(Date date) {
        String[] partsOfDate = date.toString().split(" ");
        return Integer.parseInt(turnMonthStringToNumb(partsOfDate[1]));
    }

    private static String turnMonthStringToNumb (String monthString) {
        switch (monthString) {
            case "Jan":
                return "01";
            case "Feb":
                return "02";
            case "Mar":
                return "03";
            case "Apr":
                return "04";
            case "May":
                return "05";
            case "Jun":
                return "06";
            case "Jul":
                return "07";
            case "Aug":
                return "08";
            case "Sep":
                return "09";
            case "Oct":
                return "10";
            case "Nov":
                return "11";
            case "Dec":
                return "12";
            default:
                throw new RuntimeException("Could not change date string for month into number");
        }
    }

    public static Date getDateFromSofascoreTimestamp(long secsSinceEpoch) {
        return new Date(secsSinceEpoch * 1000);
    }
}