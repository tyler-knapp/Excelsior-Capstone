package com.techelevator;

import java.util.Scanner;

public class Menu {

    private static final Scanner in = new Scanner(System.in);
    private VenueDAO venueDAO;

    public String getMainMenuSelection() {
        System.out.println("\nMAIN MENU");
        System.out.println("--------------------------");
        System.out.println("What would you like to do?");
        System.out.println("1) List Venues\nQ) Quit");
        return in.nextLine();
    }

    public String showListOfVenues(VenueDAO venueDAO) {
        System.out.println("Which venue would you like to view?");
        System.out.println("-----------------------------------");
        int count = 0;
        for(Venue venue: venueDAO.getAllVenues()) {
            count++;
            System.out.println(count + ") " + venue.toString());
        }
        return in.nextLine();
    }
}
