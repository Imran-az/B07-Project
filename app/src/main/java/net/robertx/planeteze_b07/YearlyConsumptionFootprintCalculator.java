package net.robertx.planeteze_b07;

import java.util.HashMap;
import java.util.Map;

public class YearlyConsumptionFootprintCalculator implements CalculateYearlyCarbonFootPrint {

    private static final Map<String, Double> CLOTHES_EMISSION = new HashMap<>();
    private static final Map<String, Double> DEVICES_EMISSION = new HashMap<>();
    private static final Map<String, Map<String, Double>> RECYCLING_REDUCTION = new HashMap<>();

    private static final Map<String, Double> monthlyReduction = new HashMap<>();
    private static final Map<String, Double> annualReduction = new HashMap<>();
    private static final Map<String, Double> rarelyReduction = new HashMap<>();

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

        annualReduction.put("Occasionally", 15.0);
        annualReduction.put("Frequently", 30.0);
        annualReduction.put("Always", 50.0);

        rarelyReduction.put("Occasionally", 0.75);
        rarelyReduction.put("Frequently", 1.0);
        rarelyReduction.put("Always", 2.5);

        RECYCLING_REDUCTION.put("Monthly", monthlyReduction);
        RECYCLING_REDUCTION.put("Annually", annualReduction);
        RECYCLING_REDUCTION.put("Rarely", rarelyReduction);
    }
    @Override
    public double calculateYearlyFootprint(HashMap<String, String> responses){
        String clothesFrequency = responses.get("How often do you buy new clothes?");
        String devicesPurchased = responses.get("How many new electronic devices do you purchase each year?");
        String recyclingFrequency = responses.get("How often do you recycle?");
        String isEcoFriendly = responses.get("How eco-friendly are your purchases?");

        if (!CLOTHES_EMISSION.containsKey(clothesFrequency)) {
            throw new IllegalArgumentException("Invalid clothes frequency: " + clothesFrequency);
        }
        if (!DEVICES_EMISSION.containsKey(devicesPurchased)) {
            throw new IllegalArgumentException("Invalid devices purchased: " + devicesPurchased);
        }
        if (!RECYCLING_REDUCTION.containsKey(clothesFrequency)) {
            throw new IllegalArgumentException("Invalid recycling frequency: " + recyclingFrequency);
        }

        double clothesEmission = CLOTHES_EMISSION.get(clothesFrequency);
        double devicesEmission = DEVICES_EMISSION.get(devicesPurchased);

        Map<String, Double> reductionMap = RECYCLING_REDUCTION.get(clothesFrequency);
        double recyclingReduction = reductionMap.getOrDefault(recyclingFrequency, 0.0);

        double totalEmission;
        if (isEcoFriendly.equalsIgnoreCase("Regularly")) {
            totalEmission = ((clothesEmission * 0.5) + devicesEmission) - recyclingReduction;
        } else if (isEcoFriendly.equalsIgnoreCase("Occasionally")) {
            totalEmission = ((clothesEmission * 0.7) + devicesEmission) - recyclingReduction;
        } else {
            totalEmission = (clothesEmission + devicesEmission) - recyclingReduction;
        }

        return Math.max(totalEmission, 0.0);
    }
}
