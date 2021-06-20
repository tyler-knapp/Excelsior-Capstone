package com.techelevator.view;

import com.techelevator.Reservation;
import com.techelevator.Space;
import com.techelevator.Venue;

import java.math.BigDecimal;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;
import java.text.DateFormatSymbols;



public class Menu {

    private static final Scanner in = new Scanner(System.in);

    public String getMainMenuSelection() {
        System.out.println("\nMAIN MENU");
        System.out.println("--------------------------");
        System.out.println("What would you like to do?");
        System.out.println("    1) List Venues\n    Q) Quit");
        return in.nextLine();
    }

    public String getVenueSelection(List<Venue> venues) {
        System.out.println("\nVENUE LIST");
        System.out.println("-----------------------------------");
        System.out.println("Which venue would you like to view?");
            int count = 0;
            for(Venue venue: venues) {
                count++;
                System.out.println("    " + count + ") " + venue.toString());
            }
        System.out.println("    R) Return to Previous Screen");
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
        System.out.println("    1) View Spaces\n    2) Search for Reservation\n    R) Return to Previous Screen");
        return in.nextLine();
    }


    public void getVenueSpaceHeader(Venue venue){
        System.out.println("\nSPACE LIST");
        System.out.println("-----------------------------------");
        System.out.println(venue.getName() + "Spaces");

    }

    public void showSpaceSelection(List<Space> spaces){
        System.out.println(String.format("%-5s%-40s%-15s%-15s%-18s"," ", "Name", "Open", "Close", "Daily Rate") + "Max. Occupancy");
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
                System.out.println(String.format("%-5s%-40s%-15s%-15s","#" + count, space.getName(), openFrom, openTo) + "$" + String.format("%1.2f%13d", space.getDailyRate(),space.getMaxOccupancy()));
            }
    }

    public void showInvalidSelectionMessage() {
        System.out.println("\n*** YOUR INPUT IS NOT A VALID SELECTION, PLEASE TRY AGAIN! ***");
    }

    public String getSelectionFromSpaceListSubMenu (){
        System.out.println("\nWhat would you like to do next?");
        System.out.println("    1) Reserve a Space\n    R) Return to Previous Screen");
        return in.nextLine();
    }

    public LocalDate getStartDateFromUser(){

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("[M/d/yyyy][M/d/yy][yyyy/M/d][yy/M/d][yy-M-d][yyyy-M-d][M-d-yyyy][M-d-yy]");
        while(true){
            System.out.println("\nRESERVE A SPACE");
            System.out.println("-----------------------------------");
            System.out.print("When do you need this space? ");
            String userInput = in.nextLine();
            LocalDate startDate = null;
            try {
                startDate = LocalDate.parse(userInput, formatter);
            } catch (DateTimeException e){
                showValidDate();
                continue;
            }
            if(startDate == null ) {
                showValidDate();
            } else if(startDate.isBefore(LocalDate.now())){
                showPleaseEnterAFutureDate();
            } else {
                return startDate;
            }
        }
    }

    public int getNumberOfDaysFromUser(){
        while(true){
            System.out.print("How many days will you need the space? ");
            String userInput = in.nextLine();
            int numberOfDays = 0;
            try {
                numberOfDays = Integer.parseInt(userInput);
            } catch (NumberFormatException e){
                showPleaseEnterANumber();
                continue;
            }
            if(numberOfDays <=0 ){
                showPleaseEnterANumberGreaterThanZero();
            } else {
                return numberOfDays;
            }
        }
    }

    public int getNumberOfAttendeesFromUser(){
        while(true){
            System.out.print("How many people will be in attendance? ");
            String userInput = in.nextLine();
            int numberOfAttendees = 0;
            try {
                numberOfAttendees = Integer.parseInt(userInput);
            } catch (NumberFormatException e){
                showPleaseEnterANumber();
                continue;
            }
            if(numberOfAttendees <=0 ){
                showPleaseEnterANumberGreaterThanZero();
            } else {
                return numberOfAttendees;
            }
        }
    }

    public void showPleaseEnterANumber() {
        System.out.println("\n*** YOUR INPUT IS NOT VALID, PLEASE ENTER A NUMBER! ***");
    }

    public void showPleaseEnterAFutureDate(){
        System.out.println("\n*** PLEASE ENTER A FUTURE DATE! ***");
    }

    public void showPleaseEnterANumberGreaterThanZero() {
        System.out.println("\n*** YOUR INPUT IS NOT VALID, PLEASE ENTER A NUMBER GREATER THAN ZERO! ***");
    }

    public void showValidDate() {
        System.out.println("\n*** INVALID DATE, PLEASE TRY AGAIN! ***");
    }

    public void showAllAvailableSpaces(List<Space> spaces, int numberOfDays) {
        System.out.println("\nThe following spaces are available based on your needs: ");
        System.out.println("\n" + String.format("%-10s%-40s%-15s%-15s%-15s", "Space # ", "Name ", "Daily Rate ", "Max Occup.", "Accessible?") + "Total Cost");

        for (Space space : spaces) {
            System.out.println(String.format("%-10s%-40s%-15s%-15s%-15s", space.getId(), space.getName(), "$" + space.getDailyRate().intValue(), space.getMaxOccupancy(), space.isAccessible()) +
                    "$" + (space.getDailyRate().intValue() * numberOfDays));
        }
    }
    public int getSpaceReservation() {
        while(true){
            System.out.print("\nWhich space would you like to reserve (enter 0 to cancel)? ");
            String userInput = in.nextLine();
            int spaceSelection = 0;
            try {
                spaceSelection = Integer.parseInt(userInput);
            } catch (NumberFormatException e){
                showPleaseEnterANumber();
                continue;
            }
                return spaceSelection;
        }
    }

    public String getNewUserSelectionForNoAvailability() {
        System.out.println("\nNo available spaces based on your search.");
        System.out.print("Would you like to try a different search? (Y / N) ");
        return in.nextLine();
    }

    public String getNameForReservation() {
        System.out.print("Who is this reservation for? ");
        return in.nextLine();
    }

    public void showConfirmationDetails(Reservation reservation, Venue venue, Space space, int numberOfDays) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy");
        System.out.println("\nThanks for submitting your reservation! The details for your event is listed below: ");
        System.out.println("\nConfirmation #: " + reservation.getReservationId() + "\n" + "Venue: " + venue.getName() + "\n" +
                "Space: " + space.getName() + "\n" + "Reserved For: " + reservation.getReservedFor() + "\n" +
                "Attendees: " + reservation.getNumberOfAttendees() + "\n" + "Arrival Date: " + reservation.getStartDate().format(formatter) +
                "\n" + "Depart Date: " + reservation.getEndDate().format(formatter) + "\n" + "Total Cost: $" + (space.getDailyRate().intValue() * numberOfDays));
    }

}
