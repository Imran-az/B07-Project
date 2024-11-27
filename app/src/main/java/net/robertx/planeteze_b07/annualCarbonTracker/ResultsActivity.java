package net.robertx.planeteze_b07.annualCarbonTracker;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import net.robertx.planeteze_b07.CarbonFootprintCalculators.YearlyTotalCarbonFootprintCalculator;
import net.robertx.planeteze_b07.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ResultsActivity extends AppCompatActivity {
    private PieChart pieChart;
    private BarChart barChart;
    private TextView comparisonText;
    private GridLayout sharedLegendContainer;

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    String userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

    FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_results);

        // Apply insets for system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainScrollView), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        firestore = FirebaseFirestore.getInstance();

        // Initialize views
        initializeViews();

        // Fetch and process data from Firebase
        fetchDataFromFirebase();

    }

    private void initializeViews() {
        pieChart = findViewById(R.id.pieChart);
        barChart = findViewById(R.id.barChart);
        comparisonText = findViewById(R.id.comparisonText);
        sharedLegendContainer = findViewById(R.id.sharedLegendContainer);
    }

    /**
     * Expands or collapses the breakdown section dynamically.
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
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

    private void collapseSection(LinearLayout breakdownContainer) {
        breakdownContainer.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0));
        breakdownContainer.setVisibility(View.GONE);
    }

    private void expandSection(LinearLayout breakdownContainer) {
        breakdownContainer.setAlpha(0f);
        breakdownContainer.setVisibility(View.VISIBLE);
        breakdownContainer.animate().alpha(1f).setDuration(300).start();
        breakdownContainer.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        CollectionReference annualCarbonFootprintSurveyDataRef = firestore.collection("AnnualCarbonFootprintSurveyData");

        // populate breakdown section with emission data
        annualCarbonFootprintSurveyDataRef.document(mAuth.getUid()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    try {
                        HashMap<String, String> responses = document.toObject(HashMap.class);
                        HashMap<String, Double> categoryEmissions = new YearlyTotalCarbonFootprintCalculator().calculatePerCategoryEmission(responses);
                        double[] emissions = extractEmissions(categoryEmissions);
                        String[] categories = {"Consumption", "Driving", "Flight", "Food", "Housing", "Public Transport"};
                        populateBreakdown(breakdownContainer, emissions, categories);
                    } catch (IOException e) {
                        throw new RuntimeException("Parsing firestore data failed", e);
                    }
                }
            }
        });
    }

    /**
     * Populates the breakdown section dynamically with emission data.
     */
    private void populateBreakdown(LinearLayout container, double[] emissions, String[] categories) {
        container.removeAllViews();

        for (int i = 0; i < emissions.length; i++) {
            TextView textView = createTextView(
                    String.format("You have contributed %.2f tons of CO2 emissions from %s.", emissions[i], categories[i]));
            container.addView(textView);
        }
    }

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
     * Creates a shared legend for both the Pie and Bar charts.
     */
    private void createSharedLegend(String[] categories, int[] colors) {
        sharedLegendContainer.removeAllViews();
        for (int i = 0; i < categories.length; i++) {
            sharedLegendContainer.addView(createLegendItem(colors[i], categories[i]));
        }
    }

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
     * Fetches emission data from Firebase and processes it.
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

                        HashMap<String, Double> categoryEmissions = new YearlyTotalCarbonFootprintCalculator().calculatePerCategoryEmission(parsedResponses);
                        double[] emissions = extractEmissions(categoryEmissions);
                        String[] categories = {"Consumption", "Driving", "Flight", "Food", "Housing", "Public Transport"};
                        double total = categoryEmissions.values().stream().mapToDouble(Double::doubleValue).sum();

                        TextView contributionText = findViewById(R.id.contributionText);
                        String yearlyContributionText = getString(R.string.yearly_co2_contribution_summary_line, total / 1000);
                        contributionText.setText(yearlyContributionText);

                        populatePieChart(emissions, categories);
                        populateBarChart(emissions);
                        createSharedLegend(categories, getLegendColors());
                        compareWithGlobalAverages(total, 4.083); // Example global average
                    } catch (IOException e) {
                        throw new RuntimeException("Parsing firestore data failed", e);
                    }
                }
            }
        });
    }

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

        dataSet.getColors();
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
        String message = userTotal > globalAverage
                ? String.format("Your emissions are %.2f tons above the global average.", userTotal - globalAverage)
                : String.format("Your emissions are %.2f tons below the global average.", globalAverage - userTotal);
        comparisonText.setText(message);
    }
}


