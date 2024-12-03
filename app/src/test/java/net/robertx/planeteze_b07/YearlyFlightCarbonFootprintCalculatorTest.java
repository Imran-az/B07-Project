
package net.robertx.planeteze_b07;

import org.junit.Test;
import static org.junit.Assert.*;

import net.robertx.planeteze_b07.carbonFootprintCalculators.YearlyFlightCarbonFootprintCalculator;

import java.util.HashMap;

public class YearlyFlightCarbonFootprintCalculatorTest {

    @Test
    public void testNoFlights() {
        YearlyFlightCarbonFootprintCalculator calculator = new YearlyFlightCarbonFootprintCalculator();
        HashMap<String, String> responses = new HashMap<>();
        responses.put("How many short-haul flights (less than 1,500 km / 932 miles) have you taken in the past year?", "None");
        responses.put("How many long-haul flights (more than 1,500 km / 932 miles) have you taken in the past year?", "None");

        double result = calculator.calculateYearlyFootprint(responses);
        assertEquals(0.0, result, 0.01);
    }

    @Test
    public void testShortHaulOnly() {
        YearlyFlightCarbonFootprintCalculator calculator = new YearlyFlightCarbonFootprintCalculator();
        HashMap<String, String> responses = new HashMap<>();
        responses.put("How many short-haul flights (less than 1,500 km / 932 miles) have you taken in the past year?", "3-5 flights");
        responses.put("How many long-haul flights (more than 1,500 km / 932 miles) have you taken in the past year?", "None");

        double result = calculator.calculateYearlyFootprint(responses);
        assertEquals(600.0, result, 0.01);
    }

    @Test
    public void testLongHaulOnly() {
        YearlyFlightCarbonFootprintCalculator calculator = new YearlyFlightCarbonFootprintCalculator();
        HashMap<String, String> responses = new HashMap<>();
        responses.put("How many short-haul flights (less than 1,500 km / 932 miles) have you taken in the past year?", "None");
        responses.put("How many long-haul flights (more than 1,500 km / 932 miles) have you taken in the past year?", "6-10 flights");

        double result = calculator.calculateYearlyFootprint(responses);
        assertEquals(4400.0, result, 0.01);
    }

    @Test
    public void testShortAndLongHaulFlights() {
        YearlyFlightCarbonFootprintCalculator calculator = new YearlyFlightCarbonFootprintCalculator();
        HashMap<String, String> responses = new HashMap<>();
        responses.put("How many short-haul flights (less than 1,500 km / 932 miles) have you taken in the past year?", "1-2 flights");
        responses.put("How many long-haul flights (more than 1,500 km / 932 miles) have you taken in the past year?", "3-5 flights");

        double result = calculator.calculateYearlyFootprint(responses);
        assertEquals(225.0 + 2200.0, result, 0.01);
    }

    @Test
    public void testMissingShortHaulKey() {
        YearlyFlightCarbonFootprintCalculator calculator = new YearlyFlightCarbonFootprintCalculator();
        HashMap<String, String> responses = new HashMap<>();
        // Missing short-haul key
        responses.put("How many long-haul flights (more than 1,500 km / 932 miles) have you taken in the past year?", "1-2 flights");

        double result = calculator.calculateYearlyFootprint(responses);
        assertEquals(0.0, result, 0.01);
    }

    @Test
    public void testMissingLongHaulKey() {
        YearlyFlightCarbonFootprintCalculator calculator = new YearlyFlightCarbonFootprintCalculator();
        HashMap<String, String> responses = new HashMap<>();
        responses.put("How many short-haul flights (less than 1,500 km / 932 miles) have you taken in the past year?", "3-5 flights");
        // Missing long-haul key

        double result = calculator.calculateYearlyFootprint(responses);
        assertEquals(0.0, result, 0.01);
    }

    @Test
    public void testInvalidShortHaulInput() {
        YearlyFlightCarbonFootprintCalculator calculator = new YearlyFlightCarbonFootprintCalculator();
        HashMap<String, String> responses = new HashMap<>();
        responses.put("How many short-haul flights (less than 1,500 km / 932 miles) have you taken in the past year?", "Invalid Input");
        responses.put("How many long-haul flights (more than 1,500 km / 932 miles) have you taken in the past year?", "1-2 flights");

        try {
            calculator.calculateYearlyFootprint(responses);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertEquals("Invalid short-haul flights input: Invalid Input", e.getMessage());
        }
    }

    @Test
    public void testInvalidLongHaulInput() {
        YearlyFlightCarbonFootprintCalculator calculator = new YearlyFlightCarbonFootprintCalculator();
        HashMap<String, String> responses = new HashMap<>();
        responses.put("How many short-haul flights (less than 1,500 km / 932 miles) have you taken in the past year?", "None");
        responses.put("How many long-haul flights (more than 1,500 km / 932 miles) have you taken in the past year?", "Invalid Input");

        try {
            calculator.calculateYearlyFootprint(responses);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertEquals("Invalid long-haul flights input: Invalid Input", e.getMessage());
        }
    }

    @Test
    public void testMissingBothKeys() {
        YearlyFlightCarbonFootprintCalculator calculator = new YearlyFlightCarbonFootprintCalculator();
        HashMap<String, String> responses = new HashMap<>();
        // Both keys missing

        double result = calculator.calculateYearlyFootprint(responses);
        assertEquals(0.0, result, 0.01);
    }

    @Test
    public void testMaxShortAndLongHaulFlights() {
        YearlyFlightCarbonFootprintCalculator calculator = new YearlyFlightCarbonFootprintCalculator();
        HashMap<String, String> responses = new HashMap<>();
        responses.put("How many short-haul flights (less than 1,500 km / 932 miles) have you taken in the past year?", "More than 10 flights");
        responses.put("How many long-haul flights (more than 1,500 km / 932 miles) have you taken in the past year?", "More than 10 flights");

        double result = calculator.calculateYearlyFootprint(responses);
        assertEquals(1800.0 + 6600.0, result, 0.01);
    }
}
