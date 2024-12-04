package net.robertx.planeteze_b07.carbonFootprintCalculators;

import android.util.Log;

import net.robertx.planeteze_b07.dataRetrievers.HousingCO2DataRetriever;

import java.util.HashMap;
import java.util.Map;

/**
 * This class calculates the yearly carbon footprint for housing-related activities,
 * such as heating, water heating, and electricity usage. It ensures robust handling
 * of incomplete or invalid data and adjusts for renewable energy usage where applicable.
 */
public class YearlyHousingCarbonFootprintCalculator extends CalculateYearlyCarbonFootPrint {

    /**
     * The data retriever for housing CO2 data.
     */
    private final HousingCO2DataRetriever dataRetriever;

    /**
     * Required keys for the user responses map.
     * These keys are used to validate the input data.
     */
    private final String[] requiredKeys = {
            "What type of home do you live in?",
            "How many people live in your household?",
            "What is the size of your home?",
            "What type of energy do you use to heat your home?",
            "What is your average monthly electricity bill?",
            "What type of energy do you use to heat water in your home?",
            "Do you use any renewable energy sources for electricity or heating (e.g., solar, wind)?"
    };

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
    @Override
    public double calculateYearlyFootprint(HashMap<String, String> responses){
        if (!areResponsesValid(responses, requiredKeys)) {
            return 0.0;
        }

        String typeOfHome = responses.get("What type of home do you live in?");
        String sizeOfHome = responses.get("What is the size of your home?");
        String electricityBill = responses.get("What is your average monthly electricity bill?");
        String numOccupants = responses.get("How many people live in your household?");
        String homeHeatingType = responses.get("What type of energy do you use to heat your home?");
        String waterHeatingType = responses.get("What type of energy do you use to heat water?");
        String useRenewableEnergy = responses.get("Do you use any renewable energy sources for electricity or heating (e.g., solar, wind)?");

        double heatingCO2 = dataRetriever.getSpecificCO2Value(typeOfHome, sizeOfHome, electricityBill, numOccupants, homeHeatingType);
        double waterHeatingCO2 = dataRetriever.getSpecificCO2Value(typeOfHome, sizeOfHome, electricityBill, numOccupants, waterHeatingType);
        double additionalCO2 = 0.0;
        if (!homeHeatingType.equals(waterHeatingType)) {
            additionalCO2 = 233.0;
        }
        double renewableAdjustment = adjustForRenewableEnergy(useRenewableEnergy);
        double totalFootprint = heatingCO2 + waterHeatingCO2 - additionalCO2 + renewableAdjustment;

        return Math.max(totalFootprint, 0.0);
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
