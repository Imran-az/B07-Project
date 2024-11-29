package net.robertx.planeteze_b07.CarbonFootprintCalculators;

import android.util.Log;
import java.util.HashMap;
import java.util.Map;

/**
 * This class calculates the yearly carbon footprint for consumption-related activities,
 * such as buying clothes, electronic devices, and recycling habits. It uses predefined
 * emissions and reductions to calculate the total footprint.
 */
public class YearlyConsumptionFootprintCalculator extends CalculateYearlyCarbonFootPrint {

    // Predefined emission values for clothes and devices
    private static final Map<String, Double> CLOTHES_EMISSION = new HashMap<>();
    private static final Map<String, Double> DEVICES_EMISSION = new HashMap<>();
    // Predefined reduction values for recycling based on frequency
    private static final Map<String, Map<String, Double>> RECYCLING_REDUCTION = new HashMap<>();

    private static final String[] requiredKeys = {
            "How often do you buy new clothes?",
            "Do you buy second-hand or eco-friendly products?",
            "How many electronic devices (phones, laptops, etc.) have you purchased in the past year?",
            "How often do you recycle?"};

    private static final String TAG = "ConsumptionFootprint";

    // Static block for initializing emissions and reductions
    static {
        initializeEmissions();
        initializeRecyclingReductions();
    }

    /**
     * Calculates the yearly carbon footprint based on user responses.
     *
     * @param responses A map containing user responses to survey questions.
     *                  The map must contain keys such as:
     *                  - "How often do you buy new clothes?"
     *                  - "How many electronic devices (phones, laptops, etc.) have you purchased in the past year?"
     *                  - "How often do you recycle?"
     *                  - "Do you buy second-hand or eco-friendly products?"
     * @return The calculated carbon footprint in kg CO2. Returns 0 for invalid data.
     */
    @Override
    public double calculateYearlyFootprint(HashMap<String, String> responses) {
        if(!areResponsesValid(responses, requiredKeys)) {
            Log.d(TAG, "Invalid or missing responses Consumption Footprint");
            return 0.0; // Return 0 if any required key is missing or invalid
        }

        // Extract responses or assign defaults if not provided
        String clothesFrequency = responses.getOrDefault("How often do you buy new clothes?", "Rarely");
        String devicesPurchased = responses.getOrDefault("How many electronic devices (phones, laptops, etc.) have you purchased in the past year?", "None");
        String recyclingFrequency = responses.getOrDefault("How often do you recycle?", "Never");
        String isEcoFriendly = responses.getOrDefault("Do you buy second-hand or eco-friendly products?", "No");

        // Validate inputs
        validateInput(clothesFrequency, devicesPurchased);

        // Calculate emissions and reductions
        double clothesEmission = CLOTHES_EMISSION.get(clothesFrequency);
        double devicesEmission = DEVICES_EMISSION.get(devicesPurchased);
        double recyclingReduction = getRecyclingReduction(clothesFrequency, recyclingFrequency);

        // Log intermediate values
        Log.d(TAG, "Clothes Emission pop: " + clothesEmission);
        Log.d(TAG, "Devices Emission: " + devicesEmission);
        Log.d(TAG, "Recycling Reduction: " + recyclingReduction);

        // Calculate the total emissions with adjustments for eco-friendly habits
        double totalEmission = calculateTotalEmission(clothesEmission, devicesEmission, recyclingReduction, isEcoFriendly);

        return Math.max(totalEmission, 0.0); // Ensure no negative emissions
    }

    /**
     * Validates the inputs for clothes frequency and devices purchased.
     *
     * @param clothesFrequency The frequency of buying clothes.
     * @param devicesPurchased The number of devices purchased.
     * @throws IllegalArgumentException if invalid values are provided.
     */
    private void validateInput(String clothesFrequency, String devicesPurchased) {
        if (!CLOTHES_EMISSION.containsKey(clothesFrequency)) {
            throw new IllegalArgumentException("Invalid clothes frequency: " + clothesFrequency);
        }
        if (!DEVICES_EMISSION.containsKey(devicesPurchased)) {
            throw new IllegalArgumentException("Invalid devices purchased: " + devicesPurchased);
        }
    }

    /**
     * Retrieves the recycling reduction value based on clothes frequency and recycling frequency.
     *
     * @param clothesFrequency   The frequency of buying clothes.
     * @param recyclingFrequency The frequency of recycling.
     * @return The reduction value in kg CO2.
     */
    private double getRecyclingReduction(String clothesFrequency, String recyclingFrequency) {
        Map<String, Double> reductionMap = RECYCLING_REDUCTION.get(clothesFrequency);
        if (reductionMap == null) {
            throw new IllegalArgumentException("Invalid clothes frequency for recycling reduction: " + clothesFrequency);
        }
        return reductionMap.getOrDefault(recyclingFrequency, 0.0);
    }

    /**
     * Calculates the total emissions, adjusted for eco-friendly habits.
     *
     * @param clothesEmission    Emissions from buying clothes.
     * @param devicesEmission    Emissions from buying devices.
     * @param recyclingReduction Reductions from recycling habits.
     * @param ecoFriendly        The eco-friendly buying habit.
     * @return The total emissions in kg CO2.
     */
    private double calculateTotalEmission(double clothesEmission, double devicesEmission, double recyclingReduction, String ecoFriendly) {
        double ecoFactor;
        if (ecoFriendly.equalsIgnoreCase("Yes, regularly")) {
            ecoFactor = 0.5;
        } else if (ecoFriendly.equalsIgnoreCase("Yes, occasionally")) {
            ecoFactor = 0.7;
        } else {
            ecoFactor = 1.0;
        }
        return ((clothesEmission * ecoFactor) + devicesEmission) - recyclingReduction;
    }

    /**
     * Initializes the emission values for clothes and devices.
     */
    private static void initializeEmissions() {
        CLOTHES_EMISSION.put("Monthly", 360.0);
        CLOTHES_EMISSION.put("Quarterly", 120.0);
        CLOTHES_EMISSION.put("Annually", 100.0);
        CLOTHES_EMISSION.put("Rarely", 5.0);

        DEVICES_EMISSION.put("None", 0.0);
        DEVICES_EMISSION.put("1", 300.0);
        DEVICES_EMISSION.put("2", 600.0);
        DEVICES_EMISSION.put("3", 900.0);
        DEVICES_EMISSION.put("4 or More", 1200.0);
    }

    /**
     * Initializes the recycling reduction values based on frequency.
     */
    private static void initializeRecyclingReductions() {
        Map<String, Double> monthlyReduction = Map.of(
                "Occasionally", 54.0,
                "Frequently", 108.0,
                "Always", 180.0
        );
        Map<String, Double> quarterlyReduction = Map.of(
                "Occasionally", 30.1,
                "Frequently", 20.0,
                "Always", 10.5
        );
        Map<String, Double> annualReduction = Map.of(
                "Occasional", 15.0,
                "Frequent", 30.0,
                "Always", 50.0
        );
        Map<String, Double> rarelyReduction = Map.of(
                "Occasionally", 0.75,
                "Frequently", 1.0,
                "Always", 2.5
        );

        RECYCLING_REDUCTION.put("Monthly", monthlyReduction);
        RECYCLING_REDUCTION.put("Quarterly", quarterlyReduction);
        RECYCLING_REDUCTION.put("Annually", annualReduction);
        RECYCLING_REDUCTION.put("Rarely", rarelyReduction);
    }

}
