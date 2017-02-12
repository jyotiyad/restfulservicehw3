package com.jyoti.bookingservice.rest;

import com.jyoti.bookingservice.auth.AuthenticationException;
import com.jyoti.bookingservice.auth.AuthenticationService;
import com.jyoti.bookingservice.flight.*;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
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
                        @FormParam("password") String password) {
        boolean isAuthenticated = authService.authenticateUser(username, password);
        if (isAuthenticated) {
            String token = authService.generateToken();
            return token;
        } else {
            throw new WebApplicationException(Response.Status.UNAUTHORIZED);
        }
    }

    @Path("/search/{token}/{departureCity}/{destinationCity}")
    @GET
    @Produces(MediaType.APPLICATION_XML)
    public Itineraries searchItinerary(@PathParam("token") String token,
                                       @PathParam("departureCity") String departureCity,
                                       @PathParam("destinationCity") String destinationCity) {
        boolean tokenValid = authService.validateToken(token);
        if (!tokenValid) {
            throw new WebApplicationException(Response.Status.UNAUTHORIZED);
        }

        Set<Itinerary> itineraries = flightService.searchFlights(departureCity, destinationCity);
        return new Itineraries(itineraries);
    }

    @Path("/searchAvailableItinerary")
    @POST
    @Produces(MediaType.APPLICATION_XML)
    public Itineraries searchAvailableItinerary(@FormParam("token") String token,
                                                @FormParam("departureCity") String departureCity,
                                                @FormParam("destinationCity") String destinationCity,
                                                @FormParam("date") String date) {
        boolean tokenValid = authService.validateToken(token);
        if (!tokenValid) {
            throw new WebApplicationException(Response.Status.UNAUTHORIZED);
        }

        Set<Itinerary> itineraries = flightService.searchTicketAvailableFlights(departureCity, destinationCity, date);
        return new Itineraries(itineraries);
    }

    @Path("/bookTicket")
    @POST
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.TEXT_PLAIN)
    public String bookTicket(BookingRequest bookingRequest){
        boolean tokenValid = authService.validateToken(bookingRequest.getToken());
        if (!tokenValid) {
            throw new WebApplicationException(Response.Status.UNAUTHORIZED);
        }

        String creditCardNumber = bookingRequest.getCreditCardNumber();
        if (creditCardNumber == null || creditCardNumber.length() < 0) {
            InvalidCardDetailsException invalidCardDetailsException = new InvalidCardDetailsException("credit card number should always be provided");
            throw new WebApplicationException(invalidCardDetailsException, Response.Status.BAD_REQUEST);
        }

        String ticket = null;
        try {
            ticket = flightService.bookTicket(bookingRequest.getTravellerFullName(),
                    creditCardNumber, bookingRequest.getItinerary());
        } catch (SeatNotAvailableException e) {
            throw new WebApplicationException(e);
        }

        return ticket;
    }

    @Path("/createTicket")
    @POST
    @Produces(MediaType.APPLICATION_XML)
    public Ticket createTicket(@FormParam("token") String token,
                               @FormParam("ticketNumber") String ticketNumber) throws AuthenticationException, TicketNotFoundException {
        boolean tokenValid = authService.validateToken(token);
        if (!tokenValid) {
            throw new WebApplicationException(Response.Status.UNAUTHORIZED);
        }

        Ticket ticket = flightService.getTicketDetails(ticketNumber);

        return ticket;
    }

}
