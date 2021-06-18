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

    //Maybe
//    @Override
//    public Venue getVenueById(Long id) {
//        String sql = "SELECT id, name From venue WHERE id = ?";
//        SqlRowSet rows = jdbcTemplate.queryForRowSet(sql, id);
//
//        Venue venue = null;
//        if(rows.next()){
//            venue = mapRowToVenue(rows);
//        }
//        return venue;
//    }

    @Override
    public List<Venue> getAllVenues() {
        //Updated statement to make categories into an aggregate in order to display them all
        String sql = "SELECT  venue.id, venue.name AS venue_name, city.name AS city_name, state_abbreviation, STRING_AGG(category.name, ', ') AS categories, description FROM venue " +
                "JOIN city ON venue.city_id = city.id " +
                "LEFT JOIN category_venue ON venue.id = category_venue.venue_id " +
                "LEFT JOIN category ON category_venue.category_id = category.id " +
                "GROUP BY venue.id, city.name, state_abbreviation " +
                "ORDER BY venue.name";
        SqlRowSet rows = jdbcTemplate.queryForRowSet(sql);
        List<Venue> venues = new ArrayList<Venue>();

        while (rows.next()) {
            Venue venue = mapRowToVenue(rows);
            venues.add(venue);
        }
        return venues;
    }

    private Venue mapRowToVenue(SqlRowSet row) {
        Venue venue = new Venue();

        venue.setId(row.getLong("id"));
        venue.setName(row.getString("venue_name"));
        venue.setCity(row.getString("city_name"));
        venue.setState(row.getString("state_abbreviation"));
        venue.setDescription(row.getString("description"));

        if(row.getString("categories") == null ) {
            venue.setCategory("NA");
        } else {
        venue.setCategory(row.getString("categories"));
        }

        return venue;

    }
}
