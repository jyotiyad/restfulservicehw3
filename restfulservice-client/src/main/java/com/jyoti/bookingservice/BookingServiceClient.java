package com.jyoti.bookingservice;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import java.util.ArrayList;
import java.util.List;

public class BookingServiceClient {
    private Client client;
    private static BookingRequest br = new BookingRequest();

    public BookingServiceClient() {
        this.client = Client.create();
    }

    public static void main(String[] args) {
        try {
            BookingServiceClient bookingServiceClient = new BookingServiceClient();
            System.out.println("-----------------Test for POST---------------------\n");
            String token = bookingServiceClient.login();
            bookingServiceClient.searchItinerary(token, "stockholm", "paris");
            bookingServiceClient.searchAvailableItinerary(token, "stockholm", "paris", "2017-01-01");
            br.setCreditCardNumber("12345678");
            ArrayList<Flight> list = new ArrayList<Flight>();
            list.add(new Flight(1, "stockholm", "paris", "2017-01-01", 1111.0));
            Itinerary it = new Itinerary("stockholm", "paris", list);
            br.setItinerary(it);
            String ticket = bookingServiceClient.bookTicket(br);
            bookingServiceClient.createTicket(token , ticket);
            System.out.println("-----------------Test for PUT and GET---------------------\n");

            System.out.println("\nName before :" + bookingServiceClient.getNameonicket(token, ticket));
            bookingServiceClient.changeTicketName(token, ticket, "Larry Poon");
            System.out.println("Name after :" + bookingServiceClient.getNameonicket(token, ticket));
            System.out.println("-----------------Test for DELETE---------------------\n");
            bookingServiceClient.cancel(token, ticket);
            System.out.println("-----------------Test for GET after DELETE (status 404 expected)---------------------\n");
            System.out.println("\nName before :" + bookingServiceClient.getNameonicket(token, ticket));


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
        br.setTravellerFullName("test test");

        ClientResponse response = webResource.type(MediaType.APPLICATION_FORM_URLENCODED_TYPE)
                .post(ClientResponse.class, map);

        if (response.getStatus() != 200) {
            throw new RuntimeException("Failed : HTTP error code : "
                    + response.getStatus());
        }

        String token = response.getEntity(String.class);

        System.out.println("Output from Server .... \n");
        System.out.println(token);
        br.setToken(token);

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
    private String bookTicket(BookingRequest req){
        WebResource webResource = client
                .resource("http://localhost:8080/rest/flightservice/bookTicket");
        ArrayList<String> tokenList = new ArrayList<>();
        tokenList.add(req.getToken());
        ArrayList<String> departureCityList = new ArrayList<>();
        departureCityList.add(req.getItinerary().getDepartureCity());
        ArrayList<String> destinationCityList = new ArrayList<>();
        destinationCityList.add(req.getItinerary().getDestinationCity());
        ArrayList<String> priceList = new ArrayList<>();
        priceList.add(req.getItinerary().getTotalPrice().toString());
        ArrayList<String> dateList = new ArrayList<>();
        dateList.add("2017-01-01");
        ArrayList<String> nameList = new ArrayList<>();
        nameList.add(req.getTravellerFullName());
        ArrayList<String> cardList = new ArrayList<>();
        cardList.add(req.getCreditCardNumber());

        MultivaluedMap<String, String> map = new MultivaluedMapImpl();
        map.put("token", tokenList);
        map.put("card", cardList);
        map.put("name", nameList);
        map.put("departureCity", departureCityList);
        map.put("destinationCity", destinationCityList);
        map.put("date", dateList);
        map.put("price", priceList);

        ClientResponse response = webResource.type(MediaType.APPLICATION_FORM_URLENCODED_TYPE)
                .post(ClientResponse.class, map);
        if (response.getStatus() != 200) {
            throw new RuntimeException("Failed : HTTP error code : "
                    + response.getStatus());
        }

        String output = response.getEntity(String.class);

        System.out.println("Output from Server .... \n");
        System.out.println(output);

        return output;
    }
    private String getNameonicket(String token, String ticket_num){
        WebResource webResource = client
                .resource("http://localhost:8080/rest/flightservice/getTicketInfo/"+ token +"/" + ticket_num);
        ArrayList<String> tokenList = new ArrayList<>();
        tokenList.add(token);
        ArrayList<String> ticketList = new ArrayList<>();
        ticketList.add(ticket_num);
        MultivaluedMap<String, String> map = new MultivaluedMapImpl();
        map.put("token", tokenList);
        map.put("ticket_id", ticketList);
        ClientResponse response = webResource.type(MediaType.APPLICATION_FORM_URLENCODED_TYPE)
                .get(ClientResponse.class);
        if (response.getStatus() != 200) {
            throw new RuntimeException("Failed : HTTP error code : "
                    + response.getStatus());
        }

        String output = response.getEntity(String.class);
        return output;

    }

    private void changeTicketName(String token, String ticket_num, String new_Val){
        WebResource webResource = client
                .resource("http://localhost:8080/rest/flightservice/changeInfo/"+ticket_num);
        ArrayList<String> tokenList = new ArrayList<>();
        tokenList.add(token);
        ArrayList<String> ticketList = new ArrayList<>();
        ticketList.add(ticket_num);
        ArrayList<String> newList = new ArrayList<>();
        newList.add(new_Val);
        MultivaluedMap<String, String> map = new MultivaluedMapImpl();
        map.put("token", tokenList);
        map.put("ticket_id", ticketList);
        map.put("newVal", newList);
        ClientResponse response = webResource.type(MediaType.APPLICATION_FORM_URLENCODED_TYPE)
                .put(ClientResponse.class, map);
        if (response.getStatus() != 200) {
            throw new RuntimeException("Failed : HTTP error code : "
                    + response.getStatus());
        }

        String output = response.getEntity(String.class);

        System.out.println("Output from Server .... \n");
        System.out.println(output);

    }

    private void createTicket(String token , String ticket_num){
        WebResource webResource = client
                .resource("http://localhost:8080/rest/flightservice/createTicket/");
        ArrayList<String> tokenList = new ArrayList<>();
        tokenList.add(token);
        ArrayList<String> ticketList = new ArrayList<>();
        ticketList.add(ticket_num);
        MultivaluedMap<String, String> map = new MultivaluedMapImpl();
        map.put("token", tokenList);
        map.put("ticketNumber", ticketList);

        ClientResponse response = webResource.type(MediaType.APPLICATION_FORM_URLENCODED_TYPE)
                .post(ClientResponse.class, map);

        if (response.getStatus() != 200) {
            throw new RuntimeException("Failed : HTTP error code : "
                    + response.getStatus());
        }

        String output = response.getEntity(String.class);

        System.out.println("Output from Server .... \n ");
        System.out.println(output);
    }
    private void cancel(String token , String ticket){
        WebResource webResource = client
                .resource("http://localhost:8080/rest/flightservice/cancelFlight/" + token + "/" + ticket);

        ClientResponse response = webResource.type(MediaType.APPLICATION_FORM_URLENCODED_TYPE)
                .delete(ClientResponse.class);

        if (response.getStatus() != 200) {
            throw new RuntimeException("Failed : HTTP error code : "
                    + response.getStatus());
        }

        String output = response.getEntity(String.class);

        System.out.println("Output from Server .... \n ");
        System.out.println(output);
    }
}
