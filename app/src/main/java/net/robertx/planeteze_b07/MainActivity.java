package net.robertx.planeteze_b07;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;

import net.robertx.planeteze_b07.CarbonFootprintCalculators.YearlyHousingCarbonFootprintCalculator;
import net.robertx.planeteze_b07.CarbonFootprintCalculators.YearlyTotalCarbonFootprintCalculator;
import net.robertx.planeteze_b07.DataRetrievers.EmissionsDataRetriever;
import net.robertx.planeteze_b07.DataRetrievers.HousingCO2DataRetriever;
import net.robertx.planeteze_b07.annualCarbonTracker.ResultsActivity;

import java.io.IOException;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private Button button;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
//        System.out.println("Hello, World!");
        try {
            // Initialize HousingCO2DataRetriever with context
            HousingCO2DataRetriever.initialize(this);

            // Create an instance of YearlyHousingCarbonFootprintCalculator
            YearlyHousingCarbonFootprintCalculator housingCalculator = new YearlyHousingCarbonFootprintCalculator();

            // Define user responses
            HashMap<String, String> responses = new HashMap<>();
            responses.put("What type of home do you live in?", "Detached");
            responses.put("What is the size of your home?", "Under-1000-sqft");
            responses.put("What is your average monthly electricity bill?", "Under-50-Dollars");
            responses.put("How many people live in your household?", "1-Occupant");
            responses.put("What type of energy do you use to heat your home?", "Wood");
            responses.put("What type of energy do you use to heat water?", "Electricity");
            responses.put("Do you use any renewable energy sources for electricity or heating?", "Yes, partially");

            // Calculate emissions
            double totalFootprint = housingCalculator.calculateYearlyFootprint(responses);

            // Output the result
            System.out.println("Total Yearly Housing  Carbon Footprint: " + totalFootprint);

        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            // Initialize the total calculator
            YearlyTotalCarbonFootprintCalculator totalCalculator = new YearlyTotalCarbonFootprintCalculator();

            // Create responses for the questionnaire
            HashMap<String, String> responses = new HashMap<>();

            // Transportation
            responses.put("Do you own or regularly use a car?", "Yes");
            responses.put("What type of car do you drive?", "gasoline");
            responses.put("How many kilometers/miles do you drive per year?", "Up to 5,000 km");
            responses.put("How often do you use public transportation (bus, train, subway)?", "Occasionally");
            responses.put("How much time do you spend on public transport per week (bus, train, subway)?", "Under 1 hour");
            responses.put("How many short-haul flights (less than 1,500 km / 932 miles) have you taken in the past year?", "1-2 flights");
            responses.put("How many long-haul flights (more than 1,500 km / 932 miles) have you taken in the past year?", "1-2 flights");
            System.out.println("Ok");

            // Food
            responses.put("What best describes your diet?", "Meat-based");
            responses.put("How often do you eat the following animal-based products? Beef:", "Frequently");
            responses.put("How often do you eat the following animal-based products? Pork:", "Occasionally");
            responses.put("How often do you eat the following animal-based products? Chicken:", "Daily");
            responses.put("How often do you eat the following animal-based products? Fish/Seafood:", "Frequently");
            responses.put("How often do you waste food or throw away uneaten leftovers?", "Occasionally");
            System.out.println("Ok2");
            // Housing
            responses.put("What type of home do you live in?", "Detached");
            responses.put("What is the size of your home?", "Under-1000-sqft");
            responses.put("What is your average monthly electricity bill?", "Under-50-Dollars");
            responses.put("How many people live in your household?", "1-Occupant");
            responses.put("What type of energy do you use to heat your home?", "Wood");
            responses.put("What type of energy do you use to heat water?", "Electricity");
            responses.put("Do you use any renewable energy sources for electricity or heating?", "Yes, partially");
            System.out.println("Ok3");
            // Consumption
            responses.put("How often do you buy new clothes?", "Monthly");
            responses.put("Do you buy second-hand or eco-friendly products?", "regularly");
            responses.put("How many electronic devices (phones, laptops, etc.) have you purchased in the past year?", "2");
            responses.put("How often do you recycle?", "Frequently");
            System.out.println("Ok4");

            // Calculate total emissions
            double totalEmissions = totalCalculator.calculateTotalEmissions(responses);

            // Log the result
            Log.d("MainActivity", "Total Emissions: " + totalEmissions);

        } catch (IOException e) {
            Log.e("MainActivity", "Error initializing YearlyTotalCarbonFootprintCalculator: " + e.getMessage());
        }

        // Initialize EmissionsDataRetriever
        try {
            EmissionsDataRetriever.initialize(this);
        } catch (IOException e) {
            Log.e(TAG, "Failed to initialize EmissionsDataRetriever", e);
        }

        // Rest of your code...

        EmissionsDataRetriever retriever = new EmissionsDataRetriever();

        // Retrieve emissions for a specific country
        String country = "Canada";
        double emissions = retriever.getEmissionValue(country);
        Log.d(TAG, "Emissions for " + country + ": " + emissions);

        button = findViewById(R.id.button22);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ResultsActivity.class);
                startActivity(intent);

            }
        });


        TextView usernameDisplay = findViewById(R.id.mainActivityUsernameDisplay);
        if (firebaseAuth.getCurrentUser() != null)
            usernameDisplay.setText("User ID: " + firebaseAuth.getCurrentUser().getUid() + "\n" + "Email: " + firebaseAuth.getCurrentUser().getEmail());


        findViewById(R.id.startAnnualCarbonFootprintSurvey).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AnnualCarbonFootprintSurvey.class);
            startActivity(intent);
        });
    }
}