package com.example;

import java.time.LocalDate;
import java.time.LocalTime;

public class Booking {
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;

    public Booking(LocalDate date, LocalTime startTime, LocalTime endTime) {
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public LocalDate getDate() {
        return date;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public boolean isOverlapping(LocalDate date, LocalTime startTime, LocalTime endTime) {
        if (!this.date.equals(date)) {
            return false;
        }
        return !startTime.isAfter(this.endTime) && !endTime.isBefore(this.startTime);
    }
}
