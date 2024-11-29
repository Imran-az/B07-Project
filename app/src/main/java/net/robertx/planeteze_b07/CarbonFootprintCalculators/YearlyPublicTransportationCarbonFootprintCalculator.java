package net.robertx.planeteze_b07.CarbonFootprintCalculators;

import android.util.Log;

import java.util.HashMap;

/**
 * Calculates the yearly carbon footprint for public transportation usage based on the frequency
 * of usage and the hours spent on public transport per week.
 */
public class YearlyPublicTransportationCarbonFootprintCalculator extends CalculateYearlyCarbonFootPrint {

    private static final String[] requiredKeys = {
            "How often do you use public transportation (bus, train, subway)?",
            "How much time do you spend on public transport per week (bus, train, subway)?"
    };

    private String frequency; // e.g., "Never", "Occasionally", "Frequently", "Always"
    private String hoursPerWeek; // e.g., "Under 1 hour", "1-3 hours", "3-5 hours", etc.

    private static final String TAG = "YearlyPublicTransportationCarbonFootprintCalculator";

    /**
     * Calculates the yearly carbon footprint for public transportation.
     *
     * @param responses A map containing user responses to public transportation-related questions.
     *                  The map must contain keys such as:
     *                  - "How often do you use public transportation (bus, train, subway)?"
     *                  - "How much time do you spend on public transport per week (bus, train, subway)?"
     * @return The total yearly carbon footprint in kg CO2. Returns 0 for invalid responses.
     */
    @Override
    public double calculateYearlyFootprint(HashMap<String, String> responses) {
        // Validate the responses
        if (!areResponsesValid(responses, requiredKeys)) {
            Log.d(TAG, "Invalid or missing responses Public Transportation Footprint");
            return 0.0;
        }

        // Extract values from the HashMap
        this.frequency = responses.get("How often do you use public transportation (bus, train, subway)?");
        this.hoursPerWeek = responses.get("How much time do you spend on public transport per week (bus, train, subway)?");

        // Calculate and return the carbon footprint
        return calculateFootprint(frequency, hoursPerWeek);
    }

    /**
     * Calculates the carbon footprint based on the frequency of public transportation usage
     * and the hours spent on public transportation per week.
     *
     * @param frequency The frequency of public transportation usage (e.g., "Never", "Occasionally").
     * @param hours     The hours spent on public transportation per week.
     * @return The carbon footprint in kg CO2.
     */
    private double calculateFootprint(String frequency, String hours) {
        if (frequency.equalsIgnoreCase("Never")) {
            return 0.0;
        }
        // Carbon footprint values based on frequency and hours
        if (frequency.equalsIgnoreCase("Occasionally (1-2 times/week)")) {
            return calculateOccasionalFootprint(hours);
        } else if (frequency.equalsIgnoreCase("Frequently (3-4 times/week)")) {
            return calculateFrequentFootprint(hours);
        } else if (frequency.equalsIgnoreCase("Always (5+ times/week)")) {
            return calculateAlwaysFootprint(hours);
        }

        // Default for invalid inputs
        throw new IllegalArgumentException("Invalid frequency or hours input: " + frequency + ", " + hours);
    }

    /**
     * Calculates the carbon footprint for "Occasionally" frequency.
     *
     * @param hours The hours spent on public transportation per week.
     * @return The carbon footprint in kg CO2.
     */
    private double calculateOccasionalFootprint(String hours) {
        if (hours.equalsIgnoreCase("Under 1 hour")) {
            return 246.0;
        } else if (hours.equalsIgnoreCase("1-3 hours")) {
            return 819.0;
        } else if (hours.equalsIgnoreCase("3-5 hours")) {
            return 1638.0;
        } else if (hours.equalsIgnoreCase("5-10 hours")) {
            return 3071.0;
        } else if (hours.equalsIgnoreCase("More than 10 hours")) {
            return 4095.0;
        }
        throw new IllegalArgumentException("Invalid hours input for 'Occasionally': " + hours);
    }

    /**
     * Calculates the carbon footprint for "Frequently" frequency.
     *
     * @param hours The hours spent on public transportation per week.
     * @return The carbon footprint in kg CO2.
     */
    private double calculateFrequentFootprint(String hours) {
        if (hours.equalsIgnoreCase("Under 1 hour")) {
            return 573.0;
        } else if (hours.equalsIgnoreCase("1-3 hours")) {
            return 1911.0;
        } else if (hours.equalsIgnoreCase("3-5 hours")) {
            return 3822.0;
        } else if (hours.equalsIgnoreCase("5-10 hours")) {
            return 7166.0;
        } else if (hours.equalsIgnoreCase("More than 10 hours")) {
            return 9555.0;
        }
        throw new IllegalArgumentException("Invalid hours input for 'Frequently': " + hours);
    }

    /**
     * Calculates the carbon footprint for "Always" frequency.
     *
     * @param hours The hours spent on public transportation per week.
     * @return The carbon footprint in kg CO2.
     */
    private double calculateAlwaysFootprint(String hours) {
        if (hours.equalsIgnoreCase("Under 1 hour")) {
            return 573.0;
        } else if (hours.equalsIgnoreCase("1-3 hours")) {
            return 1911.0;
        } else if (hours.equalsIgnoreCase("3-5 hours")) {
            return 3822.0;
        } else if (hours.equalsIgnoreCase("5-10 hours")) {
            return 7166.0;
        } else if (hours.equalsIgnoreCase("More than 10 hours")) {
            return 9555.0;
        }
        throw new IllegalArgumentException("Invalid hours input for 'Always': " + hours);
    }
}
