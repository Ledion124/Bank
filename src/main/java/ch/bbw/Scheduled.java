package ch.bbw;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.time.temporal.UnsupportedTemporalTypeException;

/**
 * A class to represent a scheduled date.
 * @author lro@gmx.ch, Ledion Sakaj
 * @version 1.0
 */
public class Scheduled {

    /**
     * The milliseconds until the date is due. Negative values mean that the date is past due.
     */
    private long millisecondsUntilDue;

    /**
     * Creates a date relative to now.
     *
     * @param millisecondsUntilDue milliseconds until the date; negative means in the past
     */
    public Scheduled(long millisecondsUntilDue) {
        this.millisecondsUntilDue = millisecondsUntilDue;
    }

    /**
     * Checks if the transaction date is in the past.
     * @return true if the date is in the past (past due), false if it is now or in the future
     */
    public boolean isPastDue() {
        return millisecondsUntilDue < 0;
    }

    /**
     * Creates a new Scheduled date with the current instant.
     * @return a new Scheduled date with the current instant.
     */
    public static Scheduled now() {
        return new Scheduled(0);
    }

    /**
     * Converts the Scheduled date to a unix timestamp.
     * @return the unix timestamp
     */
    public long toUnix(){
        return toInstant().toEpochMilli();
    }

    /**
     * Converts the Scheduled date to an Instant.
     * @return the Instant
     */
    public Instant toInstant(){

        return Instant.now().plus(millisecondsUntilDue, ChronoUnit.MILLIS);
    }

    /**
     * Adds a duration to the Scheduled date.
     * @param duration the duration to add
     * @param unit the unit of the duration
     * @return the Scheduled date
     * @throws UnsupportedTemporalTypeException if the unit is not supported (e.g. DECADES)
     */
    public Scheduled plus(long duration, ChronoUnit unit){
            return switch (unit) {
                case MILLIS    -> plusMillis(duration);
                case SECONDS   -> plusSeconds(duration);
                case MINUTES   -> plusMinutes(duration);
                case HOURS     -> plusHours(duration);
                case DAYS      -> plusDays(duration);
                case WEEKS -> plusWeeks(duration);
                case MONTHS    -> plusMonths(duration);
                case YEARS     -> plusYears(duration);
                default -> throw new UnsupportedTemporalTypeException("Unsupported unit: " + unit + "\nI guess you have to implement it yourself ;)");
            };
    }

    /**
     * Moves the date later by the given number of milliseconds.
     * Changes this object and returns it, so calls can be chained.
     *
     * @param millisToAdd number of milliseconds
     * @return this date
     */
    public Scheduled plusMillis(long millisToAdd){
        millisecondsUntilDue += millisToAdd;
        return this;
    }

    /**
     * Moves the date later by the given number of seconds.
     * Changes this object and returns it, so calls can be chained.
     *
     * @param secondsToAdd number of seconds
     * @return this date
     */
    public Scheduled plusSeconds(long secondsToAdd){
        millisecondsUntilDue += secondsToAdd * 1000;
        return this;
    }

    /**
     * Moves the date later by the given number of minutes.
     * Changes this object and returns it, so calls can be chained.
     *
     * @param minutesToAdd number of minutes
     * @return this date
     */
    public Scheduled plusMinutes(long minutesToAdd){
        millisecondsUntilDue += minutesToAdd * 1000 * 60;
        return this;
    }

    /**
     * Moves the date later by the given number of hours.
     * Changes this object and returns it, so calls can be chained.
     *
     * @param hoursToAdd number of hours
     * @return this date
     */
    public Scheduled plusHours(long hoursToAdd){
        millisecondsUntilDue += hoursToAdd * 1000 * 60 * 60;
        return this;
    }

    /**
     * Moves the date later by the given number of days.
     * Changes this object and returns it, so calls can be chained.
     *
     * @param daysToAdd number of days
     * @return this date
     */
    public Scheduled plusDays(long daysToAdd){
        millisecondsUntilDue += daysToAdd * 1000 * 60 * 60 * 24;
        return this;
    }

    /**
     * Moves the date later by the given number of weeks.
     * Changes this object and returns it, so calls can be chained.
     *
     * @param weeksToAdd number of weeks
     * @return this date
     */
    public Scheduled plusWeeks(long weeksToAdd){
        millisecondsUntilDue += weeksToAdd * 1000 * 60 * 60 * 24 * 7;
        return this;
    }

    /**
     * Moves the date later by the given number of months (30 days each).
     * Changes this object and returns it, so calls can be chained.
     *
     * @param monthsToAdd number of months (30 days each)
     * @return this date
     */
    public Scheduled plusMonths(long monthsToAdd){
        millisecondsUntilDue += monthsToAdd * 1000 * 60 * 60 * 24 * 30;
        return this;
    }

    /**
     * Moves the date later by the given number of years (365 days each).
     * Changes this object and returns it, so calls can be chained.
     *
     * @param yearsToAdd number of years (365 days each)
     * @return this date
     */
    public Scheduled plusYears(long yearsToAdd){
        millisecondsUntilDue += yearsToAdd * 1000 * 60 * 60 * 24 * 365;
        return this;
    }

    /**
     * Subtracts a duration from the Scheduled date.
     * @param duration the duration to subtract
     * @param unit the unit of the duration
     * @return the Scheduled date
     * @throws UnsupportedTemporalTypeException if the unit is not supported (e.g. DECADES)
     */
    public Scheduled minus(long duration, ChronoUnit unit){
        return switch (unit) {
            case MILLIS    -> minusMillis(duration);
            case SECONDS   -> minusSeconds(duration);
            case MINUTES   -> minusMinutes(duration);
            case HOURS     -> minusHours(duration);
            case DAYS      -> minusDays(duration);
            case WEEKS -> minusWeeks(duration);
            case MONTHS    -> minusMonths(duration);
            case YEARS     -> minusYears(duration);
            default -> throw new UnsupportedTemporalTypeException("Unsupported unit: " + unit + "\nI guess you have to implement it yourself ;)");
        };
    }

    /**
     * Moves the date earlier by the given number of milliseconds.
     * Changes this object and returns it, so calls can be chained.
     *
     * @param millisToAdd number of milliseconds
     * @return this date
     */
    public Scheduled minusMillis(long millisToAdd){
        millisecondsUntilDue -= millisToAdd;
        return this;
    }

    /**
     * Moves the date earlier by the given number of seconds.
     * Changes this object and returns it, so calls can be chained.
     *
     * @param secondsToAdd number of seconds
     * @return this date
     */
    public Scheduled minusSeconds(long secondsToAdd){
        millisecondsUntilDue -= secondsToAdd * 1000;
        return this;
    }

    /**
     * Moves the date earlier by the given number of minutes.
     * Changes this object and returns it, so calls can be chained.
     *
     * @param minutesToAdd number of minutes
     * @return this date
     */
    public Scheduled minusMinutes(long minutesToAdd){
        millisecondsUntilDue -= minutesToAdd * 1000 * 60;
        return this;
    }

    /**
     * Moves the date earlier by the given number of hours.
     * Changes this object and returns it, so calls can be chained.
     *
     * @param hoursToAdd number of hours
     * @return this date
     */
    public Scheduled minusHours(long hoursToAdd){
        millisecondsUntilDue -= hoursToAdd * 1000 * 60 * 60;
        return this;
    }

    /**
     * Moves the date earlier by the given number of days.
     * Changes this object and returns it, so calls can be chained.
     *
     * @param daysToAdd number of days
     * @return this date
     */
    public Scheduled minusDays(long daysToAdd){
        millisecondsUntilDue -= daysToAdd * 1000 * 60 * 60 * 24;
        return this;
    }

    /**
     * Moves the date earlier by the given number of weeks.
     * Changes this object and returns it, so calls can be chained.
     *
     * @param weeksToAdd number of weeks
     * @return this date
     */
    public Scheduled minusWeeks(long weeksToAdd){
        millisecondsUntilDue -= weeksToAdd * 1000 * 60 * 60 * 24 * 7;
        return this;
    }

    /**
     * Moves the date earlier by the given number of months (30 days each).
     * Changes this object and returns it, so calls can be chained.
     *
     * @param monthsToAdd number of months (30 days each)
     * @return this date
     */
    public Scheduled minusMonths(long monthsToAdd){
        millisecondsUntilDue -= monthsToAdd * 1000 * 60 * 60 * 24 * 30;
        return this;
    }

    /**
     * Moves the date earlier by the given number of years (365 days each).
     * Changes this object and returns it, so calls can be chained.
     *
     * @param yearsToAdd number of years (365 days each)
     * @return this date
     */
    public Scheduled minusYears(long yearsToAdd){
        millisecondsUntilDue -= yearsToAdd * 1000 * 60 * 60 * 24 * 365;
        return this;
    }

    /**
     * Converts the Scheduled date to a String.
     * @return the String representation of the Scheduled date
     */
    @Override
    public String toString() {
        return toInstant().toString();
    }
}
