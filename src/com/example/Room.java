package com.example;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class Room {
    private String room_id;
    private String name;
    private int capacity;
    private String location;
    private boolean available;
    private List<Booking> bookings;

    public Room() {
        this.bookings = new ArrayList<>();
    }

    // Getters and Setters
    public String getRoom_id() {
        return room_id;
    }

    public void setRoom_id(String room_id) {
        this.room_id = room_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public List<Booking> getBookings() {
        return bookings;
    }

    public boolean isAvailable(LocalDate date, LocalTime startTime, LocalTime endTime) {
        for (Booking booking : bookings) {
            if (booking.isOverlapping(date, startTime, endTime)) {
                return false;
            }
        }
        return true;
    }

    public void addBooking(LocalDate date, LocalTime startTime, LocalTime endTime) {
        bookings.add(new Booking(date, startTime, endTime));
    }
}

class Booking {
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
