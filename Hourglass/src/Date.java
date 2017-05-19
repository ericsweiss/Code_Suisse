/*
  Irrelevant Development - 05/18/2017
  Code Suisse Hackathon 2017
 */

import java.util.InputMismatchException;

/**
 * Date Class
 * Stores the time and date information for a transaction
 * Includes year, month, day, hour, minute, and second (including decimal values)
 */
public class Date implements Comparable<Date>{

    // INSTANCE VARIABLES

    private double date;
    private int year;
    private int month;
    private int day;
    private int hour;
    private int minute;
    private double second;


    // METHODS

    /**************************************************
     * Constructor
     * Returns a new Date with all fields assigned
     *************************************************/
    public Date(String date) {

        try {
            this.date = Double.parseDouble(date);
        } catch (NumberFormatException e) {
            this.date = 0;
            year = 0;
            month = 0;
            day = 0;
            hour = 0;
            minute = 0;
            second = 0;
            return;
        }

        try {
            if(date.length() < 14) { /* Ensure that the date has all necessary components */
                throw new InputMismatchException();
            }
        } catch (InputMismatchException e) {
            System.err.println("Date does not include all necessary components - length: " + date.length());
            this.date = 0;
            year = 0;
            month = 0;
            day = 0;
            hour = 0;
            minute = 0;
            second = 0;
            return;
        }

        try { /* Ensure that the format of the date is correct */
            year = Integer.parseInt(date.substring(0, 3));
            month = Integer.parseInt(date.substring(4, 5));
            day = Integer.parseInt(date.substring(6, 7));
            hour = Integer.parseInt(date.substring(8, 9));
            minute = Integer.parseInt(date.substring(10, 11));
            second = Double.parseDouble(date.substring(12));
        } catch(NumberFormatException e) {
            System.err.println("Date is improperly formatted!");
            year = 0;
            month = 0;
            day = 0;
            hour = 0;
            minute = 0;
            second = 0;
        }
    }

    // Accessors

    /**************************************************
     * Accessor for year
     * @return the year
     **************************************************/
    public int getYear() {
        return year;
    }

    /**************************************************
     * Accessor for month
     * @return the month
     *************************************************/
    public int getMonth() {
        return month;
    }

    /**************************************************
     * Accessor for day
     * @return the day
     *************************************************/
    public int getDay() {
        return day;
    }

    /**************************************************
     * Accessor for hour
     * @return the hour
     *************************************************/
    public int getHour() {
        return hour;
    }

    /**************************************************
     * Accessor for minute
     * @return the minute
     *************************************************/
    public int getMinute() {
        return minute;
    }

    /**************************************************
     * Accessor for second
     * @return the second
     *************************************************/
    public double getSecond() {
        return second;
    }

    /**************************************************
     * Accessor for date
     * @return the full timestamp in the form of a double
     *************************************************/
    public double getDate() {return date;}

    /**************************************************
     * Implement comparator method to compare dates
     * This is necessary to determine the order of transactions
     *************************************************/
    @Override
    public int compareTo(Date d) {
        if(date == d.getDate()) return 0;
        return date < d.getDate() ? -1 : 1;
    }
}
