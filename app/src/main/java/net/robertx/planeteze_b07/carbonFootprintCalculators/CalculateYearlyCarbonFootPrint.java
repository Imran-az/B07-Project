package net.robertx.planeteze_b07.carbonFootprintCalculators;

import java.util.HashMap;
import java.util.Map;

/**
 * Abstract class for calculating yearly carbon footprints.
 * Provides shared validation logic for all footprint calculators.
 */
public abstract class CalculateYearlyCarbonFootPrint {

    /**
     * Abstract method to calculate the yearly footprint.
     * Must be implemented by each concrete calculator.
     *
     * @param responses A map containing user responses.
     * @return The calculated yearly footprint in kg CO2.
     */
    public abstract double calculateYearlyFootprint(HashMap<String, String> responses);

    /**
     * Validates if the required keys are present and non-null in the response map.
     *
     * @param responses A map containing user responses.
     * @param requiredKeys An array of required keys to validate.
     * @return True if all required keys are present and non-null, false otherwise.
     */
    protected boolean areResponsesValid(Map<String, String> responses, String[] requiredKeys) {
        if (responses == null || responses.isEmpty()) {
            return false;
        }

        for (String key : requiredKeys) {
            if (!responses.containsKey(key) || responses.get(key) == null) {
                return false;
            }
        }
        return true;
    }
}
