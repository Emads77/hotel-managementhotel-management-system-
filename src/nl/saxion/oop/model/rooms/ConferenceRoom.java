package nl.saxion.oop.model.rooms;

public class ConferenceRoom extends Room {
   private int capacity;
    public ConferenceRoom(String roomId,int capacity) {
        super(roomId);
        this.capacity=capacity;
    }
    @Override
    public int getRatePerWeek() {
        return capacity * 25;
    }

    public int getCapacity() {
        return capacity;
    }
}
