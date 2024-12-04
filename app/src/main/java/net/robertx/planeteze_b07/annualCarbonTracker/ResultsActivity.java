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

/**
 * Activity to display the results of the user's annual carbon footprint.
 * This activity shows a breakdown of the user's carbon emissions in various categories
 * and allows comparison with country-specific and global averages.
 * <p>
 * The activity fetches data from Firebase Firestore, processes it, and visualizes it using
 * PieChart and BarChart. Users can also compare their emissions with the average emissions
 * of a specified country.
 * </p>
 */
public class ResultsActivity extends AppCompatActivity {
    /**
     * Pie chart to display the breakdown of the user's carbon emissions.
     */
    private PieChart pieChart;

    /**
     * Bar chart to display the user's carbon emissions in various categories.
     */
    private BarChart barChart;



    /**
     * GridLayout to contain the shared legend for both the PieChart and BarChart.
     */
    private GridLayout sharedLegendContainer;

    /**
     * Bar chart to display the comparison between the user's emissions and the specified country's average emissions.
     */
    private BarChart comparisonBarChart;

    /**
     * TextView to display the result of the comparison between the user's emissions and the specified country's average emissions.
     */
    private TextView comparisonResult;

    /**
     * EditText to input the name of the country for comparison.
     */
    private EditText editTextCountry;

    /**
     * Button to submit the country name and initiate the comparison.
     */
    private Button compareSubmitButton;

    /**
     * FirebaseAuth instance to manage user authentication.
     */
    final FirebaseAuth mAuth = FirebaseAuth.getInstance();

    /**
     * FirebaseFirestore instance to interact with the Firestore database.
     */
    FirebaseFirestore firestore;

    /**
     * Called when the activity is starting. This is where most initialization should go.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down,
     *                           then this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle).
     *                           Otherwise, it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_results);

        findViewById(R.id.returnHome).setOnClickListener(v -> finish());

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

        try {
            HousingCO2DataRetriever.initialize(this);
        } catch (IOException e) {
            e.printStackTrace();
        }

        firestore = FirebaseFirestore.getInstance();

        initializeViews();

        fetchDataFromFirebase();
        fetchTotalEmissions(totalEmissions -> {
            comparisonBarChart.setVisibility(View.GONE);

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
        CollectionReference annualCarbonFootprintSurveyDataRef = firestore.collection("AnnualCarbonFootprintSurveyData");

        annualCarbonFootprintSurveyDataRef.document(mAuth.getUid()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    try {
                        Map<String, Object> data = document.getData();
                        HashMap<String, String> parsedResponses = new HashMap<>();
                        for (String key : data.keySet()) {
                            parsedResponses.put(key, data.get(key).toString());
                        }

                        HashMap<String, Double> categoryEmissions = new YearlyTotalCarbonFootprintCalculator()
                                .calculatePerCategoryEmission(parsedResponses);

                        double totalKg = categoryEmissions.values().stream().mapToDouble(Double::doubleValue).sum();
                        double totalTons = totalKg;

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
            String formattedCountry = country.substring(0, 1).toUpperCase() + country.substring(1).toLowerCase();

            double countryEmissionsKg = dataRetriever.getEmissionValue(formattedCountry) * 1000;

            if (countryEmissionsKg > 0) {
                double percentageDifference = ((userEmissionsKg - countryEmissionsKg) / countryEmissionsKg) * 100;

                String message;
                if (userEmissionsKg > countryEmissionsKg) {
                    message = String.format(Locale.getDefault(), "Your emissions are %.2f%% higher than %s's average of %.2f kg.",
                            percentageDifference, formattedCountry, countryEmissionsKg);
                } else {
                    message = String.format(Locale.getDefault(), "Your emissions are %.2f%% lower than %s's average of %.2f kg.",
                            Math.abs(percentageDifference), formattedCountry, countryEmissionsKg);
                }

                comparisonResult.setText(message);
                comparisonResult.setVisibility(View.VISIBLE);
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

    /**
     * Updates the comparison bar chart with the given emissions data and labels.
     * This method creates entries for the bar chart, configures the bar data set with custom colors,
     * and styles the bar chart for modern look. It also sets up the X and Y axes with
     * appropriate labels and formatting.
     *
     * @param emissions An array of emission values to be displayed in the bar chart.
     * @param labels    An array of labels corresponding to the emission values.
     */
    private void updateComparisonBarChart(double[] emissions, String[] labels) {
        ArrayList<BarEntry> entries = new ArrayList<>();
        for (int i = 0; i < emissions.length; i++) {
            entries.add(new BarEntry(i, (float) emissions[i]));
        }

        BarDataSet dataSet = new BarDataSet(entries, "Comparison");
        dataSet.setColors(Color.parseColor("#4CAF50"), Color.parseColor("#FF5722")); // Green and red
        dataSet.setValueTextSize(12f);
        dataSet.setValueTextColor(Color.BLACK);
        BarData data = new BarData(dataSet);
        data.setBarWidth(0.8f);

        comparisonBarChart.setData(data);
        comparisonBarChart.getDescription().setEnabled(false);
        comparisonBarChart.setFitBars(true);
        comparisonBarChart.getLegend().setEnabled(false);
        comparisonBarChart.animateY(1000);

        XAxis xAxis = comparisonBarChart.getXAxis();
        xAxis.setGranularity(1f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setTextSize(12f);
        xAxis.setTextColor(Color.DKGRAY);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));

        comparisonBarChart.getAxisLeft().setDrawGridLines(false);
        comparisonBarChart.getAxisLeft().setTextSize(12f);
        comparisonBarChart.getAxisLeft().setTextColor(Color.DKGRAY);
        comparisonBarChart.getAxisLeft().setAxisMinimum(0f);
        comparisonBarChart.getAxisRight().setEnabled(false);
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
        LinearLayout breakdownContainer = findViewById(R.id.breakdownContainer);
        boolean isVisible = breakdownContainer.getVisibility() == View.VISIBLE;

        TransitionManager.beginDelayedTransition((ViewGroup) view, new AutoTransition());

        if (isVisible) {
            collapseSection(breakdownContainer);
        } else {
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
        breakdownContainer.setAlpha(0f);
        breakdownContainer.setVisibility(View.VISIBLE);
        breakdownContainer.animate().alpha(1f).setDuration(300).start();
        breakdownContainer.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        CollectionReference annualCarbonFootprintSurveyDataRef = firestore.collection("AnnualCarbonFootprintSurveyData");
        annualCarbonFootprintSurveyDataRef.document(mAuth.getUid()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    try {
                        HashMap<String, String> parsedResponses = new HashMap<>();
                        Map<String, Object> data = document.getData();
                        Log.d("ResultsActivity", "DocumentSnapshot data: " + data);

                        for (String key : data.keySet()) {
                            parsedResponses.put(key, data.get(key).toString());
                        }

                        HashMap<String, Double> categoryEmissions =
                                new YearlyTotalCarbonFootprintCalculator().calculatePerCategoryEmission(parsedResponses);

                        double[] emissions = extractEmissions(categoryEmissions);
                        String[] categories = {"Consumption", "Driving", "Flight", "Food", "Housing", "Public Transport"};

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
                    String.format(Locale.getDefault(), "You have contributed %.2f kilograms of CO2 emissions from %s.", emissions[i], categories[i]));
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
        TextView textView = new TextView(this);
        textView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        textView.setTextSize(16f);
        textView.setTextColor(Color.DKGRAY);
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
        sharedLegendContainer.removeAllViews();

        for (int i = 0; i < categories.length; i++) {
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
        LinearLayout item = new LinearLayout(this);
        item.setOrientation(LinearLayout.HORIZONTAL);

        View colorBox = new View(this);
        LinearLayout.LayoutParams boxParams = new LinearLayout.LayoutParams(40, 40);
        boxParams.setMargins(8, 8, 16, 8);
        colorBox.setLayoutParams(boxParams);
        colorBox.setBackgroundColor(color);

        TextView labelText = createTextView(label);
        labelText.setTextSize(14f);
        labelText.setTextColor(Color.BLACK);

        item.addView(colorBox);
        item.addView(labelText);

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
        CollectionReference annualCarbonFootprintSurveyDataRef = firestore.collection("AnnualCarbonFootprintSurveyData");
        annualCarbonFootprintSurveyDataRef.document(mAuth.getUid()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    try {
                        HashMap<String, String> parsedResponses = new HashMap<>();
                        Map<String, Object> data = document.getData();
                        Log.d("ResultsActivity", "DocumentSnapshot data: " + data);

                        for (String key : data.keySet()) {
                            parsedResponses.put(key, data.get(key).toString());
                        }
                        HashMap<String, Double> categoryEmissions =
                                new YearlyTotalCarbonFootprintCalculator().calculatePerCategoryEmission(parsedResponses);

                        double[] emissions = extractEmissions(categoryEmissions);
                        String[] categories = {"Consumption", "Driving", "Flight", "Food", "Housing", "Public Transport"};
                        double total = categoryEmissions.values().stream().mapToDouble(Double::doubleValue).sum();

                        TextView contributionText = findViewById(R.id.contributionText);
                        String yearlyContributionText = getString(R.string.yearly_co2_contribution_summary_line, total / 1000);
                        contributionText.setText(yearlyContributionText);

                        populatePieChart(emissions, categories);
                        populateBarChart(emissions);

                        createSharedLegend(categories, getLegendColors());
                    } catch (IOException e) {
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
        return new double[]{
                data.get("ConsumptionEmissions"),
                data.get("DrivingEmissions"),
                data.get("FlightEmissions"),
                data.get("FoodEmissions"),
                data.get("HousingEmissions"),
                data.get("PublicTransportEmissions")
        };
    }

    /**
     * Populates the PieChart with emission data and category labels.
     * This method creates entries for the PieChart, configures the PieDataSet with custom colors,
     * and styles the PieChart for a sleek and modern look. It also sets up the PieChart with
     * appropriate labels, text sizes, and other visual properties.
     *
     * @param emissions  An array of emission values to be displayed in the PieChart.
     * @param categories An array of category labels corresponding to the emission values.
     */
    private void populatePieChart(double[] emissions, String[] categories) {
        ArrayList<PieEntry> entries = new ArrayList<>();
        ArrayList<Integer> colors = new ArrayList<>();

        for (int i = 0; i < emissions.length; i++) {
            if (emissions[i] > 0) {
                entries.add(new PieEntry((float) emissions[i], categories[i]));
                colors.add(getLegendColors()[i]);
            }
        }

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(colors);
        dataSet.setValueTextSize(12f);
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setValueLineColor(Color.BLACK);
        dataSet.setValueLineWidth(1.5f);

        dataSet.setValueLinePart1OffsetPercentage(80f);
        dataSet.setValueLinePart1Length(0.4f);
        dataSet.setValueLinePart2Length(0.5f);
        dataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        dataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);

        PieData data = new PieData(dataSet);

        pieChart.setData(data);
        pieChart.setHoleRadius(35f);
        pieChart.setTransparentCircleRadius(40f);
        pieChart.setDrawEntryLabels(true);
        pieChart.setEntryLabelColor(Color.BLACK);
        pieChart.setEntryLabelTextSize(10f);
        pieChart.setDragDecelerationFrictionCoef(0.95f);
        pieChart.setRotationAngle(0);
        pieChart.setRotationEnabled(true);
        pieChart.setHighlightPerTapEnabled(true);
        pieChart.setCenterText("Emissions\nBreakdown");
        pieChart.setCenterTextSize(16f);
        pieChart.setCenterTextColor(Color.DKGRAY);
        pieChart.getDescription().setEnabled(false);
        pieChart.getLegend().setEnabled(false);
        pieChart.invalidate();
    }

    /**
     * Populates the BarChart with emission data.
     * This method creates entries for the BarChart, configures the BarDataSet with custom colors,
     * and styles the BarChart for a sleek and modern look. It also sets up the BarChart with
     * appropriate labels, text sizes, and other visual properties.
     *
     * @param emissions An array of emission values to be displayed in the BarChart.
     */
    private void populateBarChart(double[] emissions) {
        ArrayList<BarEntry> entries = new ArrayList<>();
        for (int i = 0; i < emissions.length; i++) {
            entries.add(new BarEntry(i, (float) emissions[i]));
        }

        BarDataSet dataSet = new BarDataSet(entries, "");
        dataSet.setColors(getLegendColors());
        dataSet.setValueTextSize(12f);
        dataSet.setValueTextColor(Color.BLACK);

        BarData data = new BarData(dataSet);
        data.setBarWidth(0.8f);
        barChart.setData(data);
        barChart.getDescription().setEnabled(false);
        barChart.setFitBars(true);
        barChart.getLegend().setEnabled(false);
        barChart.animateY(1000);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setGranularity(1f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setTextSize(12f);
        xAxis.setTextColor(Color.DKGRAY);
        xAxis.setDrawAxisLine(false);

        barChart.getAxisLeft().setDrawGridLines(false);
        barChart.getAxisLeft().setTextSize(12f);
        barChart.getAxisLeft().setTextColor(Color.DKGRAY);
        barChart.getAxisLeft().setAxisMinimum(0f);
        barChart.getAxisRight().setEnabled(false);
        barChart.setExtraBottomOffset(10f);
        barChart.invalidate();
    }


    /**
     * Returns an array of colors used for the legend in the charts.
     * Each color corresponds to a specific category in the emission breakdown.
     *
     * @return An array of integer color values.
     */
    private int[] getLegendColors() {
        return new int[]{
                Color.parseColor("#4CAF50"), Color.parseColor("#8BC34A"),
                Color.parseColor("#A1887F"), Color.parseColor("#CDDC39"),
                Color.parseColor("#6D4C41"), Color.parseColor("#9E9D24")
        };
    }
}


