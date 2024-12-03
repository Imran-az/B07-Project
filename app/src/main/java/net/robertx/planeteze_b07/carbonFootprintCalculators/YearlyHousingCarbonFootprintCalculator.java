package net.robertx.planeteze_b07.carbonFootprintCalculators;

import android.util.Log;

import net.robertx.planeteze_b07.DataRetrievers.HousingCO2DataRetriever;

import java.util.Map;

/**
 * This class calculates the yearly carbon footprint for housing-related activities,
 * such as heating, water heating, and electricity usage. It ensures robust handling
 * of incomplete or invalid data and adjusts for renewable energy usage where applicable.
 */
public class YearlyHousingCarbonFootprintCalculator {

    private final HousingCO2DataRetriever dataRetriever;

    private static final String TAG = "YearlyHousingCarbonFootprintCalculator";

    /**
     * Constructor initializes the CO2 data retriever dependency.
     */
    public YearlyHousingCarbonFootprintCalculator() {
        this.dataRetriever = new HousingCO2DataRetriever();
    }

    /**
     * Calculates the yearly carbon footprint based on user responses.
     *
     * @param responses A map containing user responses to survey questions.
     *                  The map must contain keys such as:
     *                  - "What type of home do you live in?"
     *                  - "What is the size of your home?"
     *                  - "What is your average monthly electricity bill?"
     *                  - "How many people live in your household?"
     *                  - "What type of energy do you use to heat your home?"
     *                  - "What type of energy do you use to heat water?"
     *                  - "Do you use any renewable energy sources for electricity or heating (e.g., solar, wind)?"
     * @return The calculated carbon footprint in kg CO2. Returns 0 if any required data is missing or invalid.
     */
    public double calculateYearlyFootprint(Map<String, String> responses) {
        if (!areResponsesValid(responses)) {
            Log.d(TAG, "Invalid or missing responses Housing Footprint");
            return 0.0; // Return 0 if any required key is missing or invalid
        }

        // Extract relevant values from the responses
        String typeOfHome = responses.get("What type of home do you live in?");
        String sizeOfHome = responses.get("What is the size of your home?");
        String electricityBill = responses.get("What is your average monthly electricity bill?");
        String numOccupants = responses.get("How many people live in your household?");
        String homeHeatingType = responses.get("What type of energy do you use to heat your home?");
        String waterHeatingType = responses.get("What type of energy do you use to heat water?");
        String useRenewableEnergy = responses.get("Do you use any renewable energy sources for electricity or heating (e.g., solar, wind)?");

        // Retrieve CO2 values for heating and water heating
        double heatingCO2 = dataRetriever.getSpecificCO2Value(typeOfHome, sizeOfHome, electricityBill, numOccupants, homeHeatingType);
        double waterHeatingCO2 = dataRetriever.getSpecificCO2Value(typeOfHome, sizeOfHome, electricityBill, numOccupants, waterHeatingType);

        // Calculate additional CO2 if heating and water heating types are different
        double additionalCO2 = !homeHeatingType.equals(waterHeatingType) ? 233.0 : 0.0;

        // Adjust for renewable energy usage
        double renewableAdjustment = adjustForRenewableEnergy(useRenewableEnergy);

        // Ensure the total footprint is not negative
        Log.d(TAG, "Heating CO2: " + heatingCO2);
        Log.d(TAG, "Water Heating CO2: " + waterHeatingCO2);
        Log.d(TAG, "Additional CO2: " + additionalCO2);
        Log.d(TAG, "Renewable Adjustment: " + renewableAdjustment);
        Log.d(TAG, "Total Footprint: " + (heatingCO2 + waterHeatingCO2 - additionalCO2 + renewableAdjustment));
        double totalFootprint = heatingCO2 + waterHeatingCO2 - additionalCO2 + renewableAdjustment;
        return Math.max(totalFootprint, 0.0);
    }

    /**
     * Validates the responses map to ensure all required keys are present and non-null.
     *
     * @param responses The map of user responses to validate.
     * @return True if all required keys are present and non-null; false otherwise.
     */
    private boolean areResponsesValid(Map<String, String> responses) {
        if (responses == null || responses.isEmpty()) {
            return false;
        }

        // List of required keys for housing footprint calculation
        String[] requiredKeys = {
                "What type of home do you live in?",
                "How many people live in your household?",
                "What is the size of your home?",
                "What type of energy do you use to heat your home?",
                "What is your average monthly electricity bill?",
                "What type of energy do you use to heat water in your home?",
                "Do you use any renewable energy sources for electricity or heating (e.g., solar, wind)?"
        };

        // Check if all required keys exist and have non-null values
        for (String key : requiredKeys) {
            if (!responses.containsKey(key) || responses.get(key) == null) {
                return false;
            }
        }
        return true;
    }


    /**
     * Adjusts the CO2 footprint based on renewable energy usage.
     *
     * @param renewableEnergy The user's renewable energy usage description.
     * @return The CO2 adjustment value (negative for reductions, zero for no adjustment).
     */
    private double adjustForRenewableEnergy(String renewableEnergy) {
        switch (renewableEnergy) {
            case "Yes, primarily (more than 50% of energy use)":
                return -6000.0;
            case "Yes, partially (less than 50% of energy use)":
                return -4000.0;
            default:
                return 0.0;
        }
    }
}
