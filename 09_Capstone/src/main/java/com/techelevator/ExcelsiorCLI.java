package com.techelevator;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;

import java.util.List;

public class ExcelsiorCLI {

	private static final String MAIN_MENU_DISPLAY_LIST_OF_VENUES = "1";
	private static final String MAIN_MENU_QUIT = "Q";
	private static final String RETURN_TO_PREVIOUS_MENU = "R";

	private Menu menu;
	private VenueDAO venueDAO;

	public static void main(String[] args) {
		BasicDataSource dataSource = new BasicDataSource();
		dataSource.setUrl("jdbc:postgresql://localhost:5432/excelsior_venues");
		dataSource.setUsername("postgres");
		dataSource.setPassword("postgres1");

		ExcelsiorCLI application = new ExcelsiorCLI(dataSource);
		application.run();
	}

	//DataSource was passed in an argument from original code
	public ExcelsiorCLI(DataSource datasource) {
		venueDAO = new JDBCVenueDAO(datasource);
		this.menu = new Menu();
	}

	public void run() {
		while(true) {
			String mainMenuChoice = menu.getMainMenuSelection();
			if(mainMenuChoice.equalsIgnoreCase(MAIN_MENU_QUIT)) {
				break;
			}
			else if(mainMenuChoice.equalsIgnoreCase(MAIN_MENU_DISPLAY_LIST_OF_VENUES)) {
//				//Get a list of venues to display in the Venue Selection Menu
//				List<Venue> venues = venueDAO.getAllVenues();
//				String venueMenuChoice = menu.getVenueSelection(venues);
//				//Creating an index from the user input to get the details of a specific venue
//				int venueIndex = Integer.parseInt(venueMenuChoice) - 1;
//				Venue venue = venues.get(venueIndex);
//				menu.showVenueDetails(venue);
				while(true) {
					//Get a list of venues to display in the Venue Selection Menu
					List<Venue> venues = venueDAO.getAllVenues();
					String venueMenuChoice = menu.getVenueSelection(venues);
					//if statement was included to make sure that a user enters a value and not an empty string
					//otherwise, a exception will occur
					if(venueMenuChoice.equalsIgnoreCase("")) {
						continue;
					}
					//Moved this above venueIndex to avoid the input being parsed into an int
					if(venueMenuChoice.equalsIgnoreCase(RETURN_TO_PREVIOUS_MENU)) {
						break;
					}
					//Creating an index from the user input to get the details of a specific venue
					int venueIndex;
					try {
						venueIndex = Integer.parseInt(venueMenuChoice) - 1;
					} catch (NumberFormatException e) {
						menu.showInvalidSelectionMessage();
						continue;
					}



					Venue venue = venues.get(venueIndex);
					menu.showVenueDetails(venue);

					while(true) {
						String venueSubMenuChoice = menu.getSelectionFromVenueListSubMenu();
						if (venueSubMenuChoice.equalsIgnoreCase(RETURN_TO_PREVIOUS_MENU)) {
							break;
						}
					}
				}
			}
		}
		//Please remove and add to menu
		System.out.println("Goodbye");
	}

}
