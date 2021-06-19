package com.techelevator;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import java.time.LocalDate;

public class JDBCReservationDAOIntegrationTest extends DAOIntegrationTest {

    private JdbcTemplate jdbcTemplate;
    private ReservationDAO reservationDAO;

    @Before
    public void setupBeforeTest() {
        reservationDAO = new JDBCReservationDAO(getDataSource());
        jdbcTemplate = new JdbcTemplate(getDataSource());
    }

    @Test
    public void insert_new_reservation() {
        Reservation newReservation = getReservation(1, "testName");

        reservationDAO.createNewReservation(newReservation);

        Assert.assertTrue(newReservation.getReservationId() > 0);
        Reservation reservationFromDatabase = getReservationById(newReservation.getReservationId());
        Assert.assertEquals(newReservation, reservationFromDatabase);
    }

    private Reservation getReservationById(long reservationId) {
        Reservation reservation = null;
        String sql = "SELECT reservation_id, space_id, number_of_attendees, start_date, end_date, reserved_for FROM reservation WHERE reservation_id = ?";
        SqlRowSet row = jdbcTemplate.queryForRowSet(sql, reservationId);

        if(row.next()) {
            reservation = new Reservation();
            reservation.setReservationId(row.getLong("reservation_id"));
            reservation.setSpaceId(row.getLong("space_id"));
            reservation.setNumberOfAttendees(row.getInt("number_of_attendees"));
            reservation.setStartDate(row.getDate("start_date").toLocalDate());
            reservation.setEndDate(row.getDate("end_date").toLocalDate());
            reservation.setReservedFor(row.getString("reserved_for"));
        }
        return reservation;
    }

    private Reservation getReservation(long reservationId, String reservationName) {
        Reservation reservation = new Reservation();
        reservation.setReservationId(reservationId);
        reservation.setSpaceId(1);
        reservation.setNumberOfAttendees(100);
        reservation.setStartDate(LocalDate.of(2021, 6, 10));
        reservation.setEndDate(LocalDate.of(2021,6, 14));
        reservation.setReservedFor(reservationName);
        return reservation;
    }
}
