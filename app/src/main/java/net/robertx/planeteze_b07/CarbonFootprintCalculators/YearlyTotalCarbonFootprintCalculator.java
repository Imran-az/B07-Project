package net.robertx.planeteze_b07.CarbonFootprintCalculators;

import java.io.IOException;
import java.util.HashMap;

public class YearlyTotalCarbonFootprintCalculator {

    // Individual category calculators
    private final YearlyDrivingCarbonFootprintCalculator drivingCalculator = new YearlyDrivingCarbonFootprintCalculator();
    private final YearlyPublicTransportationCarbonFootprintCalculator publicTransportCalculator = new YearlyPublicTransportationCarbonFootprintCalculator();
    private final YearlyFoodCarbonFootprintCalculator foodCalculator = new YearlyFoodCarbonFootprintCalculator();

    private final YearlyHousingCarbonFootprintCalculator housingCalculator = new YearlyHousingCarbonFootprintCalculator();

    private final YearlyFlightCarbonFootprintCalculator flightCalculator = new YearlyFlightCarbonFootprintCalculator();

    private  final YearlyConsumptionFootprintCalculator consumptionCalculator = new YearlyConsumptionFootprintCalculator();


    public YearlyTotalCarbonFootprintCalculator() throws IOException {
    }

    public HashMap<String, Double> calculatePerCategoryEmission(HashMap<String, String> responses) {
        HashMap<String, Double> categoryEmissions = new HashMap<>();
        categoryEmissions.put("DrivingEmissions", drivingCalculator.calculateYearlyFootprint(responses));
        categoryEmissions.put("PublicTransportEmissions", publicTransportCalculator.calculateYearlyFootprint(responses));
        categoryEmissions.put("FlightEmissions", flightCalculator.calculateYearlyFootprint(responses));
        categoryEmissions.put("HousingEmissions", housingCalculator.calculateYearlyFootprint(responses));
        categoryEmissions.put("FoodEmissions", foodCalculator.calculateYearlyFootprint(responses));
        categoryEmissions.put("ConsumptionEmissions", consumptionCalculator.calculateYearlyFootprint(responses));
        return categoryEmissions;
    }

    // Method to calculate total emissions
    public double calculateTotalEmissions(HashMap<String, String> responses) {
        double totalEmissions = 0;
        // Add contributions from each category
        totalEmissions += drivingCalculator.calculateYearlyFootprint(responses);
        System.out.println("Driving" + drivingCalculator.calculateYearlyFootprint(responses));
        System.out.println(totalEmissions);
        totalEmissions += publicTransportCalculator.calculateYearlyFootprint(responses);
        System.out.println("PT" + publicTransportCalculator.calculateYearlyFootprint(responses));
        System.out.println(totalEmissions);
        totalEmissions += flightCalculator.calculateYearlyFootprint(responses);
        System.out.println("Flying" + flightCalculator.calculateYearlyFootprint(responses));
        System.out.println(totalEmissions);
        totalEmissions += housingCalculator.calculateYearlyFootprint(responses);
        System.out.println("House" + housingCalculator.calculateYearlyFootprint(responses));
        System.out.println(totalEmissions);
        totalEmissions += foodCalculator.calculateYearlyFootprint(responses);
        System.out.println("Food dhwiaskdj  " + foodCalculator.calculateYearlyFootprint(responses));
        System.out.println(totalEmissions);
        totalEmissions += consumptionCalculator.calculateYearlyFootprint(responses);
        System.out.println("Consumption" + consumptionCalculator.calculateYearlyFootprint(responses));
        System.out.println(totalEmissions);

        return totalEmissions;
    }

}
