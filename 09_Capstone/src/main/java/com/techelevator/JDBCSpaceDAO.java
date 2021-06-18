package com.techelevator;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;

public class JDBCSpaceDAO implements SpaceDAO {

    private JdbcTemplate jdbcTemplate;
    private Space space;

    public JDBCSpaceDAO(DataSource datasource) {
        this.jdbcTemplate = new JdbcTemplate(datasource);

    }


    @Override
    public List<Space> getAllSpaces() {
        String sql = "SELECT id, venue_id, name, is_accessible, open_from, open_to, CAST(daily_rate AS DECIMAL(10,2)), max_occupancy FROM space";
        SqlRowSet rows = jdbcTemplate.queryForRowSet(sql);

        List<Space> spaces = new ArrayList<Space>();
        while (rows.next()) {
            Space space = mapRowToSpace(rows);
            spaces.add(space);
        }
        return spaces;
    }

    @Override
    public List<Space> getSpaceByVenueId(long id) {
        String sql = "SELECT id, venue_id, name, is_accessible, open_from, open_to, daily_rate, max_occupancy FROM space WHERE venue_id = ?";
        SqlRowSet rows = jdbcTemplate.queryForRowSet(sql);
        List<Space> spacesByVenueId = new ArrayList<Space>();

        while(rows.next()){
            Space space = mapRowToSpace(rows);
            spacesByVenueId.add(space);
        }
        return spacesByVenueId;
    }

    @Override
    public List<Space> getSpaceByName(String name) {
        return null;
    }

    @Override
    public List<Space> getSpaceAvailability(long maxOccupancy) {
        return null;
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
}


