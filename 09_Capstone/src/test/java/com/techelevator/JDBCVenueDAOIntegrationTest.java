package com.techelevator;

import org.junit.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import java.util.List;

public class JDBCVenueDAOIntegrationTest extends DAOIntegrationTest{

    //this variable must be static because we will have to instantiate
    //private static SingleConnectionDataSource dataSource;
    private JdbcTemplate jdbcTemplate;
    private VenueDAO venueDAO;

    @Before
    public void setupBeforeTest() {
        venueDAO = new JDBCVenueDAO(getDataSource());
        jdbcTemplate = new JdbcTemplate(getDataSource());
    }

    @Test
    public void retrieve_all_venues(){
        List<Venue> originalList = venueDAO.getAllVenues();
        Venue venueOne = getVenue(1, "test");
        Venue venueTwo = getVenue(2,"Test2");
        createNewTestVenue(venueOne);
        createNewTestVenue(venueTwo);

        //TEST
        List<Venue> venueFromDataBase = venueDAO.getAllVenues();
        //ASSERT
        Assert.assertEquals(originalList.size() + 2 , venueFromDataBase.size());
    }

    private void createNewTestVenue(Venue venue){
    String sql = "INSERT INTO venue (id, name, city_id, description)" +
            "VALUES(DEFAULT,?,?,?) RETURNING id";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, venue.getName(), venue.getCity_id(), venue.getDescription());
        rowSet.next();
        venue.setId(rowSet.getLong("id"));
    }

    private Venue getVenue(long venueId, String venueName ) {
        Venue venue = new Venue();
        venue.setId(venueId);
        venue.setName(venueName);
        venue.setCity_id(1L);
        venue.setDescription("testDescription");
        return venue;
    }

}

