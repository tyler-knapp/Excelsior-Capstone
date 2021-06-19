package com.techelevator;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import javax.sql.DataSource;

public class JDBCReservationDAO implements  ReservationDAO {

    private JdbcTemplate jdbcTemplate;
    private Reservation reservation;

    public JDBCReservationDAO(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public Reservation createNewReservation(Reservation newReservation) {
        String sql = "INSERT INTO reservation (space_id, number_of_attendees, start_date, end_date, reserved_for) " +
                "VALUES (?, ?, ?, ?, ?) RETURNING reservation_id";
        SqlRowSet rows = jdbcTemplate.queryForRowSet(sql, newReservation.getSpaceId(), newReservation.getNumberOfAttendees(), newReservation.getStartDate(), newReservation.getEndDate(), newReservation.getReservedFor());
        rows.next();
        newReservation.setReservationId(rows.getLong("reservation_id"));
        return newReservation;
    }
}
