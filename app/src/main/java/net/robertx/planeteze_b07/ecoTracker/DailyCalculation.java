package net.robertx.planeteze_b07.ecoTracker;

import java.util.HashMap;

/**
 * Calculates daily CO2 emissions based on user inputs.
 *
 * This class computes CO2 emissions for various categories such as transportation,
 * energy usage, and purchases. It also calculates the total daily CO2 emissions
 * and provides a method to represent the results as a {@code HashMap}.
 */
public class DailyCalculation extends Calculations{

    /** CO2 emissions from personal vehicle usage. */
    public final double personalVehicleCalculation;

    /** CO2 emissions from public transportation usage. */
    public final double publicTransportCalculation;

    /** CO2 emissions from flights (short-haul or long-haul). */
    public final double flightCalculation;

    /** CO2 emissions from meat consumption. */
    public final double meatCalculation;

    /** CO2 emissions from clothing purchases. */
    public final double clothesCalculation;

    /** CO2 emissions from electronics purchases. */
    public final double electronicsCalculation;

    /** CO2 emissions from gas bill payments. */
    public final double gasCalculator;

    /** CO2 emissions from electricity bill payments. */
    public final double electricityCalculator;

    /** CO2 emissions from water bill payments. */
    public final double waterCalculator;

    /** CO2 emissions from other miscellaneous purchases. */
    public final double otherPurchasesCalculator;

    /** Total daily CO2 emissions from all categories. */
    public final double dailyTotal;

    /**
     * Constructs a {@code DailyCalculation} object and computes CO2 emissions for various categories.
     *
     * @param responses A {@code HashMap} containing user responses to survey questions.
     *                  The keys correspond to specific activities (e.g., "Drive Personal Vehicle"),
     *                  and the values contain user inputs.
     */
    public DailyCalculation(HashMap<String, String> responses) {
        this.personalVehicleCalculation = personalVehicleCO2(responses);
        this.publicTransportCalculation = publicTransportCO2(responses);
        this.flightCalculation = flightCO2(responses);
        this.meatCalculation = meatCO2(responses);
        this.clothesCalculation = clothesCO2(responses);
        this.electronicsCalculation = electronicsCO2(responses);
        this.gasCalculator = gasBillCO2(responses);
        this.electricityCalculator = elecBillCO2(responses);
        this.waterCalculator = waterBillCO2(responses);
        this.otherPurchasesCalculator = otherPurchasesCO2(responses);
        this.dailyTotal = personalVehicleCalculation + publicTransportCalculation
                + flightCalculation + meatCalculation
                + clothesCalculation + electronicsCalculation + gasCalculator
                + electricityCalculator + waterCalculator;
    }

    /**
     * Converts the calculated CO2 emissions into a {@code HashMap} for easy storage or processing.
     *
     * @return A {@code HashMap} where keys represent categories of CO2 emissions, and values
     *         represent the corresponding emissions in kilograms.
     */
    public HashMap<String, Double> toHashMap() {
        HashMap<String, Double> dataMap = new HashMap<>();

        // Add the calculations to the HashMap with appropriate keys
        dataMap.put("DrivePersonalVehicleDailyCO2", personalVehicleCalculation);
        dataMap.put("TakePublicTransportationDailyCO2", publicTransportCalculation);
        dataMap.put("Flight (Short-Haul or Long-Haul)DailyCO2", flightCalculation);
        dataMap.put("MealDailyCO2", meatCalculation);
        dataMap.put("ClothesDailyCO2", clothesCalculation);
        dataMap.put("ElectronicsDailyCO2", electronicsCalculation);
        dataMap.put("OtherPurchasesDailyCO2", otherPurchasesCalculator);
        dataMap.put("EnergyBillDailyCO2", gasCalculator + electricityCalculator + waterCalculator);
        dataMap.put("dailyTotal", dailyTotal);

        return dataMap;
    }
}
