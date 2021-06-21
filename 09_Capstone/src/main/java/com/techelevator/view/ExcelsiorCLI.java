package com.techelevator.view;

import javax.sql.DataSource;

import com.techelevator.Reservation;
import com.techelevator.Space;
import com.techelevator.Venue;
import com.techelevator.dao.ReservationDAO;
import com.techelevator.dao.SpaceDAO;
import com.techelevator.dao.VenueDAO;
import com.techelevator.jdbc.JDBCReservationDAO;
import com.techelevator.jdbc.JDBCSpaceDAO;
import com.techelevator.jdbc.JDBCVenueDAO;
import org.apache.commons.dbcp2.BasicDataSource;

import java.time.LocalDate;
import java.util.List;

public class ExcelsiorCLI {

	//Private Static Methods
	private static final String MAIN_MENU_DISPLAY_LIST_OF_VENUES = "1";
	private static final String VENUE_MENU_SEARCH_FOR_RESERVATION = "2";
	private static final String VENUE_MENU_SPACES = "1";
	private static final String RESERVE_SPACE = "1";
	private static final String MAIN_MENU_QUIT = "Q";
	private static final String RETURN_TO_PREVIOUS_MENU = "R";
	private static final String CANCEL_RESERVATION_SEARCH = "0";
	private static final String YES = "Y";
	private static final String NO = "N";

	//Instance Variables
	private Menu menu;
	private VenueDAO venueDAO;
	private SpaceDAO spaceDAO;
	private ReservationDAO reservationDAO;

	//Main Method
	public static void main(String[] args) {
		BasicDataSource dataSource = new BasicDataSource();
		dataSource.setUrl("jdbc:postgresql://localhost:5432/excelsior_venues");
		dataSource.setUsername("postgres");
		dataSource.setPassword("postgres1");

		ExcelsiorCLI application = new ExcelsiorCLI(dataSource);
		application.run();
	}

	//Constructor
	public ExcelsiorCLI(DataSource datasource) {
		venueDAO = new JDBCVenueDAO(datasource);
		spaceDAO = new JDBCSpaceDAO(datasource);
		reservationDAO = new JDBCReservationDAO(datasource);
		this.menu = new Menu();
	}

	public void run() {
		outerLoop: {
			while(true) {
				//Display main menu to user
				String mainMenuChoice = menu.getMainMenuSelection();
				//If user enters "Q", quit application
				if(mainMenuChoice.equalsIgnoreCase(MAIN_MENU_QUIT)) {
					break;
				}
				//If user doesn't enter either a "Q" or "1", display "Invalid Selection" message
				if (!mainMenuChoice.equalsIgnoreCase(MAIN_MENU_QUIT) && !mainMenuChoice.equalsIgnoreCase(MAIN_MENU_DISPLAY_LIST_OF_VENUES)){
					menu.showInvalidSelectionMessage();
				}
				//If user enters "1", display all venues
				else if (mainMenuChoice.equalsIgnoreCase(MAIN_MENU_DISPLAY_LIST_OF_VENUES)) {
					//Method to handle all venues, spaces, and reservations
					//Refactored to the individual methods below the run()
					displayListOfAllVenues();
				}
			}
		}
	}

	private boolean displayListOfAllVenues() {
		while(true) {
			//Display list of all venues in the Venue Selection Menu
			List<Venue> venues = venueDAO.getAllVenues();
			String venueMenuChoice = menu.getVenueSelection(venues);
			//If user enters "R", return to the previous screen
			if(venueMenuChoice.equalsIgnoreCase(RETURN_TO_PREVIOUS_MENU)) {
				break;
			}
			//Creating an index from the user input to get the details of a specific venue
			int venueIndex;
			try {
				//Parsing the user's selection and subtracting 1 to get the index of the venue they want details on
				venueIndex = Integer.parseInt(venueMenuChoice) - 1;
				Venue venue = venues.get(venueIndex);
				while(true) {
					//Display venue details to the user
					menu.showVenueDetails(venue);
					//Display the venue sub menu and get user selection
					String venueSubMenuChoice = menu.getSelectionFromVenueListSubMenu();
					//If user enters "R", return to previous screen
					if(venueSubMenuChoice.equalsIgnoreCase(RETURN_TO_PREVIOUS_MENU)) {
						break;
					}
					//If user input is not an "R", "1", or "2", display "Invalid Selection" message
					if(!venueSubMenuChoice.equalsIgnoreCase(RETURN_TO_PREVIOUS_MENU) && !venueSubMenuChoice.equalsIgnoreCase(VENUE_MENU_SPACES)
							&& !venueSubMenuChoice.equalsIgnoreCase(VENUE_MENU_SEARCH_FOR_RESERVATION)) {
						menu.showInvalidSelectionMessage();
					}
					//If user enters "1", display list of all spaces based on venue selection
					else if(venueSubMenuChoice.equalsIgnoreCase(VENUE_MENU_SPACES)) {
						if(displayListOfVenueSpaces(venue)) return true;
					}
					//If user enters "2", prompt user for information to check available spaces based on venue selection
					else if(venueSubMenuChoice.equalsIgnoreCase(VENUE_MENU_SEARCH_FOR_RESERVATION)) {
						if(searchForReservation(venue)) return true;
					}
				}
			} catch(NumberFormatException e) {
				menu.showInvalidSelectionMessage();
				continue;
			} catch(IndexOutOfBoundsException e) {
				menu.showInvalidSelectionMessage();
				continue;
			}
		}
		return false;
	}

	private boolean searchForReservation(Venue venue) {
		while(true) {
			//Ask user for starting date they require space
			LocalDate startDate = menu.getStartDateFromUser();
			//Ask user for number of days they require space
			int numberOfDays = menu.getNumberOfDaysFromUser();
			//Ask user for number of attendees
			int numberOfAttendees = menu.getNumberOfAttendeesFromUser();
			//Display a list of all available spaces based on user input
			List<Space> availableSpaces = spaceDAO.getSpaceAvailability(startDate, numberOfDays, numberOfAttendees, venue);
			//If no spaces are available, indicate that are no available spaces and ask them if they would like to try again
			if(availableSpaces.isEmpty()) {
				String yesNoChoice = menu.getNewUserSelectionForNoAvailability();
				//If user enters "N", return them to previous screen
				if(yesNoChoice.equalsIgnoreCase(NO)) {
					break;
				//If user enters "Y", re-display search dialog
				} else if(yesNoChoice.equalsIgnoreCase(YES)) {
					continue;
				}
			}
			menu.showAllAvailableSpaces(availableSpaces, numberOfDays);
			//Ask user if they would like to reserve a space from the list provided above
			int searchReservationChoice; //= menu.getSpaceReservation();
			while(true) {
				//Ask user if they would like to reserve a space from the list provided above
				searchReservationChoice = menu.getSpaceReservation();
				//If user enters "0", return to previous screen
				if(searchReservationChoice == Integer.parseInt(CANCEL_RESERVATION_SEARCH)) {
					menu.showVenueDetails(venue);
					break;
				}
				boolean isSpaceAvailable = false;
				for(Space availableSpace : availableSpaces) {
					//If user inputs valid space selection (space id of an available space) create reservation and
					//display confirmation details
					if(searchReservationChoice == availableSpace.getId()) {
						isSpaceAvailable = true;
						break;
					}
				}
				//If space selection is not an available space, display "Invalid Selection" message
				if(!isSpaceAvailable) {
					menu.showInvalidSelectionMessage();
					continue;
				}
				else break;
			}
			//If user, enters valid selection, create a reservation and display their confirmation details
			return createReservation(venue, startDate, numberOfDays, numberOfAttendees, searchReservationChoice);
		}
		return false;
	}

	private boolean displayListOfVenueSpaces(Venue venue) {
		while(true) {
			//Display list of spaces based on user venue selection
			menu.getVenueSpaceHeader(venue);
			List<Space> spaces = spaceDAO.getSpaceByVenueId(venue.getId());
			menu.showSpaceSelection(spaces);
			//Get user input for space sub menu choices
			String spaceSubMenuChoice = menu.getSelectionFromSpaceListSubMenu();
			//If user enters "R", return to previous screen
			if(spaceSubMenuChoice.equalsIgnoreCase(RETURN_TO_PREVIOUS_MENU)) {
				menu.showVenueDetails(venue);
				break;
			//If user enters "1", prompt user for information to check available spaces based on venue selection
			} else if(spaceSubMenuChoice.equalsIgnoreCase(RESERVE_SPACE)) {
				reservationLoop : {
					while (true) {
						//Ask user for starting date they require space
						LocalDate startDate = menu.getStartDateFromUser();
						//Ask user for number of days they require space
						int numberOfDays = menu.getNumberOfDaysFromUser();
						//Ask user for number of attendees
						int numberOfAttendees = menu.getNumberOfAttendeesFromUser();
						//Display a list of all available spaces based on user input
						List<Space> availableSpaces = spaceDAO.getSpaceAvailability(startDate, numberOfDays, numberOfAttendees, venue);
						//If no spaces are available, indicate that are no available spaces and ask them if they would like to try again
						if(availableSpaces.isEmpty()) {
							String yesNoChoice = menu.getNewUserSelectionForNoAvailability();
							//If user enters "N", return them to previous screen
							if(yesNoChoice.equalsIgnoreCase(NO)) {
								break;
								//If user enters "Y", re-display search dialog
							} else if(yesNoChoice.equalsIgnoreCase(YES)) {
								continue;
							}
						}
						menu.showAllAvailableSpaces(availableSpaces, numberOfDays);
						int searchReservationChoice;
						while(true) {
							//Ask user if they would like to reserve a space from the list provided above
							searchReservationChoice = menu.getSpaceReservation();
							//If user enters "0", return to previous screen
							if(searchReservationChoice == Integer.parseInt(CANCEL_RESERVATION_SEARCH)) {
								break reservationLoop;
							}
							boolean isSpaceAvailable = false;
							for(Space availableSpace : availableSpaces) {
								//If user inputs valid space selection (space id of an available space) create reservation and
								//display confirmation details
								if(searchReservationChoice == availableSpace.getId()) {
									isSpaceAvailable = true;
									break;
								}
							}
							//If space selection is not an available space, display "Invalid Selection" message
							if(!isSpaceAvailable) {
								menu.showInvalidSelectionMessage();
								continue;
							}
							else break;
						}
						return createReservation(venue, startDate, numberOfDays, numberOfAttendees, searchReservationChoice);
					}
				}
			} else {
				menu.showInvalidSelectionMessage();
			}
		}
		return false;
	}

	private boolean createReservation(Venue venue, LocalDate startDate, int numberOfDays, int numberOfAttendees, int availableSpaceId) {
		while(true) {
			//Ask user who the reservation is for
			String reservationName = menu.getNameForReservation();
			//Get list of spaces by the space id
			Space space = spaceDAO.getSpaceBySpaceId(availableSpaceId);
			//Create new reservation with the below user provided inputs
			Reservation newReservation = new Reservation();
			newReservation.setSpaceId(availableSpaceId);
			newReservation.setStartDate(startDate);
			newReservation.setEndDate(startDate.plusDays(numberOfDays));
			newReservation.setNumberOfAttendees(numberOfAttendees);
			newReservation.setReservedFor(reservationName);
			newReservation = reservationDAO.createNewReservation(newReservation);
			//Display confirmation details to the user
			menu.showConfirmationDetails(newReservation, venue, space, numberOfDays);
			return true;
		}
	}

}
