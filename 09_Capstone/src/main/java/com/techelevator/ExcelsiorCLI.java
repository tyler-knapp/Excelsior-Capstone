package com.techelevator;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;

public class ExcelsiorCLI {

	private static final String MAIN_MENU_DISPLAY_LIST_OF_VENUES = "1";
	private static final String MAIN_MENU_QUIT = "Q";

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
			String choice = menu.getMainMenuSelection();
			if(choice.equalsIgnoreCase(MAIN_MENU_QUIT)) {
				break;
			}
			else if(choice.equalsIgnoreCase(MAIN_MENU_DISPLAY_LIST_OF_VENUES)) {
				menu.showListOfVenues(venueDAO);
			}
		}
		//Please remove and add to menu
		System.out.println("Goodbye");
	}

}
