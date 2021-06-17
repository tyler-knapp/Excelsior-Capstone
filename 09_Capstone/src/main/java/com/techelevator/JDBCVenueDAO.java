package com.techelevator;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

public class JDBCVenueDAO implements VenueDAO {

    private JdbcTemplate jdbcTemplate;
    private Venue venue;

    public JDBCVenueDAO(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public List<Venue> getAllVenues() {
        String sql = "SELECT id, name, city_id, description FROM venue GROUP BY id ORDER BY name";
        SqlRowSet rows = jdbcTemplate.queryForRowSet(sql);
        List<Venue> venues = new ArrayList<Venue>();

        while(rows.next()) {
            Venue venue = mapRowToVenue(rows);
            venues.add(venue);
        }
        return venues;
    }

    @Override
    public Venue getVenueById(Long id) {
        return null;
    }

    @Override
    public List<Venue> getVenueDetails() {
        String sql = "SELECT  venue.id, venue.name AS venue_name, city.name AS city_name, state_abbreviation, category.name AS category_name, description FROM venue " +
                "JOIN city ON venue.city_id = city.id " +
                "LEFT JOIN category_venue ON venue.id = category_venue.venue_id " +
                "LEFT JOIN category ON category_venue.category_id = category.id " +
                //"WHERE venue.id = ? " +
                "GROUP BY venue.id, city.name, state_abbreviation, category.name " +
                "ORDER BY venue.name";
        SqlRowSet rows = jdbcTemplate.queryForRowSet(sql);
        List<Venue> venueDetails = new ArrayList<Venue>();

        while (rows.next()) {
            Venue venue = mapRowToVenueDetails(rows);
            venueDetails.add(venue);
        }
        return venueDetails;
    }

    private Venue mapRowToVenue(SqlRowSet row) {
        Venue venue = new Venue();

        venue.setId(row.getLong("id"));
        venue.setName(row.getString("name"));
        venue.setCity_id(row.getLong("city_id"));
        venue.setDescription(row.getString("description"));

        return venue;
    }

    private Venue mapRowToVenueDetails(SqlRowSet row) {
        Venue venue = new Venue();

        venue.setId(row.getLong("id"));
        venue.setName(row.getString("venue_name"));
        venue.setCity(row.getString("city_name"));
        venue.setState(row.getString("state_abbreviation"));
        venue.setCategory(row.getString("category_name"));
        venue.setDescription(row.getString("description"));

        return venue;

    }
}
