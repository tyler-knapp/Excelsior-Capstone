package com.techelevator;

import com.techelevator.dao.ReservationDAO;
import com.techelevator.dao.SpaceDAO;
import com.techelevator.dao.VenueDAO;
import com.techelevator.view.Menu;

import java.time.LocalDate;
import java.util.List;

public class MakeReservation {

    private Menu menu;
    private Venue venue;
    private VenueDAO venueDAO;
    private Space space;
    private SpaceDAO spaceDAO;
    private Reservation reservation;
    private ReservationDAO reservationDAO;

    private void makeReservation() {
        //Ask user for starting date they require space
        LocalDate startDate = menu.getStartDateFromUser();
        //Ask user for number of days they require space
        int numberOfDays = menu.getNumberOfDaysFromUser();
        //Ask user for number of attendees
        int numberOfAttendees = menu.getNumberOfAttendeesFromUser();
        List<Space> availableSpaces = spaceDAO.getSpaceAvailability(startDate, numberOfDays, numberOfAttendees, venue);
        menu.showAllAvailableSpaces(availableSpaces, numberOfDays);
        //Ask user if they would like to reserve a space from the list provided above
        int searchReservationChoice = menu.getSpaceReservation();
        String reservationName = menu.getNameForReservation();

        Space space = spaceDAO.getSpaceBySpaceId(searchReservationChoice);
        Reservation newReservation = new Reservation();
        newReservation.setSpaceId(searchReservationChoice);
        newReservation.setStartDate(startDate);
        newReservation.setEndDate(startDate.plusDays(numberOfDays));
        newReservation.setNumberOfAttendees(numberOfAttendees);
        newReservation.setReservedFor(reservationName);
        newReservation = reservationDAO.createNewReservation(newReservation);
        menu.showConfirmationDetails(newReservation, venue, space, numberOfDays);
    }
}
