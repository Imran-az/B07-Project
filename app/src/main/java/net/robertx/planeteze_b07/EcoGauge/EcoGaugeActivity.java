package net.robertx.planeteze_b07.EcoGauge;

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
                loadDataFromFirebase();
            }


            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        loadDataFromFirebase();
    }

    private void loadDataFromFirebase() {
        databaseReference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DataSnapshot dataSnapshot = task.getResult();
                if (dataSnapshot.exists()) {
                    ArrayList<Entry> lineChartEntries = new ArrayList<>();
                    ArrayList<PieEntry> pieChartEntries = new ArrayList<>();
                    float totalEmissions = 0;

                    for (DataSnapshot dateSnapshot : dataSnapshot.getChildren()) {
                        Map<String, Double> dailyData = (Map<String, Double>) dateSnapshot.getValue();
                        double dailyTotal = dailyData.get("dailyTotal");

                        // Add data point to line chart
                        String date = dateSnapshot.getKey();
                        float xValue = getDateAsXValue(date);
                        lineChartEntries.add(new Entry(xValue, (float) dailyTotal));

                        // Sum total emissions
                        totalEmissions += dailyTotal;

                        // Add breakdown to pie chart
                        for (Map.Entry<String, Double> entry : dailyData.entrySet()) {
                            if (!entry.getKey().equals("dailyTotal")) {
                                pieChartEntries.add(new PieEntry(entry.getValue().floatValue(), entry.getKey()));
                            }
                        }
                    }

                    // Update line chart
                    LineDataSet lineDataSet = new LineDataSet(lineChartEntries, "CO2 Emissions");
                    lineChart.setData(new LineData(lineDataSet));
                    lineChart.invalidate();

                    // Update pie chart
                    PieDataSet pieDataSet = new PieDataSet(pieChartEntries, "Emissions Breakdown");
                    pieChart.setData(new PieData(pieDataSet));
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
