package nl.saxion.oop.model;

import nl.saxion.oop.model.clients.Client;
import nl.saxion.oop.model.clients.Company;
import nl.saxion.oop.model.clients.Person;
import nl.saxion.oop.model.exception.HotelManagerException;
import nl.saxion.oop.model.rooms.ConferenceRoom;
import nl.saxion.oop.model.rooms.HotelRoom;
import nl.saxion.oop.model.rooms.Room;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class HotelManager {


    private final List<Room> rooms = new ArrayList<>(); // keeps the created rooms
    private final Map<Room, boolean[]> weeks = new HashMap<>();            // booking calendar per room
    private final List<Booking> bookings = new ArrayList<>();
    private Client[] clients;

    private static final int WEEKS_IN_YEAR = 52;


    public HotelManager() {
        initializeClients();
        initializeRooms();
    }


    public void bookHotelRoom(String clientName, int startWeek, int duration) throws HotelManagerException {
        validateWeek(startWeek);
        Client client = findClient(clientName);
        Room room = findAvailableHotelRoom(startWeek, duration);

        if (room == null) {
            throw new HotelManagerException(
                    "No hotel room available for week " + startWeek + " and duration " + duration);
        }
        bookRoom(client, room, startWeek, duration);
        System.out.printf("Room %s booked successfully for %s%n", room, clientName);
    }

    public void bookConferenceRoom(String companyName, int minCapacity, int startWeek, int duration) throws HotelManagerException {
        validateWeek(startWeek);
        Client company = findClient(companyName);
        validateCompany(company);

        Room room = findAvailableConferenceRoom(minCapacity, startWeek, duration);
        if (room == null) {
            throw new HotelManagerException(
                    "No conference room available for week " + startWeek + " (duration "
                            + duration + ", capacity ≥ " + minCapacity + ")");
        }
        bookRoom(company, room, startWeek, duration);
        System.out.printf("Conference room %s booked successfully for %s%n", room, companyName);
    }


    public void showAvailableRoomPerWeek(int weekNr) throws HotelManagerException {
        validateWeek(weekNr);

        List<String> free = new ArrayList<>();
        for (Room r : rooms) {
            if (!weeks.get(r)[weekNr - 1]) {
                free.add(r.getRoomId());
            }
        }
        System.out.println("Available rooms for week " + weekNr + ": " + free);
    }

    public void showRoomAvailabilityPerYear(String roomId) {
        Room room = findRoom(roomId);
        if (room == null) {
            System.out.println("Room " + roomId + " not found.");
            return;
        }
        List<Integer> freeWeeks = new ArrayList<>();
        boolean[] cal = weeks.get(room);
        for (int w = 0; w < WEEKS_IN_YEAR; w++) {
            if (!cal[w]) freeWeeks.add(w + 1);
        }
        System.out.println("Room " + roomId + " – free weeks: " + freeWeeks);
    }


    public void readFromFile(String path) throws FileNotFoundException {
        Scanner sc = new Scanner(new File(path));
        sc.nextLine();                                   // skip header

        while (sc.hasNext()) try {
            String[] parts = sc.nextLine().split(",");
            String roomTag = parts[0];
            String client = parts[1].split(" ")[0];
            int week = Integer.parseInt(parts[2]);
            int dur = Integer.parseInt(parts[3]);

            if (roomTag.startsWith("h")) {
                bookHotelRoom(client, week, dur);
            } else if (roomTag.startsWith("c")) {
                int cap = Integer.parseInt(parts[4]);
                bookConferenceRoom(client, cap, week, dur);
            } else {
                System.out.println("Skipping unknown room tag: " + roomTag);
            }
        } catch (HotelManagerException ex) {
            System.err.println("Error while importing: " + ex.getMessage());
        }
    }

    public void showInvoiceForClient(String clientName) throws HotelManagerException {
        Client client = findClient(clientName);
        if (client == null)
            throw new HotelManagerException("Client " + clientName + " not found.");

        List<Booking> bks = new ArrayList<>();
        for (Booking b : bookings) {
            if (b.client.equals(client)) bks.add(b);
        }
        Collections.sort(bks);

        int subtotal = 0;
        for (Booking b : bks) {
            subtotal += b.room.getRatePerWeek() * b.getDuration();
        }

        int tax = 0;
        boolean addTax = !(client instanceof Company) || ((Company) client).isTaxable();
        if (addTax) tax = (int) Math.round(subtotal * 0.21);

        int total = subtotal + tax;

        System.out.println("Invoice for " + client);
        System.out.println("------------------------------------------------");

        for (Booking b : bks) {
            int cost = b.room.getRatePerWeek() * b.getDuration();
            System.out.printf("Room %-3s  week %2d  dur %2dw  €%,8.2f%n",
                    b.room.getRoomId(), b.getStartWeekNr(), b.getDuration(), cost / 100.0);
        }

        System.out.println("------------------------------------------------");
        System.out.printf("Subtotal\t€%,8.2f%n", subtotal / 100.0);
        if (addTax)
            System.out.printf("Tax 21%%\t€%,8.2f%n", tax / 100.0);
        System.out.printf("Total   \t€%,8.2f%n", total / 100.0);
    }


    private Room findAvailableHotelRoom(int startWeek, int duration) {
        for (Room r : rooms)
            if (r instanceof HotelRoom && isRoomFree(r, startWeek, duration))
                return r;
        return null;
    }

    private Room findAvailableConferenceRoom(int minCap, int startWeek, int duration) {
        for (Room r : rooms)
            if (r instanceof ConferenceRoom c && c.getCapacity() >= minCap && isRoomFree(r, startWeek, duration))
                return r;
        return null;
    }

    private void bookRoom(Client client, Room room, int startWeek, int duration) {
        boolean[] cal = weeks.get(room);
        for (int w = startWeek - 1; w < startWeek - 1 + duration; w++) cal[w] = true;
        bookings.add(new Booking(client, room, startWeek, duration));
    }

    private boolean isRoomFree(Room room, int startWeek, int duration) {
        boolean[] cal = weeks.get(room);
        for (int w = startWeek - 1; w < startWeek - 1 + duration; w++)
            if (cal[w]) return false;
        return true;
    }

    private void validateCompany(Client c) throws HotelManagerException {
        if (c == null) throw new HotelManagerException("Company not found.");
        if (!(c instanceof Company)) throw new HotelManagerException("Only companies can book conference rooms.");
    }

    private void validateWeek(int week) throws HotelManagerException {
        if (week < 1 || week > WEEKS_IN_YEAR)
            throw new HotelManagerException("Week number must be 1–52.");
    }

    private Client findClient(String name) {
        for (Client c : clients) if (c.getName().equals(name)) return c;
        return null;
    }

    private Room findRoom(String roomId) {
        for (Room r : rooms) if (r.getRoomId().equals(roomId)) return r;
        return null;
    }


    private void initializeRooms() {

        rooms.addAll(List.of(
                new HotelRoom("H1", false),
                new HotelRoom("H2", false),
                new HotelRoom("H3", false),
                new HotelRoom("H4", true),
                new HotelRoom("H5", true),
                new ConferenceRoom("C1", 50),
                new ConferenceRoom("C2", 100),
                new ConferenceRoom("C3", 150)
        ));

        for (Room r : rooms)
            weeks.put(r, new boolean[WEEKS_IN_YEAR]);
    }

    private void initializeClients() {
        clients = new Client[]{
                new Person("Anna", "Pothoven", "1985-9482-1342"),
                new Person("Velma", "Jansen", "2570-0309-6794"),
                new Person("Marina", "de Groot", "9112-6066-0910"),
                new Person("Velma", "Doe", "7902-7814-9680"),
                new Person("Frederik", "de Groot", "4989-2625-7287"),
                new Company("Thales", "3981-8741-0783", true),
                new Company("BBC", "4938-7986-9237", false),
                new Company("Saxion", "0877-4283-1642", false)
        };
    }
}
