package nl.saxion.oop;




import nl.saxion.oop.model.HotelManager;
import nl.saxion.oop.model.exception.HotelManagerException;

import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Scanner;

public class Application {

    private static final String[] MENU_OPTIONS = {
            "Book a hotel room for a client",                   // 1
            "Book a conference room for a client",              // 2
            "Show available rooms for a specific week",         // 3
            "Show room availability for the year",              // 4
            "Load bookings from CSV",                           // 5
            "Show invoice for client",                          // 6
    };

    public static void main(String[] args) throws FileNotFoundException, HotelManagerException {
        HotelManager hotelManager = new HotelManager();



        int menuSelection;

        do {
            printMenu();

            System.out.print("Please select a menu option: ");
            menuSelection = readInt();
            System.out.println();

            switch (menuSelection) {
                case 1 -> {
                    System.out.print("Please enter the client's name: ");
                    String clientName = readString();

                    System.out.print("Please enter week nr: ");
                    int startWeek = readInt();

                    System.out.print("Please enter the duration of the booking: ");
                    int duration = readInt();
                    try {
                        hotelManager.bookHotelRoom(clientName, startWeek, duration);
                    }catch (HotelManagerException e){
                       System.err.println("error: " + e);
                    }
                }
                case 2 -> {
                    System.out.print("Please enter the client's name: ");
                    String clientName = readString();

                    System.out.print("Please enter the minimum capacity for the conference room: ");
                    int minCapacity = readInt();

                    System.out.print("Please enter week nr: ");
                    int startWeek = readInt();

                    System.out.print("Please enter the duration of the booking: ");
                    int duration = readInt();

                    try {
                        hotelManager.bookConferenceRoom(clientName, minCapacity, startWeek, duration);
                    }catch (IllegalArgumentException e){
                        System.out.println("error " + e);
                    } catch (HotelManagerException e) {
                        throw new RuntimeException(e);
                    }
                }
                case 3 -> {
                    System.out.print("Please enter a week to check availability for: ");
                    int weekNr = readInt();

                    hotelManager.showAvailableRoomPerWeek(weekNr);
                }
                case 4 -> {
                    System.out.print("Please enter a room to show availability: ");
                    String roomId = readString();
                    hotelManager.showRoomAvailabilityPerYear(roomId);
                }
                case 5 -> {
                    String fileName = "bookings.csv";
                    System.out.println(System.getProperty("user.dir"));
                    hotelManager.readFromFile(fileName);
                    System.out.println("The files have been loaded");
                }
                case 6 -> {
                    System.out.print("Please enter the client's name: ");
                    String clientName = readString();
                    hotelManager.showInvoiceForClient(clientName);
                }
                case 0 -> {
                    System.out.println("Goodbye!");
                }
                default -> {
                    System.out.println("Invalid value (out of range), please try again.");
                }
            }

            System.out.println();

        }
        while (menuSelection != 0);

    }

    private static void printMenu() {
        int maxLength = Arrays.stream(MENU_OPTIONS).mapToInt(String::length).max().getAsInt(); // Determine menu item with the longest name.
        int repeatStars = maxLength + 7; // Add stars to cover "* X) " (5 chars) at the front AND " *" (2 chars) at the end.
        String bar = "*".repeat(repeatStars); // Create bar "***..." with desired length.

        System.out.println(bar);

        for (int i = 0; i < MENU_OPTIONS.length; i++) {
            String option = MENU_OPTIONS[i];
            System.out.printf("* %d) %-" + maxLength + "s *%n", i + 1, option);
        }

        System.out.printf("* 0) %-" + maxLength + "s *%n", "Exit");

        System.out.println(bar);
        System.out.println();
    }


    // Helper methods to do some input reading!
    private static final Scanner sc = new Scanner(System.in);

    public static String readString() {
        return sc.nextLine();
    }

    public static int readInt() {
        while (true) {
            try {
                String input = readString();
                return Integer.parseInt(input);
            } catch (NumberFormatException nfe) {
                System.out.print("Invalid value (unable to parse), please try again: ");
            }
        }
    }
}
