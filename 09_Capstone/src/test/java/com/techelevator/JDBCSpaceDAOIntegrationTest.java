package com.techelevator;

import com.techelevator.dao.ReservationDAO;
import com.techelevator.dao.SpaceDAO;
import com.techelevator.dao.VenueDAO;
import com.techelevator.jdbc.JDBCReservationDAO;
import com.techelevator.jdbc.JDBCSpaceDAO;
import com.techelevator.jdbc.JDBCVenueDAO;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class JDBCSpaceDAOIntegrationTest extends DAOIntegrationTest {

    private JdbcTemplate jdbcTemplate;
    private SpaceDAO spaceDAO;
    private VenueDAO venueDAO;
    private ReservationDAO reservationDAO;

    @Before
    public void setupBeforeTest() {
        spaceDAO = new JDBCSpaceDAO(getDataSource());
        venueDAO = new JDBCVenueDAO(getDataSource());
        reservationDAO = new JDBCReservationDAO(getDataSource());
        jdbcTemplate = new JdbcTemplate(getDataSource());
    }

    @Test
    public void retrieve_spaces_by_venue_id() {
        Venue venue = getVenue("testVenueName");
        createNewTestVenue(venue);

        Space space = getSpaceByVenue("testName", venue);
        createNewTestSpace(space);

        //Test
        List<Space> spacesInVenue = spaceDAO.getSpaceByVenueId(venue.getId());

        //Verify
        Assert.assertTrue(isSpaceInList(space, spacesInVenue));
    }

    @Test
    public void no_space_availability() {
       /* //create a new venue
        Venue venue = getVenue("testVenueName");
        createNewTestVenue(venue);

        //create new space (make sure space is associated with venue)
        Space space = getSpaceByReservation(venue, reservation);
        createNewTestSpace(space);

        Reservation reservation = getReservation(1, "testReservationName");
        createNewTestReservation(reservation);
        long numberOfDays = ChronoUnit.DAYS.between(reservation.getStartDate(), reservation.getEndDate());
        List<Space> originalList = spaceDAO.getSpaceAvailability(LocalDate.of(2021,06,12), 5, 20, venue);

        //use dao to query available spaces
        List<Space> availableSpaces = spaceDAO.getSpaceAvailability(reservation.getStartDate(), (int) numberOfDays, reservation.getNumberOfAttendees(), venue);

        //make sure space is in list
        Assert.assertEquals(originalList.size(), availableSpaces.size());*/

        //Setup
        Venue venue = getVenue("testVenueName");
        createNewTestVenue(venue);

        Space space = getSpaceByVenue("testSpaceName", venue);
        createNewTestSpace(space);

        Reservation reservation = getReservation("testReservationName", space);
        createNewTestReservation(reservation);

        //Test
        //These are where there parameters to check for space availability are set: what is used to check against the reservation that we created
        List<Space> availableSpaces = spaceDAO.getSpaceAvailability(LocalDate.of(2021, 6, 9), 2,10, venue);

        //Verify
        Assert.assertEquals("There are no available spaces", 0, availableSpaces.size());
    }

    @Test
    public void space_availability() {
        //Setup
        Venue venue = getVenue("testVenueName");
        createNewTestVenue(venue);

        Space space = getSpaceByVenue("testSpaceName", venue);
        createNewTestSpace(space);

        Reservation reservation = getReservation("testReservationName", space);
        createNewTestReservation(reservation);

        //Test
        List<Space> availableSpaces = spaceDAO.getSpaceAvailability(LocalDate.of(2021, 6, 15), 5,10, venue);

        //Verify
        //These are where there parameters to check for space availability are set: what is used to check against the reservation that we created
        Assert.assertEquals(1, availableSpaces.size());
    }

    private Boolean isSpaceInList(Space space, List<Space> spaces) {
        for(Space spaceToCheck: spaces) {
            if (spaceToCheck.equals(space)) {
                return true;
            }
        }
        return false;
    }

    private Reservation getReservation(String reservationName, Space space) {
        Reservation reservation = new Reservation();
        //Only need to set ids for update clauses
        //reservation.setReservationId(reservationId);
        reservation.setSpaceId(space.getId());
        reservation.setNumberOfAttendees(100);
        reservation.setStartDate(LocalDate.of(2021, 6, 10));
        reservation.setEndDate(LocalDate.of(2021,6, 14));
        reservation.setReservedFor(reservationName);
        return reservation;
    }

    private void createNewTestReservation(Reservation reservation) {
        String sql = "INSERT INTO reservation (space_id, number_of_attendees, start_date, end_date, reserved_for) " +
                "VALUES (?, ?, ?, ?, ?) RETURNING reservation_id";
        SqlRowSet rows = jdbcTemplate.queryForRowSet(sql, reservation.getSpaceId(), reservation.getNumberOfAttendees(), reservation.getStartDate(), reservation.getEndDate(), reservation.getReservedFor());
        rows.next();
        reservation.setReservationId(rows.getLong("reservation_id"));
    }

    /*private  Space getSpaceByReservation(Venue venue, Reservation reservation) {
        Space space = new Space();
        space.setVenueId(venue.getId());
        space.setName("testSpaceName");
        space.setAccessible(true);
        space.setDailyRate(BigDecimal.valueOf(300.00));
        space.setMaxOccupancy(150);
        venue.setName("testVenueName");
        reservation.setStartDate(LocalDate.of(2021, 6, 10));
        reservation.setEndDate(LocalDate.of(2021, 6,14));
        return space;
    }*/

    private void createNewTestSpace(Space space) {
        String sql = "INSERT INTO space (id, venue_id, name, is_accessible, daily_rate, max_occupancy) " +
                "VALUES (DEFAULT, ?, ?, ?, CAST(? AS DECIMAL(10,2)), ?) RETURNING id";
        SqlRowSet row = jdbcTemplate.queryForRowSet(sql, space.getVenueId(), space.getName(), space.isAccessible(), space.getDailyRate(), space.getMaxOccupancy());
        row.next();
        space.setId(row.getLong("id"));
    }

    private Space getSpaceByVenue(String spaceName, Venue venue) {
        Space space = new Space();
        space.setVenueId(venue.getId());
        space.setName(spaceName);
        space.setAccessible(true);
        //space.setOpenFrom(0);
        //space.setOpenTo(0);
        space.setDailyRate(BigDecimal.valueOf(300.00));
        space.setMaxOccupancy(150);
        return space;
    }

    private void createNewTestVenue(Venue venue){
        String sql = "INSERT INTO venue (id, name, city_id, description)" +
                "VALUES(DEFAULT,?,?,?) RETURNING id";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, venue.getName(), venue.getCity_id(), venue.getDescription());
        rowSet.next();
        venue.setId(rowSet.getLong("id"));
    }

    private Venue getVenue(String venueName ) {
        Venue venue = new Venue();
        //venue.setId(venueId);
        venue.setName(venueName);
        venue.setCity_id(1L);
        venue.setDescription("testDescription");
        return venue;
    }

}
