package com.jyoti.bookingservice.flight;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Flight {
    private int flightId;
    private String departureCity;
    private String destinationCity;
    //format yyyy-mm-dd
    private String departureDate;
    private Double price;

    public Flight(){

    }
    public Flight(int flightId, String departureCity, String destinationCity, String departureDate, Double price) {
        this.flightId = flightId;
        this.departureCity = departureCity;
        this.destinationCity = destinationCity;
        this.departureDate = departureDate;
        this.price = price;
    }


    public int getFlightId() {
        return flightId;
    }

    public void setFlightId(int flightId) {
        this.flightId = flightId;
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

    public String getDepartureDate() {
        return departureDate;
    }

    public void setDepartureDate(String departureDate) {
        this.departureDate = departureDate;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }
}
