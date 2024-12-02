package net.robertx.planeteze_b07;

import org.junit.Test;
import static org.junit.Assert.*;

import net.robertx.planeteze_b07.CarbonFootprintCalculators.YearlyPublicTransportationCarbonFootprintCalculator;

import java.util.HashMap;

public class YearlyPublicTransportationCarbonFootprintCalculatorTest {

    @Test
    public void testNeverUsePublicTransport() {
        YearlyPublicTransportationCarbonFootprintCalculator calculator = new YearlyPublicTransportationCarbonFootprintCalculator();
        HashMap<String, String> responses = new HashMap<>();
        responses.put("How often do you use public transportation (bus, train, subway)?", "Never");
        // No time response needed as frequency is "Never"

        double result = calculator.calculateYearlyFootprint(responses);
        assertEquals(0.0, result, 0.01);
    }

    @Test
    public void testOccasionallyUnderOneHour() {
        YearlyPublicTransportationCarbonFootprintCalculator calculator = new YearlyPublicTransportationCarbonFootprintCalculator();
        HashMap<String, String> responses = new HashMap<>();
        responses.put("How often do you use public transportation (bus, train, subway)?", "Occasionally (1-2 times/week)");
        responses.put("How much time do you spend on public transport per week (bus, train, subway)?", "Under 1 hour");

        double result = calculator.calculateYearlyFootprint(responses);
        assertEquals(246, result, 0.01);
    }

    @Test
    public void testOccasionallyOneToThreeHours() {
        YearlyPublicTransportationCarbonFootprintCalculator calculator = new YearlyPublicTransportationCarbonFootprintCalculator();
        HashMap<String, String> responses = new HashMap<>();
        responses.put("How often do you use public transportation (bus, train, subway)?", "Occasionally (1-2 times/week)");
        responses.put("How much time do you spend on public transport per week (bus, train, subway)?", "1-3 hours");

        double result = calculator.calculateYearlyFootprint(responses);
        assertEquals(819, result, 0.01);
    }

    @Test
    public void testOccasionallyThreeToFiveHours() {
        YearlyPublicTransportationCarbonFootprintCalculator calculator = new YearlyPublicTransportationCarbonFootprintCalculator();
        HashMap<String, String> responses = new HashMap<>();
        responses.put("How often do you use public transportation (bus, train, subway)?", "Occasionally (1-2 times/week)");
        responses.put("How much time do you spend on public transport per week (bus, train, subway)?", "3-5 hours");

        double result = calculator.calculateYearlyFootprint(responses);
        assertEquals(1638, result, 0.01);
    }

    @Test
    public void testOccasionallyFiveToTenHours() {
        YearlyPublicTransportationCarbonFootprintCalculator calculator = new YearlyPublicTransportationCarbonFootprintCalculator();
        HashMap<String, String> responses = new HashMap<>();
        responses.put("How often do you use public transportation (bus, train, subway)?", "Occasionally (1-2 times/week)");
        responses.put("How much time do you spend on public transport per week (bus, train, subway)?", "5-10 hours");

        double result = calculator.calculateYearlyFootprint(responses);
        assertEquals(3071, result, 0.01);
    }

    @Test
    public void testOccasionallyMoreThanTenHours() {
        YearlyPublicTransportationCarbonFootprintCalculator calculator = new YearlyPublicTransportationCarbonFootprintCalculator();
        HashMap<String, String> responses = new HashMap<>();
        responses.put("How often do you use public transportation (bus, train, subway)?", "Occasionally (1-2 times/week)");
        responses.put("How much time do you spend on public transport per week (bus, train, subway)?", "More than 10 hours");

        double result = calculator.calculateYearlyFootprint(responses);
        assertEquals(4095, result, 0.01);
    }

    @Test
    public void testFrequentlyUnderOneHour() {
        YearlyPublicTransportationCarbonFootprintCalculator calculator = new YearlyPublicTransportationCarbonFootprintCalculator();
        HashMap<String, String> responses = new HashMap<>();
        responses.put("How often do you use public transportation (bus, train, subway)?", "Frequently (3-4 times/week)");
        responses.put("How much time do you spend on public transport per week (bus, train, subway)?", "Under 1 hour");

        double result = calculator.calculateYearlyFootprint(responses);
        assertEquals(573, result, 0.01);
    }

    @Test
    public void testFrequentlyOneToThreeHours() {
        YearlyPublicTransportationCarbonFootprintCalculator calculator = new YearlyPublicTransportationCarbonFootprintCalculator();
        HashMap<String, String> responses = new HashMap<>();
        responses.put("How often do you use public transportation (bus, train, subway)?", "Frequently (3-4 times/week)");
        responses.put("How much time do you spend on public transport per week (bus, train, subway)?", "1-3 hours");

        double result = calculator.calculateYearlyFootprint(responses);
        assertEquals(1911, result, 0.01);
    }

    @Test
    public void testFrequentlyThreeToFiveHours() {
        YearlyPublicTransportationCarbonFootprintCalculator calculator = new YearlyPublicTransportationCarbonFootprintCalculator();
        HashMap<String, String> responses = new HashMap<>();
        responses.put("How often do you use public transportation (bus, train, subway)?", "Frequently (3-4 times/week)");
        responses.put("How much time do you spend on public transport per week (bus, train, subway)?", "3-5 hours");

        double result = calculator.calculateYearlyFootprint(responses);
        assertEquals(3822, result, 0.01);
    }

    @Test
    public void testFrequentlyFiveToTenHours() {
        YearlyPublicTransportationCarbonFootprintCalculator calculator = new YearlyPublicTransportationCarbonFootprintCalculator();
        HashMap<String, String> responses = new HashMap<>();
        responses.put("How often do you use public transportation (bus, train, subway)?", "Frequently (3-4 times/week)");
        responses.put("How much time do you spend on public transport per week (bus, train, subway)?", "5-10 hours");

        double result = calculator.calculateYearlyFootprint(responses);
        assertEquals(7166, result, 0.01);
    }

    @Test
    public void testFrequentlyMoreThanTenHours() {
        YearlyPublicTransportationCarbonFootprintCalculator calculator = new YearlyPublicTransportationCarbonFootprintCalculator();
        HashMap<String, String> responses = new HashMap<>();
        responses.put("How often do you use public transportation (bus, train, subway)?", "Frequently (3-4 times/week)");
        responses.put("How much time do you spend on public transport per week (bus, train, subway)?", "More than 10 hours");

        double result = calculator.calculateYearlyFootprint(responses);
        assertEquals(9555, result, 0.01);
    }

    @Test
    public void testAlwaysUnderOneHour() {
        YearlyPublicTransportationCarbonFootprintCalculator calculator = new YearlyPublicTransportationCarbonFootprintCalculator();
        HashMap<String, String> responses = new HashMap<>();
        responses.put("How often do you use public transportation (bus, train, subway)?", "Always (5+ times/week)");
        responses.put("How much time do you spend on public transport per week (bus, train, subway)?", "Under 1 hour");

        double result = calculator.calculateYearlyFootprint(responses);
        assertEquals(573, result, 0.01);
    }

    @Test
    public void testAlwaysOneToThreeHours() {
        YearlyPublicTransportationCarbonFootprintCalculator calculator = new YearlyPublicTransportationCarbonFootprintCalculator();
        HashMap<String, String> responses = new HashMap<>();
        responses.put("How often do you use public transportation (bus, train, subway)?", "Always (5+ times/week)");
        responses.put("How much time do you spend on public transport per week (bus, train, subway)?", "1-3 hours");

        double result = calculator.calculateYearlyFootprint(responses);
        assertEquals(1911, result, 0.01);
    }

    @Test
    public void testAlwaysThreeToFiveHours() {
        YearlyPublicTransportationCarbonFootprintCalculator calculator = new YearlyPublicTransportationCarbonFootprintCalculator();
        HashMap<String, String> responses = new HashMap<>();
        responses.put("How often do you use public transportation (bus, train, subway)?", "Always (5+ times/week)");
        responses.put("How much time do you spend on public transport per week (bus, train, subway)?", "3-5 hours");

        double result = calculator.calculateYearlyFootprint(responses);
        assertEquals(3822, result, 0.01);
    }

    @Test
    public void testAlwaysFiveToTenHours() {
        YearlyPublicTransportationCarbonFootprintCalculator calculator = new YearlyPublicTransportationCarbonFootprintCalculator();
        HashMap<String, String> responses = new HashMap<>();
        responses.put("How often do you use public transportation (bus, train, subway)?", "Always (5+ times/week)");
        responses.put("How much time do you spend on public transport per week (bus, train, subway)?", "5-10 hours");

        double result = calculator.calculateYearlyFootprint(responses);
        assertEquals(7166, result, 0.01);
    }

    @Test
    public void testAlwaysMoreThanTenHours() {
        YearlyPublicTransportationCarbonFootprintCalculator calculator = new YearlyPublicTransportationCarbonFootprintCalculator();
        HashMap<String, String> responses = new HashMap<>();
        responses.put("How often do you use public transportation (bus, train, subway)?", "Always (5+ times/week)");
        responses.put("How much time do you spend on public transport per week (bus, train, subway)?", "More than 10 hours");

        double result = calculator.calculateYearlyFootprint(responses);
        assertEquals(9555, result, 0.01);
    }

}
