package com.techelevator;

import java.util.List;
import java.util.Scanner;
import java.text.DateFormatSymbols;



public class Menu {

    private static final Scanner in = new Scanner(System.in);

    public String getMainMenuSelection() {
        System.out.println("\nMAIN MENU");
        System.out.println("--------------------------");
        System.out.println("What would you like to do?");
        System.out.println("1) List Venues\nQ) Quit");
        return in.nextLine();
    }

    public String getVenueSelection(List<Venue> venues) {
        System.out.println("\nVENUE LIST");
        System.out.println("-----------------------------------");
        System.out.println("Which venue would you like to view?");
        int count = 0;
        for(Venue venue: venues) {
            count++;
            System.out.println(count + ") " + venue.toString());
        }
        System.out.println("R) Return to Previous Screen");
        return in.nextLine();
    }

    public void showVenueDetails(Venue venue) {
        System.out.println("\nVENUE DETAIL");
        System.out.println("-----------------------------------");
        System.out.println(venue.getName() + "\nLocation: " + venue.getCity() + ", " + venue.getState() +
                "\nCategories: " + venue.getCategory() + "\n\n" + venue.getDescription());
    }

    public String getSelectionFromVenueListSubMenu() {
        System.out.println("\nWhat would you like to do next?");
        System.out.println("1) View Spaces\n2) Search for Reservation\nR) Return to Previous Screen");
        return in.nextLine();
    }


    public void getVenueSpaceHeader(Venue venue){
        System.out.println("\nSPACE LIST");
        System.out.println("------------------------------------");
        System.out.println(venue.getName() + "Spaces");

    }

    public void getSpaceSelection(List<Space> spaces){
        System.out.println("Name " + "Open " + "Close " + "Daily Rate " + "Max. Occupancy");
        int count = 0;

        for(Space space : spaces){
            count++;
            String[] monthStrings = new DateFormatSymbols().getMonths();
            String openFrom = "";
            if(space.getOpenFrom() > 0){
                openFrom = monthStrings[space.getOpenFrom()-1];
            }
            String openTo ="";
            if(space.getOpenTo() > 0){
                openTo = monthStrings[space.getOpenTo()-1];
            }
            System.out.println("#" + count +" "+ space.getName() + " " + openFrom + " " + openTo + " $" + space.getDailyRate() + " " + space.getMaxOccupancy() );
        }
    }

    public void showInvalidSelectionMessage() {
        System.out.println("\nInvalid selection, please try again.");
    }
}
