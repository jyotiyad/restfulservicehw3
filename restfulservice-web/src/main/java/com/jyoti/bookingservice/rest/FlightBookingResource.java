package com.jyoti.bookingservice.rest;

import com.jyoti.bookingservice.auth.AuthenticationException;
import com.jyoti.bookingservice.auth.AuthenticationService;
import com.jyoti.bookingservice.flight.*;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

@Path("/flightservice")
public class FlightBookingResource {

    private AuthenticationService authService;
    private FlightService flightService;

    public FlightBookingResource() {
        this.authService = AuthenticationService.getInstance();
        this.flightService = FlightService.getInstance();
    }


    @Path("/cancelFlight/{user_id}/{ticket_id}")
    @DELETE
    @Produces(MediaType.TEXT_PLAIN)
    public String cancel(@PathParam("user_id") String token,
                       @PathParam("ticket_id") String ticket_id)throws AuthenticationException, TicketNotFoundException {
        boolean tokenValid = authService.validateToken(token);
        if (!tokenValid) {
            throw new WebApplicationException(Response.Status.UNAUTHORIZED);
        }
        boolean ticketValid = flightService.tickExist(ticket_id);
        if(!ticketValid){
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        Map<String, Ticket> target = flightService.getBookedTicketsMap();
        target.remove(ticket_id);
        return "booking cancelled";
    }

    @Path("/getTicketInfo/{user_id}/{ticket_id}")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getTicket(@PathParam("user_id") String token,
                             @PathParam("ticket_id") String ticket_id)throws AuthenticationException, TicketNotFoundException{
        boolean tokenValid = authService.validateToken(token);
        if (!tokenValid) {
            throw new WebApplicationException(Response.Status.UNAUTHORIZED);
        }
        boolean ticketValid = flightService.tickExist(ticket_id);
        if(!ticketValid){
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        Map<String, Ticket> target = flightService.getBookedTicketsMap();
        Ticket t = target.get(ticket_id);
        return t.getTravellerName();
    }

    @Path("/changeInfo/{ticket_id}")
    @PUT
    @Produces(MediaType.TEXT_PLAIN)
    public String changeName(@FormParam("token") String token,
                             @FormParam("ticket_id") String ticket_id,
                             @FormParam("newVal") String new_val)throws AuthenticationException, TicketNotFoundException{
        boolean tokenValid = authService.validateToken(token);
        if (!tokenValid) {
            throw new WebApplicationException(Response.Status.UNAUTHORIZED);
        }
        boolean ticketValid = flightService.tickExist(ticket_id);
        if(!ticketValid){
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        Map<String, Ticket> target = flightService.getBookedTicketsMap();
        Ticket t = target.get(ticket_id);
        t.setTravellerName(new_val);
        return "change made";
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
    @Produces(MediaType.TEXT_PLAIN)
    public String bookTicket(@FormParam("token") String token,
                             @FormParam("card") String credit,
                             @FormParam("name") String fullName,
                             @FormParam("departureCity") String departureCity,
                             @FormParam("destinationCity") String destinationCity,
                             @FormParam("date") String date,
                             @FormParam("price") String price){

        boolean tokenValid = authService.validateToken(token);
        if (!tokenValid) {
            throw new WebApplicationException(Response.Status.UNAUTHORIZED);
        }

        if (credit == null || credit.length() < 0) {
            InvalidCardDetailsException invalidCardDetailsException = new InvalidCardDetailsException("credit card number should always be provided");
            throw new WebApplicationException(invalidCardDetailsException, Response.Status.BAD_REQUEST);
        }

        String ticket = null;
        BookingRequest br = new BookingRequest();
        br.setCreditCardNumber(credit);
        br.setToken(token);
        br.setTravellerFullName(fullName);
        ArrayList<Flight> list = new ArrayList<Flight>();
        list.add(new Flight(1, departureCity, destinationCity, date, Double.parseDouble(price)));
        Itinerary it = new Itinerary(departureCity, destinationCity, list);
        br.setItinerary(it);
        try {
            ticket = flightService.bookTicket(fullName,
                    credit, it);
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
