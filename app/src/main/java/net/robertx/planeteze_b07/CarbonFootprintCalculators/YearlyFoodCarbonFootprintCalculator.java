package net.robertx.planeteze_b07.CarbonFootprintCalculators;

import android.util.Log;

import java.util.HashMap;

/**
 * Calculates the yearly carbon footprint for food consumption based on diet type,
 * meat consumption, and food waste habits.
 */
public class YearlyFoodCarbonFootprintCalculator extends CalculateYearlyCarbonFootPrint {

    // Required keys for validation
    private final String[] requiredKeys = {
            "What best describes your diet?",
            "How often do you eat the following animal-based products? Beef:",
            "How often do you eat the following animal-based products? Pork:",
            "How often do you eat the following animal-based products? Chicken:",
            "How often do you eat the following animal-based products? Fish/Seafood:",
            "How often do you waste food or throw away uneaten leftovers?"
    };

    // Fields to hold responses
    private String dietType;
    private String beefConsumption;
    private String porkConsumption;
    private String chickenConsumption;
    private String fishConsumption;
    private String foodWaste;

    private static final String TAG = "YearlyFoodCarbonFootprintCalculator";

    /**
     * Calculates the yearly carbon footprint based on user responses.
     *
     * @param responses A map containing user responses to food-related questions.
     *                  The map must contain keys such as:
     *                  - "What best describes your diet?"
     *                  - "How often do you eat the following animal-based products? Beef:"
     *                  - "How often do you eat the following animal-based products? Pork:"
     *                  - "How often do you eat the following animal-based products? Chicken:"
     *                  - "How often do you eat the following animal-based products? Fish/Seafood:"
     * @return The total yearly carbon footprint in kg CO2. Returns 0 for invalid responses.
     */
    @Override
    public double calculateYearlyFootprint(HashMap<String, String> responses) {
        // Validate the responses
        if (!areResponsesValid(responses, requiredKeys)) {
            Log.d(TAG, "Invalid or missing responses Food Footprint");
            return 0.0;
        }

        // Extract responses
        this.dietType = responses.get("What best describes your diet?");
        this.foodWaste = responses.get("How often do you waste food or throw away uneaten leftovers?");

        // Calculate the diet footprint
        double dietFootprint = 0;

        // If diet is meat-based, add meat consumption footprint
        if (dietType.equalsIgnoreCase("Meat-based (eat all types of animal products)")) {
            this.beefConsumption = responses.get("How often do you eat the following animal-based products? Beef:");
            this.porkConsumption = responses.get("How often do you eat the following animal-based products? Pork:");
            this.chickenConsumption = responses.get("How often do you eat the following animal-based products? Chicken:");
            this.fishConsumption = responses.get("How often do you eat the following animal-based products? Fish/Seafood:");
            dietFootprint += calculateMeatConsumptionFootprint(beefConsumption, porkConsumption, chickenConsumption, fishConsumption);
        }
        else{
            // Calculate the diet footprint for non-meat-based diets
            dietFootprint = calculateDietFootprint(dietType);
        }

        // Add food waste footprint
        dietFootprint += calculateFoodWasteFootprint(foodWaste);

        return dietFootprint;
    }

    /**
     * Calculates the footprint for non-meat-based diets.
     *
     * @param dietType The user's diet type.
     * @return The carbon footprint in kg CO2 for the diet type.
     */
    private double calculateDietFootprint(String dietType) {
        if (dietType.equalsIgnoreCase("Vegetarian")) {
            return 1000.0;
        } else if (dietType.equalsIgnoreCase("Vegan")) {
            return 500.0;
        } else if (dietType.equalsIgnoreCase("Pescatarian (fish/seafood)")) {
            return 1500.0;
        }
        throw new IllegalArgumentException("Invalid diet type: " + dietType);
    }

    /**
     * Calculates the carbon footprint for meat consumption.
     *
     * @param beef    The user's beef consumption frequency.
     * @param pork    The user's pork consumption frequency.
     * @param chicken The user's chicken consumption frequency.
     * @param fish    The user's fish consumption frequency.
     * @return The carbon footprint in kg CO2 for meat consumption.
     */
    private double calculateMeatConsumptionFootprint(String beef, String pork, String chicken, String fish) {
        double totalFootprint = 0.0;

        // Add footprint for beef consumption
        if (beef != null) {
            if (beef.equalsIgnoreCase("Daily")) {
                totalFootprint += 2500.0;
            } else if (beef.equalsIgnoreCase("Frequently (3-5 times/week)")) {
                totalFootprint += 1900.0;
            } else if (beef.equalsIgnoreCase("Occasionally (1-2 times/week)")) {
                totalFootprint += 1300.0;
            }
        }

        // Add footprint for pork consumption
        if (pork != null) {
            if (pork.equalsIgnoreCase("Daily")) {
                totalFootprint += 1450.0;
            } else if (pork.equalsIgnoreCase("Frequently (3-5 times/week)")) {
                totalFootprint += 860.0;
            } else if (pork.equalsIgnoreCase("Occasionally (1-2 times/week)")) {
                totalFootprint += 450.0;
            }
        }

        // Add footprint for chicken consumption
        if (chicken != null) {
            if (chicken.equalsIgnoreCase("Daily")) {
                totalFootprint += 950.0;
            } else if (chicken.equalsIgnoreCase("Frequently (3-5 times/week)")) {
                totalFootprint += 600.0;
            } else if (chicken.equalsIgnoreCase("Occasionally (1-2 times/week)")) {
                totalFootprint += 200.0;
            }
        }

        // Add footprint for fish consumption
        if (fish != null) {
            if (fish.equalsIgnoreCase("Daily")) {
                totalFootprint += 800.0;
            } else if (fish.equalsIgnoreCase("Frequently (3-5 times/week)")) {
                totalFootprint += 500.0;
            } else if (fish.equalsIgnoreCase("Occasionally (1-2 times/week)")) {
                totalFootprint += 150.0;
            }
        }

        return totalFootprint;
    }

    /**
     * Calculates the carbon footprint for food waste habits.
     *
     * @param waste The user's food waste frequency.
     * @return The carbon footprint in kg CO2 for food waste.
     */
    private double calculateFoodWasteFootprint(String waste) {
        if (waste.equalsIgnoreCase("Never")) {
            return 0.0;
        } else if (waste.equalsIgnoreCase("Rarely")) {
            return 23.4;
        } else if (waste.equalsIgnoreCase("Occasionally")) {
            return 70.2;
        } else if (waste.equalsIgnoreCase("Frequently")) {
            return 140.4;
        }
        throw new IllegalArgumentException("Invalid food waste frequency: " + waste);
    }
}
