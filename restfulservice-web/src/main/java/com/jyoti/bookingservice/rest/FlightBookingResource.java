package com.jyoti.bookingservice.rest;

import com.jyoti.bookingservice.auth.AuthenticationException;
import com.jyoti.bookingservice.auth.AuthenticationService;
import com.jyoti.bookingservice.flight.*;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Set;

@Path("/flightservice")
public class FlightBookingResource {

    private AuthenticationService authService;
    private FlightService flightService;

    public FlightBookingResource() {
        this.authService = new AuthenticationService();
        this.flightService = new FlightService();
    }

    @Path("/login")
    @POST
    @Produces(MediaType.TEXT_PLAIN)
    public String login(@FormParam("username") String username,
                        @FormParam("password") String password) throws AuthenticationException {
        boolean isAuthenticated = authService.authenticateUser(username, password);
        if (isAuthenticated) {
            String token = authService.generateToken();
            return token;
        } else {
            throw new AuthenticationException("Invalid username and password");
        }
    }

    @Path("/search/{tk}/{depc}/{destc}")
    @GET
    public Set<Itinerary> searchItinerary(@PathParam("tk")String token,
                                          @PathParam("depc")String departureCity,
                                          @PathParam("destc")String destinationCity) throws AuthenticationException {
        boolean tokenValid = authService.validateToken(token);
        if (!tokenValid) {
            throw new AuthenticationException("Invalid Token");
        }

        return flightService.searchFlights(departureCity, destinationCity);
    }

    public Set<Itinerary> searchAvailableItinerary(String token,
                                                   String departureCity,
                                                   String destinationCity,
                                                   String date) throws AuthenticationException {
        boolean tokenValid = authService.validateToken(token);
        if (!tokenValid) {
            throw new AuthenticationException("Invalid Token");
        }

        return flightService.searchTicketAvailableFlights(departureCity, destinationCity, date);
    }

    public String bookTicket(String token,
                             String travellerFullName,
                             String creditCardNumber,
                             Itinerary itinerary)
            throws AuthenticationException, SeatNotAvailableException, InvalidCardDetailsException {
        boolean tokenValid = authService.validateToken(token);
        if (!tokenValid) {
            throw new AuthenticationException("Invalid Token");
        }

        if (creditCardNumber == null || creditCardNumber.length() < 0) {
            throw new InvalidCardDetailsException("credit card number should always be provided");
        }

        String ticket = flightService.bookTicket(travellerFullName,
                creditCardNumber, itinerary);

        return ticket;
    }

    public Ticket createTicket(String token,
                               String ticketNumber) throws AuthenticationException, TicketNotFoundException {
        boolean tokenValid = authService.validateToken(token);
        if (!tokenValid) {
            throw new AuthenticationException("Invalid Token");
        }

        Ticket ticket = flightService.getTicketDetails(ticketNumber);

        return ticket;
    }

}