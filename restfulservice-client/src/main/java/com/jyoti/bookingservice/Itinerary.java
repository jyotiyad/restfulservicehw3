package com.jyoti.bookingservice;

import java.util.List;

/**
 * Created by lpoon2 on 2/13/2017.
 */
public class Itinerary {
    private String departureCity;
    private String destinationCity;
    private List<Flight> flights;
    private Double totalPrice = 0d;

    public Itinerary() {
    }

    public Itinerary(String departureCity, String destinationCity, List<Flight> flights) {
        this.departureCity = departureCity;
        this.destinationCity = destinationCity;
        this.flights = flights;

        for (Flight flight : flights) {
            totalPrice = totalPrice + flight.getPrice();
        }
    }

    public String getDepartureCity() {
        return departureCity;
    }

    public void setDepartureCity(String departureCity) {
        this.departureCity = departureCity;
    }

    public String getDestinationCity() {
        return destinationCity;
    }

    public void setDestinationCity(String destinationCity) {
        this.destinationCity = destinationCity;
    }

    public List<Flight> getFlights() {
        return flights;
    }

    public void setFlights(List<Flight> flights) {
        this.flights = flights;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }
}
