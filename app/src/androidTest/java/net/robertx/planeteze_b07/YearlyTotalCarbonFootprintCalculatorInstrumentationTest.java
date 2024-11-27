package net.robertx.planeteze_b07;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.HashMap;

import static org.junit.Assert.*;

import net.robertx.planeteze_b07.CarbonFootprintCalculators.YearlyTotalCarbonFootprintCalculator;
import net.robertx.planeteze_b07.DataRetrievers.HousingCO2DataRetriever;

@RunWith(AndroidJUnit4.class)
public class YearlyTotalCarbonFootprintCalculatorInstrumentationTest {

    private Context context;
    private YearlyTotalCarbonFootprintCalculator totalCalculator;

    @Before
    public void setUp() throws IOException {
        // Use the context of the app under test
        context = InstrumentationRegistry.getInstrumentation().getTargetContext();

        // Verify the context is not null
        assertNotNull("Context is null", context);

        // Initialize HousingCO2DataRetriever with the app context
        HousingCO2DataRetriever.initialize(context);

        // Initialize the total calculator
        totalCalculator = new YearlyTotalCarbonFootprintCalculator();
    }

    @Test
    public void testCalculateTotalEmissions() throws IOException {
        // Create responses for the calculator
        HashMap<String, String> responses = new HashMap<>();

        // Populate sample responses
        responses.put("Do you own or regularly use a car?", "Yes");
        responses.put("What type of car do you drive?", "gasoline");
        responses.put("How many kilometers/miles do you drive per year?", "Up to 5,000 km");
        responses.put("How often do you use public transportation (bus, train, subway)?", "Occasionally");
        responses.put("How much time do you spend on public transport per week (bus, train, subway)?", "Under 1 hour");
        responses.put("How many short-haul flights (less than 1,500 km / 932 miles) have you taken in the past year?", "1-2 flights");
        responses.put("How many long-haul flights (more than 1,500 km / 932 miles) have you taken in the past year?", "1-2 flights");
        responses.put("What best describes your diet?", "Meat-based");
        responses.put("How often do you eat the following animal-based products? Beef:", "Frequently");
        responses.put("How often do you eat the following animal-based products? Pork:", "Occasionally");
        responses.put("How often do you eat the following animal-based products? Chicken:", "Daily");
        responses.put("How often do you eat the following animal-based products? Fish/Seafood:", "Frequently");
        responses.put("How often do you waste food or throw away uneaten leftovers?", "Occasionally");
        responses.put("What type of home do you live in?", "Detached");
        responses.put("What is the size of your home?", "Under-1000-sqft");
        responses.put("What is your average monthly electricity bill?", "Under-50-Dollars");
        responses.put("How many people live in your household?", "1-Occupant");
        responses.put("What type of energy do you use to heat your home?", "Wood");
        responses.put("What type of energy do you use to heat water?", "Electricity");
        responses.put("Do you use any renewable energy sources for electricity or heating?", "Yes, partially");
        responses.put("How often do you buy new clothes?", "Monthly");
        responses.put("Do you buy second-hand or eco-friendly products?", "Regularly");
        responses.put("How many electronic devices (phones, laptops, etc.) have you purchased in the past year?", "2");
        responses.put("How often do you recycle?", "Frequently");

        // Calculate total emissions
        double totalEmissions = totalCalculator.calculateTotalEmissions(responses);

        // Assert the calculated result
        double expectedEmissions = 5175.2; // Replace with the correct expected value
        assertEquals("Calculated emissions do not match expected value", expectedEmissions, totalEmissions, 0.1);
    }
}
