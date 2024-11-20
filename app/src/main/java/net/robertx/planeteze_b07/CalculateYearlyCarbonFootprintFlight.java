package net.robertx.planeteze_b07;

import java.util.HashMap;

public class CalculateYearlyCarbonFootprintFlight {
    private String shortHaulFlights; // e.g., "None", "1-2 flights", "3-5 flights", etc.
    private String longHaulFlights; // e.g., "None", "1-2 flights", "3-5 flights", etc.

    public CalculateYearlyCarbonFootprintFlight(HashMap<String, String> responses) {
        // Extract values from the HashMap (assuming these are always answered correctly)
        this.shortHaulFlights = responses.get("How many short-haul flights have you taken in the past year?");
        this.longHaulFlights = responses.get("How many long-haul flights have you taken in the past year?");
    }

    public double calculateYearlyFootprint() {
        double shortHaulFootprint = calculateShortHaulFootprint(shortHaulFlights);
        double longHaulFootprint = calculateLongHaulFootprint(longHaulFlights);
        return shortHaulFootprint + longHaulFootprint;
    }

    private double calculateShortHaulFootprint(String flights) {
        // Calculate based on short-haul flight choices
        if (flights.equalsIgnoreCase("None")) {
            return 0;
        } else if (flights.equalsIgnoreCase("1-2 flights")) {
            return 225;
        } else if (flights.equalsIgnoreCase("3-5 flights")) {
            return 600;
        } else if (flights.equalsIgnoreCase("6-10 flights")) {
            return 1200;
        } else if (flights.equalsIgnoreCase("More than 10 flights")) {
            return 1800;
        }
        throw new IllegalArgumentException("Invalid short-haul flights input: " + flights);
    }

    private double calculateLongHaulFootprint(String flights) {
        // Calculate based on long-haul flight choices
        if (flights.equalsIgnoreCase("None")) {
            return 0;
        } else if (flights.equalsIgnoreCase("1-2 flights")) {
            return 825;
        } else if (flights.equalsIgnoreCase("3-5 flights")) {
            return 2200;
        } else if (flights.equalsIgnoreCase("6-10 flights")) {
            return 4400;
        } else if (flights.equalsIgnoreCase("More than 10 flights")) {
            return 6600;
        }
        throw new IllegalArgumentException("Invalid long-haul flights input: " + flights);
    }
}
