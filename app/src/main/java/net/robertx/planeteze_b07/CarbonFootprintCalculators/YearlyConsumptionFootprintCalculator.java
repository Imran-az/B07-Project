package net.robertx.planeteze_b07.CarbonFootprintCalculators;

import android.util.Log;
import java.util.HashMap;
import java.util.Map;

public class YearlyConsumptionFootprintCalculator implements CalculateYearlyCarbonFootPrint {

    private static final Map<String, Double> CLOTHES_EMISSION = new HashMap<>();
    private static final Map<String, Double> DEVICES_EMISSION = new HashMap<>();
    private static final Map<String, Map<String, Double>> RECYCLING_REDUCTION = new HashMap<>();

    private static final Map<String, Double> monthlyReduction = new HashMap<>();
    private static final Map<String, Double> annualReduction = new HashMap<>();
    private static final Map<String, Double> rarelyReduction = new HashMap<>();
    private static final Map<String, Double> quarterlyReduction = new HashMap<>();

    private static final String TAG = "ConsumptionFootprint";

    static {
        CLOTHES_EMISSION.put("Monthly", 360.0);
        CLOTHES_EMISSION.put("Quarterly", 120.0);
        CLOTHES_EMISSION.put("Annually", 100.0);
        CLOTHES_EMISSION.put("Rarely", 5.0);

        DEVICES_EMISSION.put("None", 0.0);
        DEVICES_EMISSION.put("1", 300.0);
        DEVICES_EMISSION.put("2", 600.0);
        DEVICES_EMISSION.put("3", 900.0);
        DEVICES_EMISSION.put("4 or More", 1200.0);

        monthlyReduction.put("Occasionally", 54.0);
        monthlyReduction.put("Frequently", 108.0);
        monthlyReduction.put("Always", 180.0);

        annualReduction.put("Occasional", 15.0);
        annualReduction.put("Frequent", 30.0);
        annualReduction.put("Always", 50.0);

        rarelyReduction.put("Occasionally", 0.75);
        rarelyReduction.put("Frequently", 1.0);
        rarelyReduction.put("Always", 2.5);

        quarterlyReduction.put("Occasionally", 30.1);
        quarterlyReduction.put("Frequently", 20.0);
        quarterlyReduction.put("Always", 10.5);

        RECYCLING_REDUCTION.put("Monthly", monthlyReduction);
        RECYCLING_REDUCTION.put("Annually", annualReduction);
        RECYCLING_REDUCTION.put("Rarely", rarelyReduction);
        RECYCLING_REDUCTION.put("Quarterly", quarterlyReduction);
    }
    @Override
    public double calculateYearlyFootprint(HashMap<String, String> responses) {
        // Extract responses
        String clothesFrequency = responses.getOrDefault("How often do you buy new clothes?", "Rarely");
        String devicesPurchased = responses.getOrDefault("How many electronic devices (phones, laptops, etc.) have you purchased in the past year?", "None");
        String recyclingFrequency = responses.getOrDefault("How often do you recycle?", "Never");
        String isEcoFriendly = responses.getOrDefault("Do you buy second-hand or eco-friendly products?", "No");

        // Validate clothes frequency
        if (!CLOTHES_EMISSION.containsKey(clothesFrequency)) {
            throw new IllegalArgumentException("Invalid clothes frequency: " + clothesFrequency);
        }

        // Validate device purchases
        if (!DEVICES_EMISSION.containsKey(devicesPurchased)) {
            throw new IllegalArgumentException("Invalid devices purchased: " + devicesPurchased);
        }

        // Fetch the sub-map for recycling reductions
        Map<String, Double> reductionMap = RECYCLING_REDUCTION.get(clothesFrequency);
        if (reductionMap == null) {
            throw new IllegalArgumentException("Invalid clothes frequency for recycling reduction: " + clothesFrequency);
        }

        // Get the recycling reduction value
        double recyclingReduction = reductionMap.getOrDefault(recyclingFrequency, 0.0);

        // Calculate emissions
        double clothesEmission = CLOTHES_EMISSION.get(clothesFrequency);
        double devicesEmission = DEVICES_EMISSION.get(devicesPurchased);

        // Debug log
        Log.d(TAG, "Clothes Emission: " + clothesEmission);
        Log.d(TAG, "Devices Emission: " + devicesEmission);
        Log.d(TAG, "Recycling Reduction: " + recyclingReduction);

        // Calculate total emission
        double totalEmission;
        if ("Yes, regularly".equalsIgnoreCase(isEcoFriendly)) {
            totalEmission = ((clothesEmission * 0.5) + devicesEmission) - recyclingReduction;
        } else if ("Yes, occasionally".equalsIgnoreCase(isEcoFriendly)) {
            totalEmission = ((clothesEmission * 0.7) + devicesEmission) - recyclingReduction;
        } else {
            totalEmission = (clothesEmission + devicesEmission) - recyclingReduction;
        }

        return Math.max(totalEmission, 0.0); // Ensure no negative emissions
    }

}
