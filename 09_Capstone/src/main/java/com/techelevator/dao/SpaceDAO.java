package com.techelevator.dao;

import com.techelevator.Space;
import com.techelevator.Venue;

import java.time.LocalDate;
import java.util.List;

public interface SpaceDAO {

    public List<Space> getSpaceByVenueId(long venueId);

    public Space getSpaceBySpaceId(long spaceId);

    public List<Space> getSpaceAvailability(LocalDate startDate, int numberOfDays, int numberOfAttendees, Venue venue);
}
