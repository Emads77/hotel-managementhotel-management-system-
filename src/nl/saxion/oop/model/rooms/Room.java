package nl.saxion.oop.model.rooms;

public abstract class Room {
    private String roomId;

    public Room(String roomId) {
        this.roomId = roomId;
    }
    public abstract int getRatePerWeek();


    public String getRoomId() {
        return roomId;
    }


    @Override
    public String toString() {
        return roomId;
    }
}
