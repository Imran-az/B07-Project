package net.robertx.planeteze_b07.CarbonFootprintCalculators;

import android.util.Log;

import java.io.IOException;
import java.util.HashMap;

/**
 * Calculates the yearly total carbon footprint by aggregating emissions
 * from various categories such as driving, public transportation, food,
 * housing, flights, and consumption.
 */
public class YearlyTotalCarbonFootprintCalculator {

    // Individual category calculators
    private final YearlyDrivingCarbonFootprintCalculator drivingCalculator;
    private final YearlyPublicTransportationCarbonFootprintCalculator publicTransportCalculator;
    private final YearlyFoodCarbonFootprintCalculator foodCalculator;
    private final YearlyHousingCarbonFootprintCalculator housingCalculator;
    private final YearlyFlightCarbonFootprintCalculator flightCalculator;
    private final YearlyConsumptionFootprintCalculator consumptionCalculator;

    /**
     * Constructor for `YearlyTotalCarbonFootprintCalculator` initializing individual calculators.
     *
     * @throws IOException If any dependency-related issue arises.
     */
    public YearlyTotalCarbonFootprintCalculator() throws IOException {
        this.drivingCalculator = new YearlyDrivingCarbonFootprintCalculator();
        this.publicTransportCalculator = new YearlyPublicTransportationCarbonFootprintCalculator();
        this.foodCalculator = new YearlyFoodCarbonFootprintCalculator();
        this.housingCalculator = new YearlyHousingCarbonFootprintCalculator();
        this.flightCalculator = new YearlyFlightCarbonFootprintCalculator();
        this.consumptionCalculator = new YearlyConsumptionFootprintCalculator();
    }

    /**
     * Calculates the carbon footprint for each category and stores the results in a map.
     *
     * @param responses A map containing user responses for all survey questions.
     *                   The map must contain keys such as:
     *                  - "Do you own or regularly use a car?"
     *                  - "What type of car do you drive?"
     *                  - "How many kilometers/miles do you drive per year?"
     *                  - "How often do you use public transportation (bus, train, subway)?"
     *                  - "How much time do you spend on public transport per week (bus, train, subway)?"
     *                  - "How many short-haul flights (less than 1,500 km / 932 miles) have you taken in the past year?"
     *                  - "How many long-haul flights (more than 1,500 km / 932 miles) have you taken in the past year?"
     *                  - "What best describes your diet?"
     *                  - "How often do you eat the following animal-based products? Beef:"
     *                  - "How often do you eat the following animal-based products? Pork:"
     *                  - "How often do you eat the following animal-based products? Chicken:"
     *                  - "How often do you eat the following animal-based products? Fish/Seafood:"
     *                  - "What type of home do you live in?"
     *                  - "What is the size of your home?"
     *                  - "What is your average monthly electricity bill?"
     *                  - "How many people live in your household?"
     *                  - "What type of energy do you use to heat your home?"
     *                  - "What type of energy do you use to heat water?"
     *                  - "Do you use any renewable energy sources for electricity or heating (e.g., solar, wind)?"
     *                  - "How often do you buy new clothes?"
     *                  - "How many electronic devices (phones, laptops, etc.) have you purchased in the past year?"
     *                  - "How often do you recycle?"
     *                  - "Do you buy second-hand or eco-friendly products?"
     * @return A map of category names and their respective carbon footprint values.
     */
    public HashMap<String, Double> calculatePerCategoryEmission(HashMap<String, String> responses) {
        HashMap<String, Double> categoryEmissions = new HashMap<>();

        Log.d("YearlyTotalCarbonFootprintCalculator", "Calculating driving emissions");
        categoryEmissions.put("DrivingEmissions", drivingCalculator.calculateYearlyFootprint(responses));

        Log.d("YearlyTotalCarbonFootprintCalculator", "Calculating public transport emissions");
        categoryEmissions.put("PublicTransportEmissions", publicTransportCalculator.calculateYearlyFootprint(responses));

        Log.d("YearlyTotalCarbonFootprintCalculator", "Calculating flight emissions");
        categoryEmissions.put("FlightEmissions", flightCalculator.calculateYearlyFootprint(responses));

        Log.d("YearlyTotalCarbonFootprintCalculator", "Calculating housing emissions");
        categoryEmissions.put("HousingEmissions", housingCalculator.calculateYearlyFootprint(responses));

        Log.d("YearlyTotalCarbonFootprintCalculator", "Calculating food emissions");
        categoryEmissions.put("FoodEmissions", foodCalculator.calculateYearlyFootprint(responses));

        Log.d("YearlyTotalCarbonFootprintCalculator", "Calculating consumption emissions");
        categoryEmissions.put("ConsumptionEmissions", consumptionCalculator.calculateYearlyFootprint(responses));

        return categoryEmissions;
    }

    /**
     * Calculates the total carbon footprint by summing up emissions from all categories.
     *
     * @param responses A map containing user responses for all survey questions.
     *                   The map must contain keys such as:
     *                  - "Do you own or regularly use a car?"
     *                  - "What type of car do you drive?"
     *                  - "How many kilometers/miles do you drive per year?"
     *                  - "How often do you use public transportation (bus, train, subway)?"
     *                  - "How much time do you spend on public transport per week (bus, train, subway)?"
     *                  - "How many short-haul flights (less than 1,500 km / 932 miles) have you taken in the past year?"
     *                  - "How many long-haul flights (more than 1,500 km / 932 miles) have you taken in the past year?"
     *                  - "What best describes your diet?"
     *                  - "How often do you eat the following animal-based products? Beef:"
     *                  - "How often do you eat the following animal-based products? Pork:"
     *                  - "How often do you eat the following animal-based products? Chicken:"
     *                  - "How often do you eat the following animal-based products? Fish/Seafood:"
     *                  - "What type of home do you live in?"
     *                  - "What is the size of your home?"
     *                  - "What is your average monthly electricity bill?"
     *                  - "How many people live in your household?"
     *                  - "What type of energy do you use to heat your home?"
     *                  - "What type of energy do you use to heat water?"
     *                  - "Do you use any renewable energy sources for electricity or heating (e.g., solar, wind)?"
     *                  - "How often do you buy new clothes?"
     *                  - "How many electronic devices (phones, laptops, etc.) have you purchased in the past year?"
     *                  - "How often do you recycle?"
     *                  - "Do you buy second-hand or eco-friendly products?"
     * @return The total yearly carbon footprint in kg CO2.
     */
    public double calculateTotalEmissions(HashMap<String, String> responses) {
        double totalEmissions = 0.0;

        // Add contributions from each category
        totalEmissions += drivingCalculator.calculateYearlyFootprint(responses);
        Log.d("YearlyTotalCarbonFootprintCalculator", "Driving Emissions: " + drivingCalculator.calculateYearlyFootprint(responses));

        totalEmissions += publicTransportCalculator.calculateYearlyFootprint(responses);
        Log.d("YearlyTotalCarbonFootprintCalculator", "Public Transport Emissions: " + publicTransportCalculator.calculateYearlyFootprint(responses));

        totalEmissions += flightCalculator.calculateYearlyFootprint(responses);
        Log.d("YearlyTotalCarbonFootprintCalculator", "Flight Emissions: " + flightCalculator.calculateYearlyFootprint(responses));

        totalEmissions += housingCalculator.calculateYearlyFootprint(responses);
        Log.d("YearlyTotalCarbonFootprintCalculator", "Housing Emissions: " + housingCalculator.calculateYearlyFootprint(responses));

        totalEmissions += foodCalculator.calculateYearlyFootprint(responses);
        Log.d("YearlyTotalCarbonFootprintCalculator", "Food Emissions: " + foodCalculator.calculateYearlyFootprint(responses));

        totalEmissions += consumptionCalculator.calculateYearlyFootprint(responses);
        Log.d("YearlyTotalCarbonFootprintCalculator", "Consumption Emissions: " + consumptionCalculator.calculateYearlyFootprint(responses));

        return totalEmissions;
    }
}

