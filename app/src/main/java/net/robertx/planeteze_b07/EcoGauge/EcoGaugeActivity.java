package net.robertx.planeteze_b07.EcoGauge;

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
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import net.robertx.planeteze_b07.R;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class EcoGaugeActivity extends AppCompatActivity {

    private LineChart lineChart;
    private PieChart pieChart;
    private TextView totalEmissionsTextView;

    private DatabaseReference databaseReference;

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    String userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
    private String selectedTimeFrame = "daily";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ecogauge);

        lineChart = findViewById(R.id.line_chart);
        pieChart = findViewById(R.id.pie_chart);
        totalEmissionsTextView = findViewById(R.id.total_emissions_text_view);
        Spinner timeFrameSpinner = findViewById(R.id.time_frame_spinner);


        databaseReference = FirebaseDatabase.getInstance().getReference().child("DailySurveyCO2").child(userId);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.time_frames, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        timeFrameSpinner.setAdapter(adapter);

        timeFrameSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedTimeFrame = parent.getItemAtPosition(position).toString().toLowerCase();
                TextView timeFrameIndicator = findViewById(R.id.text_time_frame);
                LocalDate today = LocalDate.now();
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
                Color.parseColor("#6D4C41"), Color.parseColor("#9E9D24")
        };
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


    private void loadDataFromFirebase() {
        databaseReference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DataSnapshot dataSnapshot = task.getResult();
                if (dataSnapshot.exists()) {
                    ArrayList<Entry> lineChartEntries = new ArrayList<>();
                    ArrayList<PieEntry> pieChartEntries = new ArrayList<>();
                    float totalEmissions = 0;
                    LocalDate earliestDateToConsider = LocalDate.now().minusDays(getDuration(selectedTimeFrame));

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        LocalDate entryDate = LocalDate.parse(snapshot.getKey(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                        if (entryDate.isBefore(earliestDateToConsider)) {
                            continue;
                        }

                        Map<String, Object> data = (Map<String, Object>) snapshot.getValue();

                        double dailyTotal = data.get("dailyTotal") != null ? (double) data.get("dailyTotal") : 0;

                        // Add data point to line chart
                        String key = snapshot.getKey();
                        float xValue = getDateAsXValue(key);
                        lineChartEntries.add(new Entry(xValue, (float) dailyTotal));

                        // Sum total emissions
                        totalEmissions += dailyTotal;

                        // Add breakdown to pie chart
                        for (Map.Entry<String, Object> entry : data.entrySet()) {
                            double value = 0;
                            if (entry.getValue() instanceof Long) {
                                Long l = (Long) entry.getValue();
                                value = l.doubleValue();
                            } else if (entry.getValue() instanceof Double) {
                                value = (double) entry.getValue();
                            }

                            if (!entry.getKey().equals("dailyTotal")) {
                                pieChartEntries.add(new PieEntry((float) value, getCategory(entry.getKey())));
                            }
                        }
                    }

                    // Update line chart
                    LineDataSet lineDataSet = new LineDataSet(lineChartEntries, "CO2 Emissions");
                    lineDataSet.setColors(getLegendColors());
                    lineChart.setData(new LineData(lineDataSet));
                    lineChart.invalidate();

                    // Update pie chart
                    PieDataSet pieDataSet = new PieDataSet(pieChartEntries, "Emissions Breakdown");
                    pieDataSet.setColors(getLegendColors());
                    pieChart.setData(new PieData(pieDataSet));
                    pieChart.setDrawEntryLabels(false);
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
