package com.techelevator.jdbc;

import com.techelevator.Space;
import com.techelevator.dao.SpaceDAO;
import com.techelevator.Venue;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;

public class JDBCSpaceDAO implements SpaceDAO {

    private JdbcTemplate jdbcTemplate;
    //private Menu menu;
    private Space space;

    public JDBCSpaceDAO(DataSource datasource) {
        this.jdbcTemplate = new JdbcTemplate(datasource);

    }

    @Override
    public List<Space> getSpaceByVenueId(long id) {
        String sql = "SELECT id, venue_id, name, is_accessible, open_from, open_to, CAST(daily_rate AS DECIMAL(10,2)), max_occupancy FROM space WHERE venue_id = ?";
        SqlRowSet rows = jdbcTemplate.queryForRowSet(sql, id);
        List<Space> spacesByVenueId = new ArrayList<Space>();

        while(rows.next()){
            Space space = mapRowToSpace(rows);
            spacesByVenueId.add(space);
        }
        return spacesByVenueId;
    }

    @Override
    public Space getSpaceBySpaceId(long spaceId) {
        String sql = "SELECT id, venue_id, name, is_accessible, open_from, open_to, CAST(daily_rate AS DECIMAL(10,2)), max_occupancy FROM space WHERE id = ?";
        SqlRowSet rows = jdbcTemplate.queryForRowSet(sql, spaceId);

        Space space = null;
        if(rows.next()) {
            space = mapRowToSpace(rows);
        }
        return space;
    }

    @Override
    public List<Space> getSpaceAvailability(LocalDate startDate, int numberOfDays, int numberOfAttendees, Venue venue) {
        LocalDate endDate = startDate.plusDays(numberOfDays);
        int startMonth = startDate.getMonth().getValue();
        int endMonth = endDate.getMonth().getValue();
        String sql = "SELECT space.id, venue.name AS venue_name, space.name AS space_name, coalesce(space_reservation_count.number_of_reservations, 0) AS number_of_reservations, " +
                "space.max_occupancy, space.open_from, space.open_to, CAST(space.daily_rate AS DECIMAL(10,2)) " +
                "FROM venue " +
                "LEFT JOIN space ON venue.id = space.venue_id " +
                "LEFT JOIN (" +
                "        SELECT count(*) AS number_of_reservations, space_id " +
                "        from reservation " +
                "        WHERE start_date <= ? AND ? <= end_date " +
                "        GROUP BY space_id " +
                ") AS space_reservation_count ON space_reservation_count.space_id = space.id " +
                "WHERE space.max_occupancy > ? AND (open_from IS NULL OR open_from <= ?) AND (open_to IS NULL OR open_to >= ?) AND number_of_reservations IS NULL AND space.venue_id = ? " +
                "LIMIT 5";
        SqlRowSet rows = jdbcTemplate.queryForRowSet(sql, endDate, startDate, numberOfAttendees, startMonth, endMonth, venue.getId());
        List<Space> spacesByAvailability = new ArrayList<Space>();

        while(rows.next()){
            Space space = mapRowToSpaceAvailability(rows);
            spacesByAvailability.add(space);
        }
        return spacesByAvailability;
    }

    private Space mapRowToSpace(SqlRowSet row) {
        Space space = new Space();
        space.setId(row.getLong("id"));
        space.setVenueId(row.getLong("venue_id"));
        space.setName(row.getString("name"));
        space.setAccessible(row.getBoolean("is_accessible"));
        space.setDailyRate(row.getBigDecimal("daily_rate"));
        space.setMaxOccupancy(row.getLong("max_occupancy"));

        if (row.getInt("open_to") != 0) {
            space.setOpenTo(row.getInt("open_to"));
        }
        if (row.getInt("open_from") != 0) {
            space.setOpenFrom(row.getInt("open_from"));
        }
        return space;
    }

    private Space mapRowToSpaceAvailability(SqlRowSet row) {
        Space space = new Space();
        space.setId(row.getLong("id"));
        space.setVenueName(row.getString("venue_name"));
        space.setName(row.getString("space_name"));
        space.setNumberOfReservations(row.getInt("number_of_reservations"));
        space.setMaxOccupancy(row.getLong("max_occupancy"));
        space.setDailyRate(row.getBigDecimal("daily_rate"));

        if (row.getInt("open_to") != 0) {
            space.setOpenTo(row.getInt("open_to"));
        }
        if (row.getInt("open_from") != 0) {
            space.setOpenFrom(row.getInt("open_from"));
        }
        return space;
    }


}


