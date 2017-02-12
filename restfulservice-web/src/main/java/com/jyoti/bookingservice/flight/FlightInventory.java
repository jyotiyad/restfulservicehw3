package com.jyoti.bookingservice.flight;

public class FlightInventory {
    private int flightId;
    private int totalSeats;
    private int bookedSeats;

    public FlightInventory(int flightId, int totalSeats) {
        this.flightId = flightId;
        this.totalSeats = totalSeats;
    }

    public boolean isSeatAvailable() {
        return totalSeats - bookedSeats > 0;
    }

    public void bookSeat() {
        bookedSeats++;
    }
}
