package net.robertx.planeteze_b07.annualCarbonTracker;

import android.graphics.Color;
import android.os.Bundle;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import net.robertx.planeteze_b07.carbonFootprintCalculators.YearlyTotalCarbonFootprintCalculator;
import net.robertx.planeteze_b07.dataRetrievers.EmissionsDataRetriever;
import net.robertx.planeteze_b07.dataRetrievers.HousingCO2DataRetriever;
import net.robertx.planeteze_b07.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ResultsActivity extends AppCompatActivity {
    private PieChart pieChart;
    private BarChart barChart;
    private TextView comparisonText;
    private GridLayout sharedLegendContainer;

    private Button compareButton;
    private EditText countryEditText;

    private BarChart comparisonBarChart;
    private TextView comparisonResult;
    private EditText editTextCountry;
    private Button compareSubmitButton;

    final FirebaseAuth mAuth = FirebaseAuth.getInstance();

    FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_results);

        findViewById(R.id.returnHome).setOnClickListener(v -> finish());

        // Apply insets for system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainScrollView), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        firestore = FirebaseFirestore.getInstance();
        try {
            EmissionsDataRetriever.initialize(this);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to initialize EmissionsDataRetriever.");
        }

        // Initialize HousingCO2DataRetriever
        try {
            HousingCO2DataRetriever.initialize(this);
        } catch (IOException e) {
            e.printStackTrace();
        }

        firestore = FirebaseFirestore.getInstance();

        // Initialize views
        initializeViews();

        // Fetch and process data from Firebase
        fetchDataFromFirebase();
        fetchTotalEmissions(totalEmissions -> {
            // Hide the chart initially
            comparisonBarChart.setVisibility(View.GONE);

            // Set up button click logic
            compareSubmitButton.setOnClickListener(v -> {
                String country = editTextCountry.getText().toString().trim();
                if (!country.isEmpty()) {

                    compareWithCountryEmissions(country, totalEmissions);
                } else {
                    comparisonResult.setText("Please enter a valid country name.");
                    comparisonResult.setVisibility(View.VISIBLE);
                    comparisonBarChart.setVisibility(View.GONE);
                }
            });

        });
    }

    /**
     * Initializes the UI components used in the activity.
     */

    private void initializeViews() {
        comparisonBarChart = findViewById(R.id.comparisonBarChart);
        comparisonResult = findViewById(R.id.comparisonResult);
        editTextCountry = findViewById(R.id.editTextCountry);
        compareSubmitButton = findViewById(R.id.compareSubmitButton);
        pieChart = findViewById(R.id.pieChart);
        barChart = findViewById(R.id.barChart);
        sharedLegendContainer = findViewById(R.id.sharedLegendContainer);

    }




    /**
     * Fetches the user's total annual carbon emissions from Firebase Firestore and invokes a callback with the result.
     * The method retrieves the user's saved survey data from the "AnnualCarbonFootprintSurveyData" Firestore collection,
     * parses the data into category-specific emissions, calculates the total emissions, and converts it to tons.
     * <br>
     * If the data retrieval or parsing fails, the method throws a runtime exception with detailed error information.
     *
     * @param callback A callback interface {@link DataFetchCallback} that processes the total emissions value
     *                 once the data is successfully fetched and calculated. The value is passed in tons.
     */
    private void fetchTotalEmissions(DataFetchCallback callback) {
        // Reference to the Firestore collection containing the user's carbon footprint data
        CollectionReference annualCarbonFootprintSurveyDataRef = firestore.collection("AnnualCarbonFootprintSurveyData");

        // Fetch the document associated with the current user's unique ID
        annualCarbonFootprintSurveyDataRef.document(mAuth.getUid()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    try {
                        // Retrieve and parse the Firestore data into a usable format
                        Map<String, Object> data = document.getData();
                        HashMap<String, String> parsedResponses = new HashMap<>();
                        for (String key : data.keySet()) {
                            parsedResponses.put(key, data.get(key).toString());
                        }

                        // Calculate emissions for each category using the parsed responses
                        HashMap<String, Double> categoryEmissions = new YearlyTotalCarbonFootprintCalculator()
                                .calculatePerCategoryEmission(parsedResponses);

                        // Calculate the total emissions in kilograms and convert to tons
                        double totalKg = categoryEmissions.values().stream().mapToDouble(Double::doubleValue).sum();
                        double totalTons = totalKg; // Convert kg to tons

                        // Pass the calculated total emissions to the callback
                        callback.onDataFetched(totalTons);
                    } catch (IOException e) {
                        // Handle parsing errors and throw a runtime exception
                        throw new RuntimeException("Parsing Firestore data failed", e);
                    }
                }
            }
        });
    }


    /**
     * Compares the user's total emissions with the average emissions of a specified country.
     * The method retrieves the average emission value for the specified country, formats the country name to ensure proper capitalization,
     * and determines whether the user's emissions are higher or lower than the country's average.
     * <br>
     * The result of the comparison is displayed in the `comparisonText` view with a clear message about the difference.
     * If no valid emission data is available for the specified country, an appropriate error message is displayed.
     *
     * @param country       The name of the country to compare the user's emissions with.
     *                      The input is case-insensitive and will be formatted with the first letter capitalized.
     * @param userEmissionsKg The total emissions of the user in tons. This value will be compared to the country's average.
     */
    private void compareWithCountryEmissions(String country, double userEmissionsKg) {
        EmissionsDataRetriever dataRetriever = new EmissionsDataRetriever();

        try {
            // Format the country name
            String formattedCountry = country.substring(0, 1).toUpperCase() + country.substring(1).toLowerCase();

            // Retrieve country emissions in tons and convert to kilograms
            double countryEmissionsKg = dataRetriever.getEmissionValue(formattedCountry) * 1000;

            if (countryEmissionsKg > 0) {
                // Calculate percentage difference
                double percentageDifference = ((userEmissionsKg - countryEmissionsKg) / countryEmissionsKg) * 100;

                // Create a comparison message
                String message;
                if (userEmissionsKg > countryEmissionsKg) {
                    message = String.format(Locale.getDefault(), "Your emissions are %.2f%% higher than %s's average of %.2f kg.",
                            percentageDifference, formattedCountry, countryEmissionsKg);
                } else {
                    message = String.format(Locale.getDefault(), "Your emissions are %.2f%% lower than %s's average of %.2f kg.",
                            Math.abs(percentageDifference), formattedCountry, countryEmissionsKg);
                }

                // Update the comparison result
                comparisonResult.setText(message);
                comparisonResult.setVisibility(View.VISIBLE);

                // Populate and display the bar chart
                double[] emissions = {userEmissionsKg, countryEmissionsKg};
                String[] labels = {"Yours (Kg)", formattedCountry + " Avg (kg)"};
                updateComparisonBarChart(emissions, labels);
                comparisonBarChart.setVisibility(View.VISIBLE);
            } else {
                throw new IllegalArgumentException("No valid emission data available for " + formattedCountry);
            }
        } catch (IllegalArgumentException e) {
            comparisonResult.setText(String.format("Data for %s is not available.", country));
            comparisonResult.setVisibility(View.VISIBLE);
            comparisonBarChart.setVisibility(View.GONE);
        }
    }

    private void updateComparisonBarChart(double[] emissions, String[] labels) {
        // Create entries for the Bar Chart
        ArrayList<BarEntry> entries = new ArrayList<>();
        for (int i = 0; i < emissions.length; i++) {
            entries.add(new BarEntry(i, (float) emissions[i]));
        }

        // Create a BarDataSet with custom colors
        BarDataSet dataSet = new BarDataSet(entries, "Comparison");
        dataSet.setColors(Color.parseColor("#4CAF50"), Color.parseColor("#FF5722")); // Green and red
        dataSet.setValueTextSize(12f); // Adjust font size for bar values
        dataSet.setValueTextColor(Color.BLACK); // Set value text color

        // Configure the BarData
        BarData data = new BarData(dataSet);
        data.setBarWidth(0.8f); // Adjust bar width for a sleek look
        comparisonBarChart.setData(data);

        // Style the Bar Chart
        comparisonBarChart.getDescription().setEnabled(false); // Remove the "Description Label"
        comparisonBarChart.setFitBars(true); // Ensure bars are fitted nicely
        comparisonBarChart.getLegend().setEnabled(false); // Hide the legend
        comparisonBarChart.animateY(1000); // Add animation for a modern feel

        // Configure X-Axis
        XAxis xAxis = comparisonBarChart.getXAxis();
        xAxis.setGranularity(1f); // Ensure labels are spaced evenly
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM); // Position labels at the bottom
        xAxis.setDrawGridLines(false); // Remove grid lines
        xAxis.setTextSize(12f); // Adjust font size
        xAxis.setTextColor(Color.DKGRAY); // Set label color
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels)); // Set custom labels

        // Configure Y-Axis
        comparisonBarChart.getAxisLeft().setDrawGridLines(false); // Remove left-axis grid lines
        comparisonBarChart.getAxisLeft().setTextSize(12f); // Adjust font size
        comparisonBarChart.getAxisLeft().setTextColor(Color.DKGRAY); // Set text color
        comparisonBarChart.getAxisLeft().setAxisMinimum(0f); // Start at 0

        comparisonBarChart.getAxisRight().setEnabled(false); // Disable right Y-axis

        // Refresh the chart
        comparisonBarChart.invalidate();
    }


    /**
     * Callback interface for fetching data from Firebase.
     */
    public interface DataFetchCallback {
        void onDataFetched(double totalEmissions);
    }


    /**
     * Dynamically expands or collapses the breakdown section in the UI based on its current visibility state.
     * This method toggles the visibility of the `breakdownContainer` between `VISIBLE` and `GONE`.
     * When expanding, it calls {@link #expandSection(LinearLayout)} to dynamically populate the section with emission data.
     * When collapsing, it calls {@link #collapseSection(LinearLayout)} to hide the section and reset its layout.
     * <br>
     * A smooth transition animation is applied to enhance the user experience, leveraging the `TransitionManager`.
     *
     * @param view The triggering view (e.g., a button) that invokes this method.
     *             This view is used as the parent for the transition animation.
     */
    public void expand(View view) {
        // Find the container for the breakdown section
        LinearLayout breakdownContainer = findViewById(R.id.breakdownContainer);

        // Check the current visibility state of the breakdown section
        boolean isVisible = breakdownContainer.getVisibility() == View.VISIBLE;

        // Apply a smooth transition animation for layout changes
        TransitionManager.beginDelayedTransition((ViewGroup) view, new AutoTransition());

        // Toggle between expanding and collapsing the breakdown section
        if (isVisible) {
            // Collapse the breakdown section if it is currently visible
            collapseSection(breakdownContainer);
        } else {
            // Expand the breakdown section if it is currently hidden
            expandSection(breakdownContainer);
        }
    }


    /**
     * Collapses the breakdown section.
     * @param breakdownContainer The `LinearLayout` container to be collapsed.
     */
    private void collapseSection(LinearLayout breakdownContainer) {
        breakdownContainer.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0));
        breakdownContainer.setVisibility(View.GONE);
    }

    /**
     * Expands the breakdown section in the UI to dynamically display detailed emission data.
     * This method sets the visibility of the `breakdownContainer` to `VISIBLE`, animates its appearance,
     * and dynamically populates the container with emission data retrieved from Firebase Firestore.
     * <br>
     * The animation smoothly fades the section into view, and the layout is updated to wrap its content.
     * The breakdown section shows data for various categories such as "Consumption," "Driving," and more.
     *
     * @param breakdownContainer The `LinearLayout` container to be expanded. This container will hold
     *                           detailed emission data for each category after it is populated dynamically.
     */
    private void expandSection(LinearLayout breakdownContainer) {
        // Initialize the container's alpha for the fade-in animation
        breakdownContainer.setAlpha(0f);

        // Make the container visible before starting the animation
        breakdownContainer.setVisibility(View.VISIBLE);

        // Animate the fade-in effect for the breakdown section
        breakdownContainer.animate().alpha(1f).setDuration(300).start();

        // Update the container's layout parameters to wrap its content
        breakdownContainer.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        // Reference to Firestore collection for annual carbon footprint data
        CollectionReference annualCarbonFootprintSurveyDataRef = firestore.collection("AnnualCarbonFootprintSurveyData");

        // Fetch data from Firestore and populate the breakdown section
        annualCarbonFootprintSurveyDataRef.document(mAuth.getUid()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    try {
                        // Parse Firestore data into a HashMap
                        HashMap<String, String> parsedResponses = new HashMap<>();
                        Map<String, Object> data = document.getData();
                        Log.d("ResultsActivity", "DocumentSnapshot data: " + data);

                        // Convert Firestore data to a usable format for calculations
                        for (String key : data.keySet()) {
                            parsedResponses.put(key, data.get(key).toString());
                        }

                        // Calculate category-specific emissions
                        HashMap<String, Double> categoryEmissions =
                                new YearlyTotalCarbonFootprintCalculator().calculatePerCategoryEmission(parsedResponses);

                        // Extract emission values and categories for UI population
                        double[] emissions = extractEmissions(categoryEmissions);
                        String[] categories = {"Consumption", "Driving", "Flight", "Food", "Housing", "Public Transport"};

                        // Populate the breakdown section with detailed emission data
                        populateBreakdown(breakdownContainer, emissions, categories);
                    } catch (IOException e) {
                        throw new RuntimeException("Parsing Firestore data failed", e);
                    }
                }
            }
        });
    }


    /**
     * Populates a breakdown section dynamically with detailed emission data.
     * Each emission value is paired with its corresponding category and displayed as a formatted text entry.
     * This method creates and adds a `TextView` for each category's emission data to the provided container.
     * <br>
     * The container is cleared before populating to ensure there are no duplicate or old entries.
     *
     * @param container  The `LinearLayout` container where the breakdown information will be displayed.
     *                   This layout will dynamically hold the emission data for each category.
     * @param emissions  An array of emission values (in tons) for each category. The values correspond
     *                   directly to the `categories` array.
     * @param categories A string array of category names representing each emission source, such as
     *                   "Consumption," "Driving," "Flight," etc. Each category matches with an emission value
     *                   from the `emissions` array by index.
     */
    private void populateBreakdown(LinearLayout container, double[] emissions, String[] categories) {
        container.removeAllViews();

        for (int i = 0; i < emissions.length; i++) {
            TextView textView = createTextView(
                    String.format(Locale.getDefault(), "You have contributed %.2f tons of CO2 emissions from %s.", emissions[i], categories[i]));
            container.addView(textView);
        }
    }

    /**
     * Creates a dynamically styled `TextView` with the provided text.
     * This method initializes a new `TextView`, applies default styling such as text size, color, and layout parameters,
     * and sets the provided text as its content. It is commonly used for displaying formatted information
     * in dynamic layouts like a breakdown of emissions.
     *
     * @param text The text to display in the `TextView`. This text will be set as the content of the newly created `TextView`.
     * @return A newly created `TextView` instance with the provided text and default styling applied.
     */
    private TextView createTextView(String text) {
        // Create a new TextView instance
        TextView textView = new TextView(this);

        // Set layout parameters: full width and height wrapping its content
        textView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        textView.setTextSize(16f);

        textView.setTextColor(Color.DKGRAY);

        // Assign the provided text to the TextView
        textView.setText(text);

        return textView;
    }


    /**
     * Dynamically creates and populates a shared legend for both the Pie and Bar charts.
     * This method generates a legend based on the provided categories and corresponding colors.
     * Each legend entry consists of a color box and its associated category label.
     *
     * @param categories An array of category names that will appear as labels in the legend.
     *                   Each category corresponds to a section of the charts.
     * @param colors     An array of colors where each color corresponds to a category.
     *                   The order of colors should match the order of categories.
     */
    private void createSharedLegend(String[] categories, int[] colors) {
        // Clear any existing legend items to avoid duplication
        sharedLegendContainer.removeAllViews();

        // Iterate through categories and colors to create and add legend entries
        for (int i = 0; i < categories.length; i++) {
            // Create a legend entry and add it to the container
            sharedLegendContainer.addView(createLegendItem(colors[i], categories[i]));
        }
    }


    /**
     * Creates a legend item consisting of a color box and a label.
     * This method dynamically generates a horizontal layout containing:
     * - A color box that visually represents the category's color.
     * - A text label describing the category.
     *
     * @param color The color of the legend item's color box. This should correspond to the category it represents.
     * @param label The text label for the legend item, describing the category.
     * @return A `View` representing the complete legend item, including the color box and text label.
     */
    private View createLegendItem(int color, String label) {
        // Create a horizontal LinearLayout to hold the legend item
        LinearLayout item = new LinearLayout(this);
        item.setOrientation(LinearLayout.HORIZONTAL);

        // Create a square View for the color box and set its size and margins
        View colorBox = new View(this);
        LinearLayout.LayoutParams boxParams = new LinearLayout.LayoutParams(40, 40);
        boxParams.setMargins(8, 8, 16, 8);
        colorBox.setLayoutParams(boxParams);
        colorBox.setBackgroundColor(color); // Set the background color for the box

        // Create a TextView for the label and set its text and style
        TextView labelText = createTextView(label);
        labelText.setTextSize(14f); // Adjust text size for better readability
        labelText.setTextColor(Color.BLACK); // Set text color to black

        // Add the color box and label to the LinearLayout
        item.addView(colorBox);
        item.addView(labelText);

        // Return the fully constructed legend item
        return item;
    }


    /**
     * Fetches user emission data from Firebase Firestore and populates the UI with the retrieved data.
     * This method retrieves a document associated with the current user's ID from the "AnnualCarbonFootprintSurveyData" collection,
     * parses the data into emission categories, and updates various UI components like charts and contribution text.
     *
     * @throws RuntimeException If parsing Firestore data fails due to an I/O error.
     */
    private void fetchDataFromFirebase() {
        // Reference to Firestore collection containing user carbon footprint data
        CollectionReference annualCarbonFootprintSurveyDataRef = firestore.collection("AnnualCarbonFootprintSurveyData");

        // Fetch the document corresponding to the current user's unique ID
        annualCarbonFootprintSurveyDataRef.document(mAuth.getUid()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    try {
                        // Parse Firestore document into a HashMap for processing
                        HashMap<String, String> parsedResponses = new HashMap<>();
                        Map<String, Object> data = document.getData();
                        Log.d("ResultsActivity", "DocumentSnapshot data: " + data);

                        // Convert Firestore data to a usable format for calculations
                        for (String key : data.keySet()) {
                            parsedResponses.put(key, data.get(key).toString());
                        }

                        // Calculate emissions for each category using the parsed responses
                        HashMap<String, Double> categoryEmissions =
                                new YearlyTotalCarbonFootprintCalculator().calculatePerCategoryEmission(parsedResponses);

                        // Extract emission values and categories for visualization
                        double[] emissions = extractEmissions(categoryEmissions);
                        String[] categories = {"Consumption", "Driving", "Flight", "Food", "Housing", "Public Transport"};

                        // Calculate the total emissions in kilograms and convert to tons
                        double total = categoryEmissions.values().stream().mapToDouble(Double::doubleValue).sum();

                        // Update the summary text view with the user's yearly CO2 contribution
                        TextView contributionText = findViewById(R.id.contributionText);
                        String yearlyContributionText = getString(R.string.yearly_co2_contribution_summary_line, total / 1000);
                        contributionText.setText(yearlyContributionText);

                        // Populate the PieChart and BarChart with calculated data
                        populatePieChart(emissions, categories);
                        populateBarChart(emissions);

                        // Create a shared legend for both charts
                        createSharedLegend(categories, getLegendColors());
                    } catch (IOException e) {
                        // Throw a runtime exception if data parsing fails
                        throw new RuntimeException("Parsing Firestore data failed", e);
                    }
                }
            }
        });
    }



    /**
     * Extracts emission values for specific categories from a given data map.
     * This method retrieves the values associated with predefined keys representing different emission categories
     * and returns them as an array of doubles. The categories include:
     * - "ConsumptionEmissions"
     * - "DrivingEmissions"
     * - "FlightEmissions"
     * - "FoodEmissions"
     * - "HousingEmissions"
     * - "PublicTransportEmissions"
     *
     * @param data A `HashMap` where keys are category names (e.g., "ConsumptionEmissions") and values are their corresponding emissions in tons.
     * @return A `double[]` array containing the emissions for each predefined category in the following order:
     *         [Consumption, Driving, Flight, Food, Housing, Public Transport].
     * @throws NullPointerException If a required key is missing or its value is `null` in the input map.
     */
    private double[] extractEmissions(HashMap<String, Double> data) {
        // Extract and return emission values for the predefined categories
        return new double[]{
                data.get("ConsumptionEmissions"),
                data.get("DrivingEmissions"),
                data.get("FlightEmissions"),
                data.get("FoodEmissions"),
                data.get("HousingEmissions"),
                data.get("PublicTransportEmissions")
        };
    }


    private void populatePieChart(double[] emissions, String[] categories) {

        // Create entries for the Pie Chart
        ArrayList<PieEntry> entries = new ArrayList<>();

        ArrayList<Integer> colors = new ArrayList<>();

        for (int i = 0; i < emissions.length; i++) {
            if (emissions[i] > 0) {
                entries.add(new PieEntry((float) emissions[i], categories[i]));
                colors.add(getLegendColors()[i]);
            }
        }

        // Create dataset
        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(colors);
        dataSet.setValueTextSize(12f);                     // Value text size for visibility
        dataSet.setValueTextColor(Color.BLACK);            // Value text color
        dataSet.setValueLineColor(Color.BLACK);            // Black lines for values
        dataSet.setValueLineWidth(1.5f);                   // Slightly thicker connecting lines

        // Configure label positions and behavior
        dataSet.setValueLinePart1OffsetPercentage(80f);    // Offset the first part of the value line
        dataSet.setValueLinePart1Length(0.4f);             // First segment length of the line
        dataSet.setValueLinePart2Length(0.5f);             // Second segment length of the line
        dataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE); // Values float outside slices
        dataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE); // Labels float outside slices

        PieData data = new PieData(dataSet);

        // Configure Pie Chart
        pieChart.setData(data);
        pieChart.setHoleRadius(35f);                      // Adjust the inner circle radius
        pieChart.setTransparentCircleRadius(40f);         // Adjust transparent circle radius

        // Enable entry labels (floating labels on slices)
        pieChart.setDrawEntryLabels(true);                // Enable labels on the slices
        pieChart.setEntryLabelColor(Color.BLACK);         // Set slice label color
        pieChart.setEntryLabelTextSize(10f);              // Set slice label size

        // Floating animation for labels
        pieChart.setDragDecelerationFrictionCoef(0.95f);  // Smooth drag behavior
        pieChart.setRotationAngle(0);                     // Start at default rotation angle
        pieChart.setRotationEnabled(true);                // Enable rotation for dynamic interaction
        pieChart.setHighlightPerTapEnabled(true);         // Highlight slice on tap

        // Add center text
        pieChart.setCenterText("Emissions\nBreakdown");    // Add multi-line text in the center
        pieChart.setCenterTextSize(16f);                  // Set center text size
        pieChart.setCenterTextColor(Color.DKGRAY);        // Set center text color

        // Aesthetic adjustments
        pieChart.getDescription().setEnabled(false);      // Disable description
        pieChart.getLegend().setEnabled(false);           // Disable legend (handled elsewhere)

        // Refresh the Pie Chart
        pieChart.invalidate();
    }


    private void populateBarChart(double[] emissions) {
        // Create entries for the Bar Chart
        ArrayList<BarEntry> entries = new ArrayList<>();
        for (int i = 0; i < emissions.length; i++) {
            entries.add(new BarEntry(i, (float) emissions[i]));
        }

        // Create a BarDataSet with custom colors
        BarDataSet dataSet = new BarDataSet(entries, ""); // Empty label to avoid "Description Label"
        dataSet.setColors(getLegendColors());
        dataSet.setValueTextSize(12f); // Adjust font size for bar values
        dataSet.setValueTextColor(Color.BLACK); // Set value text color

        // Configure the BarData
        BarData data = new BarData(dataSet);
        data.setBarWidth(0.8f); // Adjust bar width for a sleeker look
        barChart.setData(data);

        // Style the Bar Chart
        barChart.getDescription().setEnabled(false); // Remove the "Description Label"
        barChart.setFitBars(true); // Ensure bars are fitted nicely
        barChart.getLegend().setEnabled(false); // Hide the legend
        barChart.animateY(1000); // Add animation for a modern feel

        // Configure X-Axis
        XAxis xAxis = barChart.getXAxis();
        xAxis.setGranularity(1f); // Ensure labels are spaced evenly
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM); // Position labels at the bottom
        xAxis.setDrawGridLines(false); // Remove grid lines
        xAxis.setTextSize(12f); // Adjust font size
        xAxis.setTextColor(Color.DKGRAY); // Set label color
        xAxis.setDrawAxisLine(false); // Remove axis line for a cleaner look

        // Configure Y-Axis
        barChart.getAxisLeft().setDrawGridLines(false); // Remove left-axis grid lines
        barChart.getAxisLeft().setTextSize(12f); // Adjust font size
        barChart.getAxisLeft().setTextColor(Color.DKGRAY); // Set text color
        barChart.getAxisLeft().setAxisMinimum(0f); // Start at 0

        barChart.getAxisRight().setEnabled(false); // Disable right Y-axis

        // Apply final styling
        barChart.setExtraBottomOffset(10f); // Add padding below chart
        barChart.invalidate(); // Refresh the chart
    }


    private int[] getLegendColors() {
        return new int[]{
                Color.parseColor("#4CAF50"), Color.parseColor("#8BC34A"),
                Color.parseColor("#A1887F"), Color.parseColor("#CDDC39"),
                Color.parseColor("#6D4C41"), Color.parseColor("#9E9D24")
        };
    }

    private void compareWithGlobalAverages(double userTotal, double globalAverage) {
        String message;
        if (userTotal > globalAverage) {
            message = String.format(Locale.getDefault(), "Your emissions are %.2f tons above the global average.", userTotal - globalAverage);
        } else {
            message = String.format(Locale.getDefault(), "Your emissions are %.2f tons below the global average.", globalAverage - userTotal);
        }
        comparisonText.setText(message);
    }

}


