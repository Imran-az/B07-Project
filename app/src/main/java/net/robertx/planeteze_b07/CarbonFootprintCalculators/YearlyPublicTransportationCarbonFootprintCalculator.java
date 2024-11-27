package net.robertx.planeteze_b07.CarbonFootprintCalculators;

import java.util.HashMap;

public class YearlyPublicTransportationCarbonFootprintCalculator {
    private String frequency; // e.g., "Never", "Occasionally", "Frequently", "Always"
    private String hoursPerWeek; // e.g., "Under 1 hour", "1-3 hours", "3-5 hours", etc.


    public double calculateYearlyFootprint(HashMap <String, String> responses) {
        // Extract values from the HashMap
        this.frequency = responses.get("How often do you use public transportation (bus, train, subway)?");
        this.hoursPerWeek = responses.get("How much time do you spend on public transport per week (bus, train, subway)?");

        return calculateFootprint(frequency, hoursPerWeek);
    }

    private double calculateFootprint(String frequency, String hours) {
        if (frequency.equalsIgnoreCase("Never")) {
            return 0;
        }
        // Based on frequency and hours, return the corresponding value
        if (frequency.equalsIgnoreCase("Occasionally")) {
            if (hours.equalsIgnoreCase("Under 1 hour")) {
                return 246;
            } else if (hours.equalsIgnoreCase("1-3 hours")) {
                return 819;
            } else if (hours.equalsIgnoreCase("3-5 hours")) {
                return 1638;
            } else if (hours.equalsIgnoreCase("5-10 hours")) {
                return 3071;
            } else if (hours.equalsIgnoreCase("More than 10 hours")) {
                return 4095;
            }
        } else if (frequency.equalsIgnoreCase("Frequently")) {
            if (hours.equalsIgnoreCase("Under 1 hour")) {
                return 573;
            } else if (hours.equalsIgnoreCase("1-3 hours")) {
                return 1911;
            } else if (hours.equalsIgnoreCase("3-5 hours")) {
                return 3822;
            } else if (hours.equalsIgnoreCase("5-10 hours")) {
                return 7166;
            } else if (hours.equalsIgnoreCase("More than 10 hours")) {
                return 9555;
            }
        } else if (frequency.equalsIgnoreCase("Always")) {
            if (hours.equalsIgnoreCase("Under 1 hour")) {
                return 573;
            } else if (hours.equalsIgnoreCase("1-3 hours")) {
                return 1911;
            } else if (hours.equalsIgnoreCase("3-5 hours")) {
                return 3822;
            } else if (hours.equalsIgnoreCase("5-10 hours")) {
                return 7166;
            } else if (hours.equalsIgnoreCase("More than 10 hours")) {
                return 9555;
            }
        }

        // Default for invalid inputs
        throw new IllegalArgumentException("Invalid frequency or hours input: " + frequency + ", " + hours);
    }
}
