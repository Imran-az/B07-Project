package net.robertx.planeteze_b07.carbonFootprintCalculators;

import java.util.HashMap;

/**
 * Calculates the yearly carbon footprint for driving based on the car type and distance driven.
 */
public class YearlyDrivingCarbonFootprintCalculator extends CalculateYearlyCarbonFootPrint {


    /**
     * The type of car driven by the user (e.g., gasoline, diesel, hybrid, electric).
     */
    private String carType;

    /**
     * Required keys for the user responses map.
     * These keys are used to validate the input data.
     */
    private static final String[] requiredKeys = {
            "Do you own or regularly use a car?",
            "What type of car do you drive?",
            "How many kilometers/miles do you drive per year?"
    };

    /**
     * Parses the distance string from user input into a numeric value in kilometers.
     *
     * @param distanceStr The distance string (e.g., "Up to 5,000 km (3,000 miles)").
     * @return The parsed distance in kilometers or 0 for invalid input.
     */
    private double parseDistance(String distanceStr) {
        if (distanceStr == null || distanceStr.isEmpty()) {
            return 0;
        }

        if (distanceStr.equalsIgnoreCase("up to 5,000 km (3,000 miles)")) {
            return 5000;
        } else if (distanceStr.equalsIgnoreCase("5,000–10,000 km (3,000–6,000 miles)")) {
            return 10000;
        } else if (distanceStr.equalsIgnoreCase("10,000–15,000 km (6,000–9,000 miles)")) {
            return 15000;
        } else if (distanceStr.equalsIgnoreCase("15,000–20,000 km (9,000–12,000 miles)")) {
            return 20000;
        } else if (distanceStr.equalsIgnoreCase("20,000–25,000 km (12,000–15,000 miles)")) {
            return 25000;
        } else if (distanceStr.equalsIgnoreCase("more than 25,000 km (15,000 miles)")) {
            return 35000;
        } else {
            return 0;
        }
    }


    /**
     * Retrieves the emission factor for the given car type.
     *
     * @return The emission factor in kg CO2 per kilometer.
     */
    private double getEmissionFactor() {
        if (carType.equalsIgnoreCase("gasoline")) {
            return 0.24; // kg CO2 per km
        } else if (carType.equalsIgnoreCase("diesel")) {
            return 0.27; // kg CO2 per km
        } else if (carType.equalsIgnoreCase("hybrid")) {
            return 0.16; // kg CO2 per km
        } else if (carType.equalsIgnoreCase("electric")) {
            return 0.05; // kg CO2 per km
        } else {
            return 0.1; // Default emission factor
        }
    }


    /**
     * Calculates the yearly carbon footprint for driving.
     *
     * @param responses A map containing user responses to survey questions.
     *                  The map must contain keys such as:
     *                  - "Do you own or regularly use a car?"
     *                  - "What type of car do you drive?"
     *                  - "How many kilometers/miles do you drive per year?"
     * @return The calculated yearly carbon footprint in kg CO2. returns 0 for invalid responses.
     */
    @Override
    public double calculateYearlyFootprint(HashMap<String, String> responses) {
        if (!areResponsesValid(responses, requiredKeys)) {
            return 0.0;
        }

        String ownsCar = responses.get("Do you own or regularly use a car?");
        if (ownsCar.equalsIgnoreCase("No")) {
            return 0.0;
        }

        carType = responses.get("What type of car do you drive?");
        String distanceStr = responses.get("How many kilometers/miles do you drive per year?");

        double distanceDriven = parseDistance(distanceStr);
        double emissionFactor = getEmissionFactor();

        return emissionFactor * distanceDriven;
    }
}
