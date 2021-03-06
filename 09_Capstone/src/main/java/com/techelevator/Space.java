package com.techelevator;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

public class Space {

    private long id;
    private long venueId;
    private String name;
    private boolean isAccessible;
    private int openFrom;
    private int openTo;
    private BigDecimal dailyRate;
    private long maxOccupancy;

    private String venueName;
    private int numberOfReservations;

    public String getVenueName() {
        return venueName;
    }

    public void setVenueName(String venueName) {
        this.venueName = venueName;
    }

    public int getNumberOfReservations() {
        return numberOfReservations;
    }

    public void setNumberOfReservations(int numberOfReservations) {
        this.numberOfReservations = numberOfReservations;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getVenueId() {
        return venueId;
    }

    public void setVenueId(long venueId) {
        this.venueId = venueId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isAccessible() {
        return isAccessible;
    }

    public void setAccessible(boolean accessible) {
        isAccessible = accessible;
    }

    public int getOpenFrom() {
        return openFrom;
    }

    public void setOpenFrom(int openFrom) {
        this.openFrom = openFrom;
    }

    public int getOpenTo() {
        return openTo;
    }

    public void setOpenTo(int openTo) {
        this.openTo = openTo;
    }

    public BigDecimal getDailyRate() {
        return dailyRate;
    }

    public void setDailyRate(BigDecimal dailyRate) {
        this.dailyRate = dailyRate;
    }

    public long getMaxOccupancy() {
        return maxOccupancy;
    }

    public void setMaxOccupancy(long maxOccupancy) {
        this.maxOccupancy = maxOccupancy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Space space = (Space) o;
        return id == space.id && venueId == space.venueId && isAccessible == space.isAccessible && openFrom == space.openFrom && openTo == space.openTo && maxOccupancy == space.maxOccupancy && numberOfReservations == space.numberOfReservations && Objects.equals(name, space.name) && Objects.equals(dailyRate.doubleValue(), space.dailyRate.doubleValue()) && Objects.equals(venueName, space.venueName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, venueId, name, isAccessible, openFrom, openTo, dailyRate, maxOccupancy, venueName, numberOfReservations);
    }
}
