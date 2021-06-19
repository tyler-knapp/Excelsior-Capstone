package com.techelevator;

import java.time.LocalDate;
import java.util.List;

public interface SpaceDAO {


    public List<Space> getSpaceByVenueId(long venueId);


    public List<Space> getSpaceAvailability(LocalDate startDate, int numberOfDays, int numberOfAttendees, Venue venue);
}
