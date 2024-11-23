package net.robertx.planeteze_b07;

import java.util.HashMap;

public class YearlyFlightCarbonFootprintCalculator implements CalculateYearlyCarbonFootPrint {

    @Override
    public double calculateYearlyFootprint(HashMap<String, String> responses) {
        String shortHaulFlights = responses.get("How many short-haul flights have you taken in the past year?");
        String longHaulFlights = responses.get("How many long-haul flights have you taken in the past year?");
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
