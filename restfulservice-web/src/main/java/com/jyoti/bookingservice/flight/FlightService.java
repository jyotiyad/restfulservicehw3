package com.jyoti.bookingservice.flight;

import java.util.*;

public class FlightService {

    private Set<Flight> flights;
    private Map<Integer, FlightInventory> flightInventoryMap;
    private String[] source = {"stockholm", "malmo", "copenhagen", "paris", "berlin", "paris"};
    private String[] destination = {"paris", "copenhagen", "malmo", "stockholm", "goteborg", "berlin"};

    private Map<String, Ticket> bookedTicketsMap;
    public Map<Integer, FlightInventory> getflightInventoryMap(){
        return this.flightInventoryMap;
    };
    public Map<String, Ticket> getBookedTicketsMap(){
        return this.bookedTicketsMap;
    }

    public FlightService() {
        flights = new HashSet<>();
        flightInventoryMap = new HashMap<>();
        bookedTicketsMap = new HashMap<>();
        int i = 0;
        Flight flight1 = new Flight(i, source[i], destination[i], "2017-01-01", 1111d);
        ++i;
        Flight flight2 = new Flight(i, source[i], destination[i], "2017-02-01", 2222d);
        ++i;
        Flight flight3 = new Flight(i, source[i], destination[i], "2017-03-01", 3333d);
        ++i;
        Flight flight4 = new Flight(i, source[i], destination[i], "2017-04-01", 4444d);
        ++i;
        Flight flight5 = new Flight(i, source[i], destination[i], "2017-05-01", 5555d);
        ++i;
        Flight flight6 = new Flight(i, source[i], destination[i], "2017-01-01", 5555d);

        flights.add(flight1);
        flights.add(flight2);
        flights.add(flight3);
        flights.add(flight4);
        flights.add(flight5);
        flights.add(flight6);

        flightInventoryMap.put(flight1.getFlightId(), new FlightInventory(flight1.getFlightId(), 10));
        flightInventoryMap.put(flight2.getFlightId(), new FlightInventory(flight2.getFlightId(), 10));
        flightInventoryMap.put(flight3.getFlightId(), new FlightInventory(flight3.getFlightId(), 10));
        flightInventoryMap.put(flight4.getFlightId(), new FlightInventory(flight4.getFlightId(), 10));
        flightInventoryMap.put(flight5.getFlightId(), new FlightInventory(flight5.getFlightId(), 10));
    }

    public Set<Itinerary> searchFlights(String departureCity, String destinationCity) {
        Set<Itinerary> itinerarySearchResult = new HashSet<>();
        for (Flight flight : flights) {
            if (flight.getDepartureCity().equals(departureCity)
                    && flight.getDestinationCity().equals(destinationCity)) {
                List<Flight> flightSearchResult = new ArrayList<>();
                flightSearchResult.add(flight);
                Itinerary itinerary = new Itinerary(departureCity, destinationCity, flightSearchResult);
                itinerarySearchResult.add(itinerary);
            }
        }

        //if no direct flight found then try combination flight
        if (itinerarySearchResult.isEmpty()) {
            for (Flight flight1 : flights) {
                if (flight1.getDepartureCity().equals(departureCity)) {
                    for (Flight flight2 : flights) {
                        if (flight2.getDestinationCity().equals(destinationCity)
                                && flight2.getDepartureCity().equals(flight1.getDestinationCity())) {
                            List<Flight> flightSearchResult = new ArrayList<>();
                            flightSearchResult.add(flight1);
                            flightSearchResult.add(flight2);
                            Itinerary itinerary = new Itinerary(departureCity, destinationCity, flightSearchResult);
                            itinerarySearchResult.add(itinerary);
                        }
                    }
                }
            }

        }

        return itinerarySearchResult;
    }

    public Set<Itinerary> searchTicketAvailableFlights(String departureCity, String destinationCity, String date) {
        Set<Itinerary> itinerarySearchResult = new HashSet<>();
        Set<Itinerary> flightResult = searchFlights(departureCity, destinationCity);
        //filter on date and availability
        for (Itinerary itinerary : flightResult) {
            List<Flight> flights = itinerary.getFlights();
            //match departure date with first flight
            if (flights.get(0).getDepartureDate().equals(date)) {
                //check seat availability in all flights of itinerary
                boolean available = true;
                for (Flight flight : flights) {
                    int flightId = flight.getFlightId();
                    FlightInventory flightInventory = flightInventoryMap.get(flightId);
                    if (!flightInventory.isSeatAvailable()) {
                        available = false;
                        break;
                    }
                }

                if (available) {
                    itinerarySearchResult.add(itinerary);
                }
            }
        }

        return itinerarySearchResult;
    }

    public String bookTicket(String travellerFullName, String creditCardNumber, Itinerary itinerary) throws SeatNotAvailableException {
        List<Flight> flights = itinerary.getFlights();
        //check for seat availability
        for (Flight flight : flights) {
            int flightId = flight.getFlightId();
            FlightInventory flightInventory = flightInventoryMap.get(flightId);

            if (!flightInventory.isSeatAvailable()) {
                throw new SeatNotAvailableException("Flight=" + flightId + " is fully booked. Not available");
            }
        }

        //now updateInventory
        for (Flight flight : flights) {
            int flightId = flight.getFlightId();
            FlightInventory flightInventory = flightInventoryMap.get(flightId);
            flightInventory.bookSeat();
        }

        //now create ticket
        UUID uuid = UUID.randomUUID();
        Ticket t = new Ticket();
        String ticketNumber = uuid.toString();
        t.setTicketNumber(ticketNumber);
        t.setTravellerName(travellerFullName);
        t.setTotalPrice(itinerary.getTotalPrice());
        t.setItinerary(itinerary);

        //add it to map for future lookup
        bookedTicketsMap.put(ticketNumber, t);
        return ticketNumber;
    }


    public Ticket getTicketDetails(String ticketNumber) throws TicketNotFoundException {
        Ticket s = bookedTicketsMap.get(ticketNumber);
        if (s == null) {
            throw new TicketNotFoundException("Ticket with ticketnumber " + ticketNumber + " not found");
        }
        return s;
    }


}
