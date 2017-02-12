package com.jyoti.bookingservice.flight;

public class SeatNotAvailableException extends Exception{

    public SeatNotAvailableException(String message) {
        super(message);
    }
}
