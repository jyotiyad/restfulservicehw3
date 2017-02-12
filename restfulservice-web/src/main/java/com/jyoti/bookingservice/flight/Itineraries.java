package com.jyoti.bookingservice.flight;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.HashSet;
import java.util.Set;

@XmlRootElement
public class Itineraries {

    private Set<Itinerary> itinerary = new HashSet<>();

    public Itineraries() {
    }

    public Itineraries(Set<Itinerary> itinerary) {
        this.itinerary.addAll(itinerary);
    }

    public void setItinerary(Set<Itinerary> itinerary) {
        this.itinerary = itinerary;
    }

    public Set<Itinerary> getItinerary() {
        return itinerary;
    }
}
