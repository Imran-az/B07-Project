package net.robertx.planeteze_b07.ecoGauge;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import net.robertx.planeteze_b07.R;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class EcoGaugeActivity extends AppCompatActivity {

    private LineChart lineChart;
    private PieChart pieChart;
    private TextView totalEmissionsTextView;

    private DatabaseReference databaseReference;

    final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    final String userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
    private String selectedTimeFrame = "daily";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ecogauge);

        MaterialToolbar toolbar = findViewById(R.id.eco_gauge_toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        lineChart = findViewById(R.id.eco_gauge_line_chart);
        pieChart = findViewById(R.id.eco_gauge_pie_chart);
        totalEmissionsTextView = findViewById(R.id.total_emissions_text_view);
        Spinner timeFrameSpinner = findViewById(R.id.time_frame_spinner);
        TextView timeFrameIndicator = findViewById(R.id.text_time_frame);
        String initialTimeframe = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        timeFrameIndicator.setText(String.format(Locale.getDefault(), "Time frame: %s to %s", initialTimeframe, initialTimeframe));
        databaseReference = FirebaseDatabase.getInstance().getReference().child("DailySurveyCO2").child(userId);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.time_frames, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        timeFrameSpinner.setAdapter(adapter);

        timeFrameSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                LocalDate today = LocalDate.now();
                String newTimeFrame = parent.getItemAtPosition(position).toString().toLowerCase();
                if (newTimeFrame.equals(selectedTimeFrame)) {
                    return;
                }
                selectedTimeFrame = newTimeFrame;
                LocalDate earliestDateToConsider = today.minusDays(getDuration(selectedTimeFrame));
                timeFrameIndicator.setText(String.format(Locale.getDefault(), "Time frame: %s to %s", earliestDateToConsider.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) , today.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))));

                loadDataFromFirebase();
            }


            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        loadDataFromFirebase();
    }

    /**
     * Obtains the category of the CO2 emitted from the database keys
     * @param key The key in the DB
     * @return the category of the CO2 emitted
     */
    public static String getCategory(String key) {
        switch (key) {
            case "ClothesDailyCO2":
                return "Clothes";
            case "DrivePersonalVehicleDailyCO2":
                return "Driving Personal Vehicle";
            case "ElectronicsDailyCO2":
                return "Electronics";
            case "EnergyBillDailyCO2":
                return "Energy Bill";
            case "Flight (Short-Haul or Long-Haul)DailyCO2":
                return "Flights";
            case "MealDailyCO2":
                return "Meals";
            case "OtherPurchasesDailyCO2":
                return "Other Purchases";
            case "TakePublicTransportationDailyCO2":
                return "Public Transportation";
            case "dailyTotal":
                return "Total Daily Emissions";
            default:
                return "Unknown Key";
        }
    }

    private int[] getLegendColors() {
        return new int[]{
                Color.parseColor("#4CAF50"), Color.parseColor("#8BC34A"),
                Color.parseColor("#A1887F"), Color.parseColor("#CDDC39"),
                Color.parseColor("#6D4C41"), Color.parseColor("#9E9D24"),
                Color.parseColor("#FFC107"), Color.parseColor("#FF5722")
        };
    }

    private int getLegendColor(String categoryKey) {
        int[] colors = getLegendColors();
        switch (categoryKey) {
            case "Clothes":
                return colors[0];
            case "Driving Personal Vehicle":
                return colors[1];
            case "Electronics":
                return colors[2];
            case "Energy Bill":
                return colors[3];
            case "Flights":
                return colors[4];
            case "Meals":
                return colors[5];
            case "Other Purchases":
                return colors[6];
            case "Public Transportation":
                return colors[7];
            default:
                return Color.BLACK;
        }
    }

    private int getDataSetIndexForLabel(String label) {
        switch (label) {
            case "Clothes":
                return 0;
            case "Driving Personal Vehicle":
                return 1;
            case "Electronics":
                return 2;
            case "Energy Bill":
                return 3;
            case "Flights":
                return 4;
            case "Meals":
                return 5;
            case "Other Purchases":
                return 6;
            case "Public Transportation":
                return 7;
            default:
                return -1;
        }
    }

    private int getDuration(String timeFrame) {
        switch (timeFrame) {
            case "daily":
                return 0;
            case "weekly":
                return 7;
            case "monthly":
                return 30;
            case "yearly":
                return 365;
            default:
                return 1;
        }
    }

    /**
     * Convert firebase numeric type to a double
     * @param value The value to convert
     * @return The value as a double
     */
    private double convertFirebaseNumerics(Object value) {
        if (value instanceof Long) {
            return ((Long) value).doubleValue();
        } else if (value instanceof Double) {
            return (double) value;
        } else {
            return 0;
        }
    }


    private void loadDataFromFirebase() {
        databaseReference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DataSnapshot dataSnapshot = task.getResult();
                double totalEmissions = 0;
                if (dataSnapshot.exists()) {
                    LineDataSet lineDataSet = new LineDataSet(new ArrayList<>(), "CO2 Emissions");
                    ArrayList<PieEntry> pieChartEntries = new ArrayList<>();
                    ArrayList<Integer> colorsForPieChart = new ArrayList<>();

                    LocalDate earliestDateToConsider = LocalDate.now().minusDays(getDuration(selectedTimeFrame));

                    HashMap<String, Double> breakdownMap = new HashMap<>();

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        LocalDate entryDate = LocalDate.parse(snapshot.getKey(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                        if (entryDate.isBefore(earliestDateToConsider)) {
                            continue;
                        }
                        Map<String, Object> data = (Map<String, Object>) snapshot.getValue();

                        // Add data point for the given date to line chart
                        double dailyTotal = convertFirebaseNumerics(data.get("dailyTotal"));
                        totalEmissions += dailyTotal;
                        String key = snapshot.getKey();
                        float xValue = getDateAsXValue(key);
                        lineDataSet.addEntry(new Entry(xValue, (float) dailyTotal));

                        for (Map.Entry<String, Object> entry : data.entrySet()) {
                            double value = convertFirebaseNumerics(entry.getValue());
                            String category = getCategory(entry.getKey());
                            if (breakdownMap.containsKey(category)) {
                                breakdownMap.put(category, breakdownMap.get(category) + value);
                            } else {
                                breakdownMap.put(category, value);
                            }
                        }

                    }
                    // populate pie chart data
                    for (Map.Entry<String, Double> entry : breakdownMap.entrySet()) {
                        if (!entry.getKey().equals("Total Daily Emissions") && entry.getValue() > 0) {
                            pieChartEntries.add(new PieEntry(entry.getValue().floatValue(), entry.getKey()));
                            colorsForPieChart.add(getLegendColor(entry.getKey()));
                        }
                    }

                    // Update line chart
                    lineChart.clear();
                    LineData lineData = new LineData(lineDataSet);
                    Description lineChartDesc = new Description();
                    lineChartDesc.setText("CO2 Emissions Over Time");
                    lineChart.setDescription(lineChartDesc);
                    lineChart.setData(lineData);
                    lineChart.invalidate();

                    // Update pie chart
                    pieChart.clear();
                    PieDataSet pieDataSet = new PieDataSet(pieChartEntries, "Emissions category");
                    pieDataSet.setColors(colorsForPieChart);
                    Description pieChartDesc = new Description();
                    pieChartDesc.setText("Emissions Breakdown");
                    pieChart.setDescription(pieChartDesc);
                    pieChart.setData(new PieData(pieDataSet));
                    pieChart.setDrawEntryLabels(false);
                    pieChart.getLegend().setWordWrapEnabled(true);
                    pieChart.invalidate();

                    // Update total emissions text
                    totalEmissionsTextView.setText(String.format(Locale.getDefault(),
                            "Total CO2e: %.2f kg", totalEmissions));
                }
            }
        });
    }

    private float getDateAsXValue(String dateString) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(sdf.parse(dateString));
            return calendar.get(Calendar.DAY_OF_YEAR);  // Use day of the year as X-value
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}
