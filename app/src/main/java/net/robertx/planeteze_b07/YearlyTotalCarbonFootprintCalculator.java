package net.robertx.planeteze_b07;

import java.util.HashMap;

public class YearlyTotalCarbonFootprintCalculator {

    // Individual category calculators
    private final YearlyDrivingCarbonFootprintCalculator drivingCalculator = new YearlyDrivingCarbonFootprintCalculator();
    private final YearlyPublicTransportationCarbonFootprintCalculator publicTransportCalculator = new YearlyPublicTransportationCarbonFootprintCalculator();
    private final YearlyHousingCarbonFootprintCalculator housingCalculator = new YearlyHousingCarbonFootprintCalculator();
    private final YearlyFoodCarbonFootprintCalculator foodCalculator = new YearlyFoodCarbonFootprintCalculator();

    // Method to calculate total emissions
    public double calculateTotalEmissions(HashMap<String, String> responses) {
        double totalEmissions = 0;

        // Add contributions from each category
        totalEmissions += drivingCalculator.calculateYearlyFootprint(responses);
        totalEmissions += publicTransportCalculator.calculateYearlyFootprint(responses);
        totalEmissions += housingCalculator.calculateYearlyFootprint(responses);
        totalEmissions += foodCalculator.calculateYearlyFootprint(responses);

        return totalEmissions;
    }

}
