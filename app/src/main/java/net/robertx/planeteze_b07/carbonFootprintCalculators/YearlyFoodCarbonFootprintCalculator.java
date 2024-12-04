package net.robertx.planeteze_b07.carbonFootprintCalculators;

import android.util.Log;

import java.util.HashMap;

/**
 * Calculates the yearly carbon footprint for food consumption based on diet type,
 * meat consumption, and food waste habits.
 */
public class YearlyFoodCarbonFootprintCalculator extends CalculateYearlyCarbonFootPrint {

    /**
     * Required keys for the user responses map.
     * These keys are used to validate the input data.
     */
    private final String[] requiredKeys = {
            "What best describes your diet?",
            "How often do you eat the following animal-based products? Beef:",
            "How often do you eat the following animal-based products? Pork:",
            "How often do you eat the following animal-based products? Chicken:",
            "How often do you eat the following animal-based products? Fish/Seafood:",
            "How often do you waste food or throw away uneaten leftovers?"
    };

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
        if (!areResponsesValid(responses, requiredKeys)) {
            Log.d(TAG, "Invalid or missing responses Food Footprint");
            return 0.0;
        }
        String dietType = responses.get("What best describes your diet?");
        String foodWaste = responses.get("How often do you waste food or throw away uneaten leftovers?");

        double dietFootprint = 0;

        if (dietType.equalsIgnoreCase("Meat-based (eat all types of animal products)")) {
            String beefConsumption = responses.get("How often do you eat the following animal-based products? Beef:");
            String porkConsumption = responses.get("How often do you eat the following animal-based products? Pork:");
            String chickenConsumption = responses.get("How often do you eat the following animal-based products? Chicken:");
            String fishConsumption = responses.get("How often do you eat the following animal-based products? Fish/Seafood:");
            dietFootprint += calculateMeatConsumptionFootprint(beefConsumption, porkConsumption, chickenConsumption, fishConsumption);
        }
        else{
            dietFootprint = calculateDietFootprint(dietType);
        }

        dietFootprint += calculateFoodWasteFootprint(foodWaste);

        return dietFootprint;
    }

    /**
     * Calculates the footprint for non-meat-based diets.
     *
     * @param dietType The user's diet type. Expected values are "Vegetarian", "Vegan", or "Pescatarian (fish/seafood)".
     * @return The carbon footprint in kg CO2 for the diet type.
     * @throws IllegalArgumentException if the diet type is invalid.
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
     * @param frequencyBeef    The user's beef consumption frequency.
     * @param frequencyPork    The user's pork consumption frequency.
     * @param frequencyChicken The user's chicken consumption frequency.
     * @param frequencyFish    The user's fish consumption frequency.
     * @return The carbon footprint in kg CO2 for meat consumption. Returns 0 for invalid responses.
     */
    private double calculateMeatConsumptionFootprint(String frequencyBeef, String frequencyPork, String frequencyChicken, String frequencyFish) {
        double totalFootprint = 0.0;

        if (frequencyBeef != null) {
            if (frequencyBeef.equalsIgnoreCase("Daily")) {
                totalFootprint += 2500.0;
            } else if (frequencyBeef.equalsIgnoreCase("Frequently (3-5 times/week)")) {
                totalFootprint += 1900.0;
            } else if (frequencyBeef.equalsIgnoreCase("Occasionally (1-2 times/week)")) {
                totalFootprint += 1300.0;
            }
        }

        if (frequencyPork != null) {
            if (frequencyPork.equalsIgnoreCase("Daily")) {
                totalFootprint += 1450.0;
            } else if (frequencyPork.equalsIgnoreCase("Frequently (3-5 times/week)")) {
                totalFootprint += 860.0;
            } else if (frequencyPork.equalsIgnoreCase("Occasionally (1-2 times/week)")) {
                totalFootprint += 450.0;
            }
        }

        if (frequencyChicken != null) {
            if (frequencyChicken.equalsIgnoreCase("Daily")) {
                totalFootprint += 950.0;
            } else if (frequencyChicken.equalsIgnoreCase("Frequently (3-5 times/week)")) {
                totalFootprint += 600.0;
            } else if (frequencyChicken.equalsIgnoreCase("Occasionally (1-2 times/week)")) {
                totalFootprint += 200.0;
            }
        }

        if (frequencyFish != null) {
            if (frequencyFish.equalsIgnoreCase("Daily")) {
                totalFootprint += 800.0;
            } else if (frequencyFish.equalsIgnoreCase("Frequently (3-5 times/week)")) {
                totalFootprint += 500.0;
            } else if (frequencyFish.equalsIgnoreCase("Occasionally (1-2 times/week)")) {
                totalFootprint += 150.0;
            }
        }
        return totalFootprint;
    }

    /**
     * Calculates the carbon footprint for food waste habits.
     *
     * @param wasteFrequency The user's food waste frequency. Expected values are "Never", "Rarely", "Occasionally", or "Frequently".
     * @return The carbon footprint in kg CO2 for food waste.
     * @throws IllegalArgumentException if the food waste frequency is invalid.
     */
    private double calculateFoodWasteFootprint(String wasteFrequency) {
        if (wasteFrequency.equalsIgnoreCase("Never")) {
            return 0.0;
        } else if (wasteFrequency.equalsIgnoreCase("Rarely")) {
            return 23.4;
        } else if (wasteFrequency.equalsIgnoreCase("Occasionally")) {
            return 70.2;
        } else if (wasteFrequency.equalsIgnoreCase("Frequently")) {
            return 140.4;
        }
        throw new IllegalArgumentException("Invalid food waste frequency: " + wasteFrequency);
    }
}
