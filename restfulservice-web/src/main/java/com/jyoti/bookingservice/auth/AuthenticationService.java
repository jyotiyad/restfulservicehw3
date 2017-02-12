package com.jyoti.bookingservice.auth;

import java.util.*;

public class AuthenticationService {

    private Map<String, String> userMap;
    private Random random;

    private Set<String> tokens;

    public AuthenticationService() {
        this.userMap = new HashMap<>();
        //add fix set up users with username as key and password as value
        userMap.put("jyoti", "jyoti123");
        userMap.put("larry", "larry");
        userMap.put("test", "test");
        this.random = new Random();
        this.tokens = new HashSet<>();
    }

    public boolean authenticateUser(String username, String password) {
        String s = userMap.get(username);
        if (s == null) {
            return false;
        }
        return s.equals(password);
    }

    public String generateToken() {
        int randomNumber = random.nextInt();
        //return  String.valueOf(randomNumber);
        String token = "TOKEN-" + randomNumber;
        tokens.add(token);

        return token;
    }

    public boolean validateToken(String token) {
        return tokens.contains(token);
    }

}
