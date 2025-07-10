package com.example.bankfake.service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;

@Service
public class GeoNamesService {

    private static final String GEONAMES_URL =
        "http://api.geonames.org/searchJSON?country=EG&featureClass=P&lang=ar&maxRows=500&username=";

    // Simple inner class for city name + coords
    private static class City {
        public final String name;
        public final double lat;
        public final double lng;
        public City(String name, double lat, double lng) {
            this.name = name;
            this.lat  = lat;
            this.lng  = lng;
        }
    }

    private final String username;
    private final List<City> cities = new ArrayList<>();
    private final Random rnd = new Random();

    public GeoNamesService(@Value("${geonames.username}") String username) {
        this.username = username;
    }

    @PostConstruct
    public void init() {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(GEONAMES_URL + username))
                .build();
            HttpResponse<String> resp = client.send(
                req,
                HttpResponse.BodyHandlers.ofString()
            );
            JsonNode root = new ObjectMapper().readTree(resp.body());
            JsonNode arr  = root.get("geonames");
            if (arr != null && arr.isArray()) {
                for (JsonNode node : arr) {
                    String name = node.get("name").asText();
                    double lat  = node.get("lat").asDouble();
                    double lng  = node.get("lng").asDouble();
                    cities.add(new City(name, lat, lng));
                }
            }
        } catch (Exception e) {
            System.err.println("GeoNames init failed: " + e.getMessage());
        }
    }

    /** Pick a random city (Arabic name + coords), falling back to a small static list if needed. */
    private City randomCity() {
        if (!cities.isEmpty()) {
            return cities.get(rnd.nextInt(cities.size()));
        }
        // fallback: several major Egyptian cities
        List<City> fallback = List.of(
            new City("القاهرة", 30.0444, 31.2357),
            new City("الإسكندرية", 31.2001, 29.9187),
            new City("الجيزة", 30.0131, 31.2089),
            new City("شرم الشيخ", 27.9158, 34.3299),
            new City("الأقصر", 25.6872, 32.6396),
            new City("أسوان", 24.0889, 32.8998)
        );
        return fallback.get(rnd.nextInt(fallback.size()));
    }

    /**
     * Try GeoNames’s findNearbyStreet; fallback to Faker’s Arabic street name.
     */
    public String randomStreet() {
        City c = randomCity();
        String url = String.format(
            "http://api.geonames.org/findNearbyStreetJSON?lat=%.6f&lng=%.6f&username=%s",
            c.lat, c.lng, username
        );
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> res = client.send(
                HttpRequest.newBuilder().uri(URI.create(url)).build(),
                HttpResponse.BodyHandlers.ofString()
            );
            JsonNode segs = new ObjectMapper()
                .readTree(res.body())
                .get("streetSegments");
            if (segs != null && segs.isArray() && segs.size() > 0) {
                JsonNode pick = segs.get(rnd.nextInt(segs.size()));
                return pick.get("name").asText();
            }
        } catch (Exception ignored) { }
        // fallback
        return new Faker(new Locale("ar"))
            .address().streetName();
    }

    /** “street، city” */
    public String randomAddress() {
        City c = randomCity();
        return randomStreet() + "، " + c.name;
    }
}
