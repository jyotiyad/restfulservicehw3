package com.jyoti.bookingservice;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import java.util.ArrayList;

public class BookingServiceClient {
    private Client client;

    public BookingServiceClient() {
        this.client = Client.create();
    }

    public static void main(String[] args) {
        try {
            BookingServiceClient bookingServiceClient = new BookingServiceClient();

            String token = bookingServiceClient.login();
            bookingServiceClient.searchItinerary(token, "stockholm", "paris");
            bookingServiceClient.searchAvailableItinerary(token, "stockholm", "paris", "2017-01-01");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String login() {
        WebResource webResource = client
                .resource("http://localhost:8080/rest/flightservice/login");
        ArrayList<String> userName = new ArrayList<>();
        userName.add("test");
        ArrayList<String> password = new ArrayList<>();
        password.add("test");

        MultivaluedMap<String, String> map = new MultivaluedMapImpl();
        map.put("username", userName);
        map.put("password", password);

        ClientResponse response = webResource.type(MediaType.APPLICATION_FORM_URLENCODED_TYPE)
                .post(ClientResponse.class, map);

        if (response.getStatus() != 200) {
            throw new RuntimeException("Failed : HTTP error code : "
                    + response.getStatus());
        }

        String token = response.getEntity(String.class);

        System.out.println("Output from Server .... \n");
        System.out.println(token);
        return token;
    }

    private void searchItinerary(String token, String departureCity, String destinationCity) {
        String resource = "http://localhost:8080/rest/flightservice/search/";
        resource = resource + token + "/" + departureCity + "/" + destinationCity;
        WebResource webResource = client
                .resource(resource);

        ClientResponse response = webResource.type(MediaType.WILDCARD_TYPE)
                .get(ClientResponse.class);

        if (response.getStatus() != 200) {
            throw new RuntimeException("Failed : HTTP error code : "
                    + response.getStatus());
        }

        String output = response.getEntity(String.class);

        System.out.println("Output from Server .... \n");
        System.out.println(output);
    }

    private void searchAvailableItinerary(String token, String departureCity, String destinationCity, String date) {
        WebResource webResource = client
                .resource("http://localhost:8080/rest/flightservice/searchAvailableItinerary");
        ArrayList<String> tokenList = new ArrayList<>();
        tokenList.add(token);
        ArrayList<String> departureCityList = new ArrayList<>();
        departureCityList.add(departureCity);
        ArrayList<String> destinationCityList = new ArrayList<>();
        destinationCityList.add(destinationCity);
        ArrayList<String> dateList = new ArrayList<>();
        dateList.add(date);

        MultivaluedMap<String, String> map = new MultivaluedMapImpl();
        map.put("token", tokenList);
        map.put("destinationCity", destinationCityList);
        map.put("departureCity", departureCityList);
        map.put("date", dateList);

        ClientResponse response = webResource.type(MediaType.APPLICATION_FORM_URLENCODED_TYPE)
                .post(ClientResponse.class, map);

        if (response.getStatus() != 200) {
            throw new RuntimeException("Failed : HTTP error code : "
                    + response.getStatus());
        }

        String output = response.getEntity(String.class);

        System.out.println("Output from Server .... \n");
        System.out.println(output);

    }
}
