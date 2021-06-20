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
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ExcelsiorCLI {

	private static final String MAIN_MENU_DISPLAY_LIST_OF_VENUES = "1";
	private static final String VENUE_MENU_SEARCH_FOR_RESERVATION = "2";
	private static final String VENUE_MENU_SPACES = "1";
	private static final String RESERVE_SPACE = "1";
	private static final String MAIN_MENU_QUIT = "Q";
	private static final String RETURN_TO_PREVIOUS_MENU = "R";
	private static final String CANCEL_RESERVATION_SEARCH = "0";
	private static final String YES = "Y";
	private static final String NO = "N";

	private Menu menu;
	private VenueDAO venueDAO;
	private SpaceDAO spaceDAO;
	private ReservationDAO reservationDAO;

	public static void main(String[] args) {
		BasicDataSource dataSource = new BasicDataSource();
		dataSource.setUrl("jdbc:postgresql://localhost:5432/excelsior_venues");
		dataSource.setUsername("postgres");
		dataSource.setPassword("postgres1");

		ExcelsiorCLI application = new ExcelsiorCLI(dataSource);
		application.run();
	}

	public ExcelsiorCLI(DataSource datasource) {
		venueDAO = new JDBCVenueDAO(datasource);
		spaceDAO = new JDBCSpaceDAO(datasource);
		reservationDAO = new JDBCReservationDAO(datasource);
		this.menu = new Menu();
	}

	public void run() {
		outerLoop: {
			while (true) {
				//Display main menu to user
				String mainMenuChoice = menu.getMainMenuSelection();
				//If user enters "Q", quit application
				if (mainMenuChoice.equalsIgnoreCase(MAIN_MENU_QUIT)) {
					break;
				}
				if (!mainMenuChoice.equalsIgnoreCase(MAIN_MENU_QUIT) && !mainMenuChoice.equalsIgnoreCase(MAIN_MENU_DISPLAY_LIST_OF_VENUES) ){
					menu.showInvalidSelectionMessage();
				}
				//If user enters "1", display all venues
				else if (mainMenuChoice.equalsIgnoreCase(MAIN_MENU_DISPLAY_LIST_OF_VENUES)) {
					while (true) {
						//Get a list of venues to display in the Venue Selection Menu
						List<Venue> venues = venueDAO.getAllVenues();
						String venueMenuChoice = menu.getVenueSelection(venues);
						//The if statement below was included to make sure that a user enters a value and not an empty string
						//otherwise, a exception will occur

						//Moved this above venueIndex to avoid the input being parsed into an int
						//If user enters "R", return to the previous screen
						if (venueMenuChoice.equalsIgnoreCase(RETURN_TO_PREVIOUS_MENU)) {
							break;
						}
						//Creating an index from the user input to get the details of a specific venue
						int venueIndex;
						try {
							//Parsing the user's selection and subtracting 1 to get the index of the venue they want details on
							venueIndex = Integer.parseInt(venueMenuChoice) - 1;
							//Catching NumberFormatException and displaying error message
							//Display venue details to the user
							Venue venue = venues.get(venueIndex);
							menu.showVenueDetails(venue);
							while (true) {
								//Display the venue sub menu and get user selection
								String venueSubMenuChoice = menu.getSelectionFromVenueListSubMenu();
								//If user enters "R", return to previous screen
								if (venueSubMenuChoice.equalsIgnoreCase(RETURN_TO_PREVIOUS_MENU)) {
									//continue;
									break;
								}
								if (!venueSubMenuChoice.equalsIgnoreCase(RETURN_TO_PREVIOUS_MENU) && !venueSubMenuChoice.equalsIgnoreCase(VENUE_MENU_SPACES)
										&& !venueSubMenuChoice.equalsIgnoreCase(VENUE_MENU_SEARCH_FOR_RESERVATION)){
									menu.showInvalidSelectionMessage();
								}
								else if (venueSubMenuChoice.equalsIgnoreCase(VENUE_MENU_SPACES)) {

									while (true) {
										//Display list of venue spaces based on user venue selection
										menu.getVenueSpaceHeader(venue);
										List<Space> spaces = spaceDAO.getSpaceByVenueId(venue.getId());
										menu.showSpaceSelection(spaces);
										//If user enters "R", return to previous screen
										String spaceSubMenuChoice = menu.getSelectionFromSpaceListSubMenu();
										if (spaceSubMenuChoice.equalsIgnoreCase(RETURN_TO_PREVIOUS_MENU)) {
											menu.showVenueDetails(venue);
											break;
										} else if (spaceSubMenuChoice.equalsIgnoreCase(RESERVE_SPACE)) {
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
													if (availableSpaces.isEmpty()) {
														String yesNoChoice = menu.getNewUserSelectionForNoAvailability();
														if (yesNoChoice.equalsIgnoreCase(NO)) {
															break;
														} else if (yesNoChoice.equalsIgnoreCase(YES)) {
															continue;
														}
													}
													menu.showAllAvailableSpaces(availableSpaces, numberOfDays);
													//Ask user if they would like to reserve a space from the list provided above
													int availableSpaceId;
													while (true) {
														availableSpaceId = menu.getSpaceReservation();
														if (availableSpaceId == Integer.parseInt(CANCEL_RESERVATION_SEARCH)) {
															//menu.showVenueDetails(venue);
															break reservationLoop;
														}
														boolean isSpaceFound = false;
														for (Space currentSpace : availableSpaces) {
															if (availableSpaceId == currentSpace.getId()) {
																isSpaceFound = true;
																break;
															}
														}
														if (!isSpaceFound) {
															menu.showInvalidSelectionMessage();
															continue;
														}
														else break;
													}
													while (true) {
														String reservationName = menu.getNameForReservation();

														Space space = spaceDAO.getSpaceBySpaceId(availableSpaceId);
														Reservation newReservation = new Reservation();
														newReservation.setSpaceId(availableSpaceId);
														newReservation.setStartDate(startDate);
														newReservation.setEndDate(startDate.plusDays(numberOfDays));
														newReservation.setNumberOfAttendees(numberOfAttendees);
														newReservation.setReservedFor(reservationName);
														newReservation = reservationDAO.createNewReservation(newReservation);
														menu.showConfirmationDetails(newReservation, venue, space, numberOfDays);
														break outerLoop;
													}
												}
											}
										}
									}
								}
								//If user enters "2", prompt user for information to check available spaces based on venue selection
								else if (venueSubMenuChoice.equalsIgnoreCase(VENUE_MENU_SEARCH_FOR_RESERVATION)) {
									while (true) {
										//Ask user for starting date they require space
										LocalDate startDate = menu.getStartDateFromUser();
										//Ask user for number of days they require space
										int numberOfDays = menu.getNumberOfDaysFromUser();
										//Ask user for number of attendees
										int numberOfAttendees = menu.getNumberOfAttendeesFromUser();
										//Display a list of all available spaces based on user input
										List<Space> availableSpaces = spaceDAO.getSpaceAvailability(startDate, numberOfDays, numberOfAttendees, venue);
										if (availableSpaces.isEmpty()) {
											String yesNoChoice = menu.getNewUserSelectionForNoAvailability();
											if (yesNoChoice.equalsIgnoreCase(NO)) {
												break;
											} else if (yesNoChoice.equalsIgnoreCase(YES)) {
												continue;
											}
										}
										menu.showAllAvailableSpaces(availableSpaces, numberOfDays);
										//Ask user if they would like to reserve a space from the list provided above
										int searchReservationChoice = menu.getSpaceReservation();

										if (searchReservationChoice == Integer.parseInt(CANCEL_RESERVATION_SEARCH)) {
											menu.showVenueDetails(venue);
											break;
										}
										while (true) {
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
											break outerLoop;
										}
									}
								}
							}
						} catch (NumberFormatException e) {
							menu.showInvalidSelectionMessage();
							continue;
						} catch (IndexOutOfBoundsException e){
							menu.showInvalidSelectionMessage();
							continue;
						}
					}
				}
			}
		}
	}

}
