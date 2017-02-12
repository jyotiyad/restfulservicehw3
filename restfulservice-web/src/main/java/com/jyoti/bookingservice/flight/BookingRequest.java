package com.jyoti.bookingservice.flight;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class BookingRequest {

    private String token;
    private String travellerFullName;
    private String creditCardNumber;
    private Itinerary itinerary;

    public Itinerary getItinerary() {
        return itinerary;
    }

    public void setItinerary(Itinerary itinerary) {
        this.itinerary = itinerary;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTravellerFullName() {
        return travellerFullName;
    }

    public void setTravellerFullName(String travellerFullName) {
        this.travellerFullName = travellerFullName;
    }

    public String getCreditCardNumber() {
        return creditCardNumber;
    }

    public void setCreditCardNumber(String creditCardNumber) {
        this.creditCardNumber = creditCardNumber;
    }
}
