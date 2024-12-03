package net.robertx.planeteze_b07;

import org.junit.Test;
import static org.junit.Assert.*;

import net.robertx.planeteze_b07.carbonFootprintCalculators.YearlyDrivingCarbonFootprintCalculator;

import java.util.HashMap;

public class YearlyDrivingCarbonFootprintCalculatorTest {

    @Test
    public void testNoCarUsage() {
        YearlyDrivingCarbonFootprintCalculator calculator = new YearlyDrivingCarbonFootprintCalculator();
        HashMap<String, String> responses = new HashMap<>();
        responses.put("Do you own or regularly use a car?", "No");

        double result = calculator.calculateYearlyFootprint(responses);

        assertEquals(0.0, result, 0.01); // No car usage should result in 0 footprint
    }

    // Test all car types with all distance ranges
    @Test
    public void testCarTypeAndDistanceCombinations() {
        String[] carTypes = {"Gasoline", "Diesel", "Hybrid", "Electric", "I don't know"};
        String[] distances = {
                "Up to 5,000 km (3,000 miles)",
                "5,000–10,000 km (3,000–6,000 miles)",
                "10,000–15,000 km (6,000–9,000 miles)",
                "15,000–20,000 km (9,000–12,000 miles)",
                "20,000–25,000 km (12,000–15,000 miles)",
                "More than 25,000 km (15,000 miles)"
        };

        double[] expectedEmissionFactors = {0.24, 0.27, 0.16, 0.05, 0.1};
        int[] distanceValues = {5000, 10000, 15000, 20000, 25000, 35000};

        for (int i = 0; i < carTypes.length; i++) {
            for (int j = 0; j < distances.length; j++) {
                YearlyDrivingCarbonFootprintCalculator calculator = new YearlyDrivingCarbonFootprintCalculator();
                HashMap<String, String> responses = new HashMap<>();
                responses.put("Do you own or regularly use a car?", "Yes");
                responses.put("What type of car do you drive?", carTypes[i]);
                responses.put("How many kilometers/miles do you drive per year?", distances[j]);

                double result = calculator.calculateYearlyFootprint(responses);

                double expected = expectedEmissionFactors[i] * distanceValues[j];
                assertEquals("Failed for carType=" + carTypes[i] + " and distance=" + distances[j], expected, result, 0.01);
            }
        }
    }

    // Test invalid distance input
    @Test
    public void testInvalidDistance() {
        YearlyDrivingCarbonFootprintCalculator calculator = new YearlyDrivingCarbonFootprintCalculator();
        HashMap<String, String> responses = new HashMap<>();
        responses.put("Do you own or regularly use a car?", "Yes");
        responses.put("What type of car do you drive?", "Gasoline");
        responses.put("How many kilometers/miles do you drive per year?", "Invalid Distance");

        double result = calculator.calculateYearlyFootprint(responses);

        assertEquals(0.0, result, 0.01); // Invalid distance should result in 0 footprint
    }

    // Test missing car type
    @Test
    public void testMissingCarType() {
        YearlyDrivingCarbonFootprintCalculator calculator = new YearlyDrivingCarbonFootprintCalculator();
        HashMap<String, String> responses = new HashMap<>();
        responses.put("Do you own or regularly use a car?", "Yes");
        responses.put("How many kilometers/miles do you drive per year?", "5,000–10,000 km (3,000–6,000 miles)");

        double result = calculator.calculateYearlyFootprint(responses);

        assertEquals(0.0, result, 0.01); // Missing car type should result in 0 footprint
    }

    // Test missing distance
    @Test
    public void testMissingDistance() {
        YearlyDrivingCarbonFootprintCalculator calculator = new YearlyDrivingCarbonFootprintCalculator();
        HashMap<String, String> responses = new HashMap<>();
        responses.put("Do you own or regularly use a car?", "Yes");
        responses.put("What type of car do you drive?", "Gasoline");

        double result = calculator.calculateYearlyFootprint(responses);

        assertEquals(0.0, result, 0.01); // Missing distance should result in 0 footprint
    }

    // Test missing car usage
    @Test
    public void testMissingCarUsage() {
        YearlyDrivingCarbonFootprintCalculator calculator = new YearlyDrivingCarbonFootprintCalculator();
        HashMap<String, String> responses = new HashMap<>();
        responses.put("What type of car do you drive?", "Gasoline");
        responses.put("How many kilometers/miles do you drive per year?", "5,000–10,000 km (3,000–6,000 miles)");

        double result = calculator.calculateYearlyFootprint(responses);

        assertEquals(0.0, result, 0.01); // Missing car usage should result in 0 footprint
    }
}
