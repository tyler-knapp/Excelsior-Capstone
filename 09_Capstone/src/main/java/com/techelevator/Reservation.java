package com.techelevator;

import java.time.LocalDate;
import java.util.Objects;

public class Reservation {

    private long reservationId;
    private long spaceId;
    private int numberOfAttendees;
    private LocalDate startDate;
    private LocalDate endDate;
    private String reservedFor;

    public long getReservationId() {
        return reservationId;
    }

    public void setReservationId(long reservationId) {
        this.reservationId = reservationId;
    }

    public long getSpaceId() {
        return spaceId;
    }

    public void setSpaceId(long spaceId) {
        this.spaceId = spaceId;
    }

    public int getNumberOfAttendees() {
        return numberOfAttendees;
    }

    public void setNumberOfAttendees(int numberOfAttendees) {
        this.numberOfAttendees = numberOfAttendees;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public String getReservedFor() {
        return reservedFor;
    }

    public void setReservedFor(String reservedFor) {
        this.reservedFor = reservedFor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Reservation that = (Reservation) o;
        return reservationId == that.reservationId && spaceId == that.spaceId && numberOfAttendees == that.numberOfAttendees && Objects.equals(startDate, that.startDate) && Objects.equals(endDate, that.endDate) && Objects.equals(reservedFor, that.reservedFor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(reservationId, spaceId, numberOfAttendees, startDate, endDate, reservedFor);
    }
}
