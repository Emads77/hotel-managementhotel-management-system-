package nl.saxion.oop.model.rooms;

public class HotelRoom extends Room {

    private final boolean hasBath;

    public HotelRoom(String roomId, boolean hasBath) {
        super(roomId);
        this.hasBath = hasBath;
    }
    @Override
    public int getRatePerWeek() {
        if (hasBath) {
            return 12500;
        } else {
            return 10000;
        }
    }

    @Override
    public String toString() {
        return super.toString() + (hasBath ? " with bath" : " without bath");
    }
}
