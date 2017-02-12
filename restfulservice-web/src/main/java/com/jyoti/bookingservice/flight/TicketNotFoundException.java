package com.jyoti.bookingservice.flight;

public class TicketNotFoundException extends Exception {
    public TicketNotFoundException(String message) {
        super(message);
    }
}
