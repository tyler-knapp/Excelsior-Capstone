package com.techelevator;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import java.math.BigDecimal;
import java.util.List;

public class JDBCSpaceDAOIntegrationTest extends DAOIntegrationTest {

    private JdbcTemplate jdbcTemplate;
    private SpaceDAO spaceDAO;
    private VenueDAO venueDAO;

    @Before
    public void setupBeforeTest() {
        spaceDAO = new JDBCSpaceDAO(getDataSource());
        venueDAO = new JDBCVenueDAO(getDataSource());
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

    private Boolean isSpaceInList(Space space, List<Space> spaces) {
        for(Space spaceToCheck: spaces) {
            if (spaceToCheck.equals(space)) {
                return true;
            }
        }
        return false;
    }

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
