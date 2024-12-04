package net.robertx.planeteze_b07.carbonFootprintCalculators;

import java.util.HashMap;

/**
 * Calculates the yearly carbon footprint from flights based on the number of short-haul
 * and long-haul flights taken in the past year.
 */
public class YearlyFlightCarbonFootprintCalculator extends CalculateYearlyCarbonFootPrint {
    /**
     * Required keys for the user responses map.
     * These keys are used to validate the input data.
     */
    private final String[] requiredKeys = {
            "How many short-haul flights (less than 1,500 km / 932 miles) have you taken in the past year?",
            "How many long-haul flights (more than 1,500 km / 932 miles) have you taken in the past year?"
    };

    /**
     * Calculates the yearly carbon footprint for flights based on user responses.
     *
     * @param responses A map containing user responses to flight-related questions.
     *                  The map must contain keys such as:
     *                  - "How many short-haul flights (less than 1,500 km / 932 miles) have you taken in the past year?"
     *                  - "How many long-haul flights (more than 1,500 km / 932 miles) have you taken in the past year?"
     * @return The total yearly carbon footprint from flights in kg CO2. Returns 0 for invalid responses.
     */
    @Override
    public double calculateYearlyFootprint(HashMap<String, String> responses) {
        if (!areResponsesValid(responses, requiredKeys)) {
            return 0.0;
        }

        String shortHaulFlights = responses.get("How many short-haul flights (less than 1,500 km / 932 miles) have you taken in the past year?");
        String longHaulFlights = responses.get("How many long-haul flights (more than 1,500 km / 932 miles) have you taken in the past year?");

        double shortHaulFootprint = calculateShortHaulFootprint(shortHaulFlights);
        double longHaulFootprint = calculateLongHaulFootprint(longHaulFlights);

        return shortHaulFootprint + longHaulFootprint;
    }

    /**
     * Calculates the carbon footprint for short-haul flights based on user input.
     *
     * @param flights The user-provided input for short-haul flights.
     *                Expected values are "None", "1-2 flights", "3-5 flights", "6-10 flights", or "More than 10 flights".
     * @return The carbon footprint in kg CO2 for short-haul flights.
     * @throws IllegalArgumentException if the input is null or invalid.
     */
    private double calculateShortHaulFootprint(String flights) {
        if (flights == null) {
            throw new IllegalArgumentException("Short-haul flights input cannot be null.");
        }

        if (flights.equalsIgnoreCase("None")) {
            return 0.0;
        } else if (flights.equalsIgnoreCase("1-2 flights")) {
            return 225.0;
        } else if (flights.equalsIgnoreCase("3-5 flights")) {
            return 600.0;
        } else if (flights.equalsIgnoreCase("6-10 flights")) {
            return 1200.0;
        } else if (flights.equalsIgnoreCase("More than 10 flights")) {
            return 1800.0;
        }
        throw new IllegalArgumentException("Invalid short-haul flights input: " + flights);
    }
    /**
     * Calculates the carbon footprint for long-haul flights based on user input.
     *
     * @param flights The user-provided input for long-haul flights.
     *                Expected values are "None", "1-2 flights", "3-5 flights", "6-10 flights", or "More than 10 flights".
     * @return The carbon footprint in kg CO2 for long-haul flights.
     * @throws IllegalArgumentException if the input is null or invalid.
     */
    private double calculateLongHaulFootprint(String flights) {
        if (flights == null) {
            throw new IllegalArgumentException("Long-haul flights input cannot be null.");
        }

        if (flights.equalsIgnoreCase("None")) {
            return 0.0;
        } else if (flights.equalsIgnoreCase("1-2 flights")) {
            return 825.0;
        } else if (flights.equalsIgnoreCase("3-5 flights")) {
            return 2200.0;
        } else if (flights.equalsIgnoreCase("6-10 flights")) {
            return 4400.0;
        } else if (flights.equalsIgnoreCase("More than 10 flights")) {
            return 6600.0;
        }
        throw new IllegalArgumentException("Invalid long-haul flights input: " + flights);
    }
}
