package net.robertx.planeteze_b07;

import org.junit.Test;

import static org.junit.Assert.*;

import net.robertx.planeteze_b07.CarbonFootprintCalculators.YearlyConsumptionFootprintCalculator;

import java.util.HashMap;

public class YearlyConsumptionFootprintCalculatorTest {

    @Test
    public void testValidInputsMonthlyEcoFriendlyAlwaysRecycle() {
        YearlyConsumptionFootprintCalculator calculator = new YearlyConsumptionFootprintCalculator();
        HashMap<String, String> responses = new HashMap<>();
        responses.put("How often do you buy new clothes?", "Monthly");
        responses.put("Do you buy second-hand or eco-friendly products?", "Yes, regularly");
        responses.put("How many electronic devices (phones, laptops, etc.) have you purchased in the past year?", "2");
        responses.put("How often do you recycle?", "Always");

        double result = calculator.calculateYearlyFootprint(responses);
        assertEquals(600.0, result, 0.01);
    }

    @Test
    public void testValidInputsRarelyNoEcoFriendlyNeverRecycle() {
        YearlyConsumptionFootprintCalculator calculator = new YearlyConsumptionFootprintCalculator();
        HashMap<String, String> responses = new HashMap<>();
        responses.put("How often do you buy new clothes?", "Rarely");
        responses.put("Do you buy second-hand or eco-friendly products?", "No");
        responses.put("How many electronic devices (phones, laptops, etc.) have you purchased in the past year?", "None");
        responses.put("How often do you recycle?", "Never");

        double result = calculator.calculateYearlyFootprint(responses);
        assertEquals(5.0, result, 0.01);
    }

    @Test
    public void testMissingClothesFrequency() {
        YearlyConsumptionFootprintCalculator calculator = new YearlyConsumptionFootprintCalculator();
        HashMap<String, String> responses = new HashMap<>();
        responses.put("Do you buy second-hand or eco-friendly products?", "No");
        responses.put("How many electronic devices (phones, laptops, etc.) have you purchased in the past year?", "2");
        responses.put("How often do you recycle?", "Frequently");

        double result = calculator.calculateYearlyFootprint(responses);
        assertEquals(0.0, result, 0.01);
    }

    @Test
    public void testMissingRecyclingFrequency() {
        YearlyConsumptionFootprintCalculator calculator = new YearlyConsumptionFootprintCalculator();
        HashMap<String, String> responses = new HashMap<>();
        responses.put("How often do you buy new clothes?", "Quarterly");
        responses.put("Do you buy second-hand or eco-friendly products?", "Yes, occasionally");
        responses.put("How many electronic devices (phones, laptops, etc.) have you purchased in the past year?", "1");

        double result = calculator.calculateYearlyFootprint(responses);
        assertEquals(0.0, result, 0.01);
    }

    @Test
    public void testInvalidClothesFrequency() {
        YearlyConsumptionFootprintCalculator calculator = new YearlyConsumptionFootprintCalculator();
        HashMap<String, String> responses = new HashMap<>();
        responses.put("How often do you buy new clothes?", "Weekly"); // Invalid
        responses.put("Do you buy second-hand or eco-friendly products?", "No");
        responses.put("How many electronic devices (phones, laptops, etc.) have you purchased in the past year?", "1");
        responses.put("How often do you recycle?", "Frequently");

        try {
            calculator.calculateYearlyFootprint(responses);
            fail("Expected IllegalArgumentException for invalid clothes frequency.");
        } catch (IllegalArgumentException e) {
            assertEquals("Invalid clothes frequency: Weekly", e.getMessage());
        }
    }

    @Test
    public void testInvalidDevicesPurchased() {
        YearlyConsumptionFootprintCalculator calculator = new YearlyConsumptionFootprintCalculator();
        HashMap<String, String> responses = new HashMap<>();
        responses.put("How often do you buy new clothes?", "Monthly");
        responses.put("Do you buy second-hand or eco-friendly products?", "No");
        responses.put("How many electronic devices (phones, laptops, etc.) have you purchased in the past year?", "10"); // Invalid
        responses.put("How often do you recycle?", "Frequently");

        try {
            calculator.calculateYearlyFootprint(responses);
            fail("Expected IllegalArgumentException for invalid devices purchased.");
        } catch (IllegalArgumentException e) {
            assertEquals("Invalid devices purchased: 10", e.getMessage());
        }
    }

    @Test
    public void testAllMissingResponses() {
        YearlyConsumptionFootprintCalculator calculator = new YearlyConsumptionFootprintCalculator();
        HashMap<String, String> responses = new HashMap<>();

        double result = calculator.calculateYearlyFootprint(responses);
        assertEquals(0.0, result, 0.01);
    }

    @Test
    public void testPartialMissingResponses() {
        YearlyConsumptionFootprintCalculator calculator = new YearlyConsumptionFootprintCalculator();
        HashMap<String, String> responses = new HashMap<>();
        responses.put("How often do you buy new clothes?", "Annually");

        double result = calculator.calculateYearlyFootprint(responses);
        assertEquals(0.0, result, 0.01);
    }
}
