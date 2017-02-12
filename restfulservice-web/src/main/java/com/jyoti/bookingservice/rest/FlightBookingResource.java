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
        this.authService = AuthenticationService.getInstance();
        this.flightService = FlightService.getInstance();
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

    @Path("/search/{token}/{departureCity}/{destinationCity}")
    @GET
    public Set<Itinerary> searchItinerary(@PathParam("token")String token,
                                          @PathParam("departureCity")String departureCity,
                                          @PathParam("destinationCity")String destinationCity) throws AuthenticationException {
        boolean tokenValid = authService.validateToken(token);
        if (!tokenValid) {
            throw new AuthenticationException("Invalid Token");
        }

        return flightService.searchFlights(departureCity, destinationCity);
    }
    @Path("/AvailableItinerary")
    @POST
    @Produces(MediaType.TEXT_PLAIN)
    public Set<Itinerary> searchAvailableItinerary(@FormParam("token")String token,
                                                   @FormParam("departureCity")String departureCity,
                                                   @FormParam("destinationCity")String destinationCity,
                                                   @FormParam("date")String date) throws AuthenticationException {
        boolean tokenValid = authService.validateToken(token);
        if (!tokenValid) {
            throw new AuthenticationException("Invalid Token");
        }

        return flightService.searchTicketAvailableFlights(departureCity, destinationCity, date);
    }

    /*@Path("/bookTicket")
    @POST
    @Produces(MediaType.TEXT_PLAIN)*/
    public String bookTicket(@FormParam("token")String token,
                             @FormParam("travellerFullName")String travellerFullName,
                             @FormParam("creditCardNumber")String creditCardNumber,
                             @FormParam("itinerary")Itinerary itinerary)
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
    @Path("/createTicket")
    @POST
    @Produces(MediaType.TEXT_PLAIN)
    public Ticket createTicket(@FormParam("token")String token,
                               @FormParam("ticketNumber")String ticketNumber) throws AuthenticationException, TicketNotFoundException {
        boolean tokenValid = authService.validateToken(token);
        if (!tokenValid) {
            throw new AuthenticationException("Invalid Token");
        }

        Ticket ticket = flightService.getTicketDetails(ticketNumber);

        return ticket;
    }

}
