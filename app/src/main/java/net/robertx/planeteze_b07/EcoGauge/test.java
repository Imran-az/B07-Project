package net.robertx.planeteze_b07.EcoGauge;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class test extends AppCompatActivity {
    private PieChart gaugeChart;
    private TextView comparisonText;
    private Spinner timeFrameSpinner;

    // Global average per capita emissions map (parsed from CSV)
    private Map<String, Double> globalAverages = new HashMap<>();
    private double userEmissions = 0.0;  // Dummy value; to be fetched from DB
    private String selectedTimeFrame = "Yearly";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create root layout programmatically
        RelativeLayout rootLayout = new RelativeLayout(this);
        rootLayout.setLayoutParams(new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT
        ));

        // Create and add PieChart programmatically
        gaugeChart = new PieChart(this);
        RelativeLayout.LayoutParams chartParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                700
        );
        chartParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        gaugeChart.setLayoutParams(chartParams);
        rootLayout.addView(gaugeChart);

        // Create and add TextView for comparison text programmatically
        comparisonText = new TextView(this);
        RelativeLayout.LayoutParams comparisonTextParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        comparisonTextParams.addRule(RelativeLayout.BELOW, gaugeChart.getId());
        comparisonTextParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        comparisonText.setLayoutParams(comparisonTextParams);
        comparisonText.setTextSize(18f);
        rootLayout.addView(comparisonText);

        // Create and add Spinner programmatically for time frame selection
        timeFrameSpinner = new Spinner(this);
        RelativeLayout.LayoutParams spinnerParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        spinnerParams.addRule(RelativeLayout.BELOW, comparisonText.getId());
        spinnerParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        timeFrameSpinner.setLayoutParams(spinnerParams);
        rootLayout.addView(timeFrameSpinner);

        setContentView(rootLayout);  // Set the programmatically created layout

        // Initialize UI logic
        loadGlobalAveragesFromCSV();
        setupTimeFrameSpinner();
        fetchUserEmissions(selectedTimeFrame);
        updateGaugeAndComparison();
    }

    private void loadGlobalAveragesFromCSV() {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(getResources().openRawResource(
                        getResources().getIdentifier("global_averages", "raw", getPackageName())
                )))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] splitLine = line.split(",");
                globalAverages.put(splitLine[0], Double.parseDouble(splitLine[1]));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setupTimeFrameSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                getResources().getIdentifier("time_frames", "array", getPackageName()),
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        timeFrameSpinner.setAdapter(adapter);

        timeFrameSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedTimeFrame = parent.getItemAtPosition(position).toString();
                fetchUserEmissions(selectedTimeFrame);
                updateGaugeAndComparison();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }

    private void fetchUserEmissions(String timeFrame) {
        // Placeholder for DB call
        switch (timeFrame) {
            case "Daily":
                userEmissions = 5.0;  // Dummy value
                break;
            case "Weekly":
                userEmissions = 35.0;
                break;
            case "Monthly":
                userEmissions = 150.0;
                break;
            case "Yearly":
                userEmissions = 1800.0;
                break;
        }
    }

    private void updateGaugeAndComparison() {
        double globalAverage = globalAverages.getOrDefault("Global", 4.083) * getConversionFactor();
        double userPercentage = (userEmissions / globalAverage) * 100;

        updateGaugeChart(userPercentage);

        String message = userPercentage > 100
                ? String.format("Your emissions are %.2f%% higher than the global average.", userPercentage - 100)
                : String.format("Your emissions are %.2f%% lower than the global average.", 100 - userPercentage);
        comparisonText.setText(message);
    }

    private double getConversionFactor() {
        switch (selectedTimeFrame) {
            case "Daily":
                return 1 / 365.0;
            case "Weekly":
                return 1 / 52.0;
            case "Monthly":
                return 1 / 12.0;
            default:
                return 1.0;
        }
    }

    private void updateGaugeChart(double userPercentage) {
        ArrayList<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry((float) userPercentage, "You"));
        entries.add(new PieEntry(100f - (float) userPercentage, "Global Avg"));

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(new int[]{Color.RED, Color.GREEN});
        dataSet.setValueTextSize(12f);
        dataSet.setValueTextColor(Color.BLACK);

        PieData data = new PieData(dataSet);

        gaugeChart.setData(data);
        gaugeChart.setHoleRadius(50f);
        gaugeChart.setTransparentCircleRadius(55f);
        gaugeChart.setDrawEntryLabels(false);
        gaugeChart.getDescription().setEnabled(false);
        gaugeChart.getLegend().setEnabled(false);

        gaugeChart.invalidate();
    }
}
