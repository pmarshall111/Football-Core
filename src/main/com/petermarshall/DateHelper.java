package com.petermarshall;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateHelper {
    public static Date removeTimeFromDate(Date date) {
        SimpleDateFormat fmt = new SimpleDateFormat("MMM-dd-yyyy");
        String[] partsOfDate = date.toString().split(" ");

//        for (String part: partsOfDate) System.out.println(part);

        try {
            return fmt.parse(partsOfDate[1] + "-" + partsOfDate[2] + "-" + partsOfDate[5]);
        } catch (ParseException e) {
            return null;
        }
    }
    public static String turnDateToyyyyMMddString(Date date) {
        String[] partsOfDate = date.toString().split(" ");

        return partsOfDate[5] + "-" + turnMonthStringToNumb(partsOfDate[1]) + "-" + partsOfDate[2];
    }
    public static String turnDateToddMMyyyyString(Date date) {
        String[] partsOfDate = date.toString().split(" ");

        return partsOfDate[2] + "-" + turnMonthStringToNumb(partsOfDate[1]) + "-" + partsOfDate[5];
    }

    private static String turnMonthStringToNumb (String monthString) {
        String month;

        switch(monthString) {
            case "Jan":
                month = "01";
                break;
            case "Feb":
                month = "02";
                break;
            case "Mar":
                month = "03";
                break;
            case "Apr":
                month = "04";
                break;
            case "May":
                month = "05";
                break;
            case "Jun":
                month = "06";
                break;
            case "Jul":
                month = "07";
                break;
            case "Aug":
                month = "08";
                break;
            case "Sep":
                month = "09";
                break;
            case "Oct":
                month = "10";
                break;
            case "Nov":
                month = "11";
                break;
            case "Dec":
                month = "12";
                break;
            default:
                throw new RuntimeException("Could not change date string for month into number");
        }

        return month;
    }

    public static int getMonthOfDate(Date date) {
        String[] partsOfDate = date.toString().split(" ");

        return Integer.parseInt(turnMonthStringToNumb(partsOfDate[1]));
    }

    public static int getEndingYearForCurrentSeason(Date currentDate) {
        String[] partsOfDate = currentDate.toString().split(" ");

        int year2Digits = Integer.parseInt(partsOfDate[5].substring(2,4));
        int month = Integer.parseInt(turnMonthStringToNumb(partsOfDate[1]));

        if (month >= 8) return year2Digits+1;
        else return year2Digits;
    }

    public static int getStartYearForCurrentSeason() {
        return getEndingYearForCurrentSeason(new Date())-1;
    }

    public static Date add1DayToDate(Date date) {
        SimpleDateFormat fmt = new SimpleDateFormat("MMM-dd-yyyy");
        String[] partsOfDate = date.toString().split(" ");

//        for (String part: partsOfDate) System.out.println(part);

        try {
            return fmt.parse(partsOfDate[1] + "-" + (Integer.parseInt(partsOfDate[2])+1) + "-" + partsOfDate[5]);
        } catch (ParseException e) {
            return null;
        }
    }
    public static Date subtract1DayFromDate(Date date) {
        SimpleDateFormat fmt = new SimpleDateFormat("MMM-dd-yyyy");
        String[] partsOfDate = date.toString().split(" ");

//        for (String part: partsOfDate) System.out.println(part);

        try {
            return fmt.parse(partsOfDate[1] + "-" + (Integer.parseInt(partsOfDate[2])-1) + "-" + partsOfDate[5]);
        } catch (ParseException e) {
            return null;
        }
    }

    public static Date subtractXDaysFromDate(Date date, int days) {
        SimpleDateFormat fmt = new SimpleDateFormat("MMM-dd-yyyy");
        String[] partsOfDate = date.toString().split(" ");
        try {
            return fmt.parse(partsOfDate[1] + "-" + (Integer.parseInt(partsOfDate[2])-days) + "-" + partsOfDate[5]);
        } catch (ParseException e) {
            return null;
        }
    }

    public static Date add45MinsToDate(Date date) {
        SimpleDateFormat fmt = new SimpleDateFormat("MMM-dd-yyyy HH:mm:ss");
        String[] partsOfDate = date.toString().split(" ");
        String[] partsOfTime = partsOfDate[3].split(":");

        try {
            return fmt.parse(partsOfDate[1] + "-" + partsOfDate[2] + "-" + partsOfDate[5] + " " + partsOfTime[0] + ":" + (Integer.parseInt(partsOfTime[1])+45) + ":" + partsOfTime[2]);
        } catch (ParseException e) {
            return null;
        }
    }

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

    public static Date subtract5minsFromDate(Date date) {
        SimpleDateFormat fmt = new SimpleDateFormat("MMM-dd-yyyy HH:mm:ss");
        String[] partsOfDate = date.toString().split(" ");
        String[] partsOfTime = partsOfDate[3].split(":");

        try {
            return fmt.parse(partsOfDate[1] + "-" + partsOfDate[2] + "-" + partsOfDate[5] + " " + partsOfTime[0] + ":" + (Integer.parseInt(partsOfTime[1])-5) + ":" + partsOfTime[2]);
        } catch (ParseException e) {
            return null;
        }
    }

    public static Date addMinsToDate(Date date, int mins) {
        SimpleDateFormat fmt = new SimpleDateFormat("MMM-dd-yyyy HH:mm:ss");
        String[] partsOfDate = date.toString().split(" ");
        String[] partsOfTime = partsOfDate[3].split(":");

        try {
            return fmt.parse(partsOfDate[1] + "-" + partsOfDate[2] + "-" + partsOfDate[5] + " " + partsOfTime[0] + ":" + (Integer.parseInt(partsOfTime[1])+mins) + ":" + partsOfTime[2]);
        } catch (ParseException e) {
            return null;
        }
    }

    //confirmed to work with negative days 04/06/19
    public static Date addDaysToDate(Date date, int days) {
        SimpleDateFormat fmt = new SimpleDateFormat("MMM-dd-yyyy HH:mm:ss");
        String[] partsOfDate = date.toString().split(" ");
        String[] partsOfTime = partsOfDate[3].split(":");

        try {
            return fmt.parse(partsOfDate[1] + "-" + (Integer.parseInt(partsOfDate[2]) + days) + "-" + partsOfDate[5] + " " + partsOfTime[0] + ":" + partsOfTime[1] + ":" + partsOfTime[2]);
        } catch (ParseException e) {
            return null;
        }
    }

    /*
     * Method will change sql dateString to one that is compatible with oddschecker. NOTE: will not add the timezone information.
     */
    public static String changeSqlDateToOddschecker(String date) {
        //oddschecker: 2018-12-21T20:00+00:00
        //sql: 2018-12-21 20:00:00
        //result: 2018-12-21T20:00

        return date.replace(' ', 'T').substring(0, date.length()-3);
    }

    //NOTE: will not add the timezone information
    public static String changeDateToOddsChecker(Date date) {
        String[] partsOfDate = date.toString().split(" ");

        return partsOfDate[5] + "-" + turnMonthStringToNumb(partsOfDate[1]) + "-" + partsOfDate[2] + "T" + partsOfDate[3].substring(0,partsOfDate[3].length()-3);
    }

    public static String removeOddsCheckerTimezone(String string) {
        return string.substring(0, string.length()-6);
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

    public static Date setDate(int year, int month, int day) {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");

        try {
            return fmt.parse(year + "-" + month + "-" + day);
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

    public static int findMinutesBetweenDates(Date currentTime, Date futureTime) {
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

    //2018-08-10 21:45:00
    public static Date getDateFromUnderstatDateString(String dateString) {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        try {
            return fmt.parse(dateString);
        } catch (ParseException e) {
            return null;
        }
    }

    //Tue Jun 04 16:36:21 UTC 2019
    public static Date getDateFromStandardToStringFormat(String dateString) {
        SimpleDateFormat fmt = new SimpleDateFormat("MMM-dd-yyyy HH:mm:ss");

        String[] partsOfDate = dateString.split(" ");
        String[] partsOfTime = partsOfDate[3].split(":");

        try {
            return fmt.parse(partsOfDate[1] + "-" + partsOfDate[2] + "-" + partsOfDate[5] + " " + partsOfTime[0] + ":" + partsOfTime[1] + ":" + partsOfTime[2]);
        } catch (ParseException e) {
            return null;
        }
    }


    public static void main(String[] args) {
//        System.out.println(changeSqlDateToOddschecker("2018-12-21 20:00:00"));
//        changeDateToOddsChecker(new Date());
//        System.out.println(removeOddsCheckerTimezone("2018-12-21T20:00+00:00"));
//        System.out.println(changeDateToOddsChecker(new Date()));
//        System.out.println(setTimeOfDate(new Date(), 23, 59, 59));

//        Date d = createDateFromSQL("2019-08-26 00:00:00");
//        System.out.println(getEndingYearForCurrentSeason(d));

//        System.out.println(setDate(2019,1,1));
//        System.out.println(setMonthAndDay(new Date(), 5,5));

//        System.out.println(getDateFromStandardToStringFormat("Tue Jan 01 10:37:32 GMT 2019"));

        System.out.println(addDaysToDate(new Date(), -5));
    }

}
