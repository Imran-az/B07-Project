package net.robertx.planeteze_b07.EcoTracker;

import android.content.Intent;
import android.os.Bundle;
import android.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import net.robertx.planeteze_b07.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.HashMap;

public class HabitTrackerListPage extends AppCompatActivity {

    private Button confirmButton;
    private Button cancelButton;
    private List<String> habitList;
    private List<String> filteredHabitList;
    private ArrayAdapter<String> dialogAdapter;
    private String selectedHabit = "";
    TextView customSpinner, resultText;
    FirebaseDatabase logDatabase;
    DatabaseReference habitLogReference;
    private DatabaseReference dailyHabitTrackerRef;
    private String userID;
    public static Map<String, ArrayList<String>> typeFilter = new HashMap<>();
    public static Map<String, ArrayList<String>> impactFilter = new HashMap<>();
    String contributorGreatest;

    public HabitTrackerListPage(Button personalizedButton) {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_habit_tracker_list_page);

        logDatabase = FirebaseDatabase.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        userID = currentUser.getUid();
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        // Set up references
        dailyHabitTrackerRef = logDatabase.getReference("DailyHabitTracker").child(userID);
        habitLogReference = dailyHabitTrackerRef.child("SelectedHabit");
        DatabaseReference habitLogsRef = dailyHabitTrackerRef.child("HabitLogs");

        customSpinner = findViewById(R.id.custom_spinner);
        resultText = findViewById(R.id.result_text);
        confirmButton = findViewById(R.id.confirm_button);
        cancelButton = findViewById(R.id.cancel_button);
        Button backButton = findViewById(R.id.back_button);
        Button logButton = findViewById(R.id.log_button);

        habitLogsRef.child(currentDate).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && !task.getResult().exists()) {
                habitLogsRef.child(currentDate).setValue("Not Completed");
            }
        });

        habitList = new ArrayList<>();
        habitList.add("Walk to work/school/destinations");
        habitList.add("Cycle to work/school/destinations");
        habitList.add("Carpool to work/school/destinations");
        habitList.add("Take public transportation to work/school/destinations");
        habitList.add("Unplug devices when not in use");
        habitList.add("Switch to LED bulbs");
        habitList.add("Use cold water for laundry");
        habitList.add("Introduce one plant-based meal per day");
        habitList.add("Compost food scraps instead of discarding them");
        habitList.add("Reduce portion sizes for meals");
        habitList.add("Switch to cycling or walking for short distances");
        habitList.add("Combine errands into a single trip to reduce driving distances");
        habitList.add("Eliminate non-essential flights, especially long-haul");
        habitList.add("Adopt a fully plant-based diet");
        habitList.add("Buy seasonal produce to reduce energy-intensive farming practices");
        habitList.add("Buy fewer new clothes and opt for second-hand items");
        habitList.add("Unplug and recycle old electronics responsibly");
        habitList.add("Choose energy-efficient appliances");
        habitList.add("Reduce idling time when driving to save fuel");
        habitList.add("Switch to renewable energy");
        habitList.add("Eliminate food waste by meal planning and using leftovers");
        habitList.add("Buy locally-sourced produce to reduce transportation emissions");
        habitList.add( "Repair broken items instead of discarding them");

        // Initialize filteredHabitList as a copy of habitList
        filteredHabitList = new ArrayList<>(habitList);

        // Initialize filters
        initializeFilters();

        customSpinner.setOnClickListener(v -> showSearchableDialog());

        confirmButton.setOnClickListener(v -> {
            if (!selectedHabit.isEmpty()) {
                // Clear existing logs when selecting a new habit
                habitLogsRef.removeValue() // This removes all logs under HabitLogs
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                // Save the selected habit name and start date
                                habitLogReference.child("habitName").setValue(selectedHabit);
                                habitLogReference.child("startDate").setValue(currentDate);

                                // Do not create a log for "Not Completed" on adoption.
                                // Logs will be created only when the user marks the habit as completed.

                                Toast.makeText(HabitTrackerListPage.this, "Habit confirmed", Toast.LENGTH_SHORT).show();
                                resultText.setText("Selected Habit: " + selectedHabit);
                                resultText.setVisibility(View.VISIBLE);
                                confirmButton.setVisibility(View.GONE);
                                cancelButton.setVisibility(View.GONE);
                            } else {
                                Toast.makeText(HabitTrackerListPage.this, "Failed to reset habit logs: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        cancelButton.setOnClickListener(v -> {
            // Reset habit selection process
            TextView resultText = findViewById(R.id.result_text);
            selectedHabit = "";
            confirmButton.setVisibility(View.GONE);
            cancelButton.setVisibility(View.GONE);
            resultText.setVisibility(View.GONE);
            customSpinner.setHint("Select Habit");
            customSpinner.setText("");
        });

        backButton.setOnClickListener(view -> {
            Intent intent = new Intent(HabitTrackerListPage.this, HabitDecision.class);
            startActivity(intent);
        });

        logButton.setOnClickListener(view -> {
            Intent intent = new Intent(HabitTrackerListPage.this, HabitLoggingPage.class);
            startActivity(intent);
        });
    }

    private void showSearchableDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View customView = getLayoutInflater().inflate(R.layout.dialog_searchable_spinner, null);
        builder.setView(customView);

        AlertDialog dialog = builder.create();
        dialog.show();

        // Initialize views inside the dialog layout
        EditText searchBox = customView.findViewById(R.id.search_box);
        Spinner typeSpinner = customView.findViewById(R.id.type_spinner);
        Spinner impactSpinner = customView.findViewById(R.id.impact_spinner);
        ListView listView = customView.findViewById(R.id.list_view);
        Button personalizedButton = customView.findViewById(R.id.personalized_button); // Correctly reference button

        // Initialize dialogAdapter with filteredHabitList
        dialogAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, filteredHabitList);
        listView.setAdapter(dialogAdapter);

        // Set up spinners
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,
                new ArrayList<>(typeFilter.keySet()));
        typeAdapter.insert("All Types", 0);
        typeSpinner.setAdapter(typeAdapter);

        ArrayAdapter<String> impactAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,
                new ArrayList<>(impactFilter.keySet()));
        impactAdapter.insert("All Impacts", 0);
        impactSpinner.setAdapter(impactAdapter);

        // Text search filter
        searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                applyFilters(s.toString(), typeSpinner.getSelectedItem().toString(), impactSpinner.getSelectedItem().toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Type filter selection
        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                applyFilters(searchBox.getText().toString(), typeSpinner.getSelectedItem().toString(), impactSpinner.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Impact filter selection
        impactSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                applyFilters(searchBox.getText().toString(), typeSpinner.getSelectedItem().toString(), impactSpinner.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        logDatabase = FirebaseDatabase.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        userID = currentUser.getUid();
        dailyHabitTrackerRef = logDatabase.getReference("DailySurveyCO2").child(userID);

        dailyHabitTrackerRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String greatestDate = null;
                double greatestEmission = 0.0;
                for (DataSnapshot dateSnapshot : snapshot.getChildren()){
                    String date = dateSnapshot.getKey();
                    double dailyTotal = dateSnapshot.child("dailyTotal").getValue(Double.class);

                    if (dailyTotal > greatestEmission){
                        greatestEmission = dailyTotal;
                        greatestDate = date;
                    }
                }

                DataSnapshot maxContributor = snapshot.child(greatestDate);
                String maxKey = null;
                double maxContribution = 0.0;

                for (DataSnapshot emissionSnapshot : maxContributor.getChildren()) {
                    String emissionType = emissionSnapshot.getKey();

                    // Exclude "dailyTotal" from the consideration
                    if (!"dailyTotal".equals(emissionType)) {
                        double doubleCO2 = emissionSnapshot.getValue(Double.class);

                        if (doubleCO2 > maxContribution) {
                            maxContribution = doubleCO2;
                            maxKey = emissionType;
                        }
                    }
                }

                contributorGreatest = maxKey;
                Log.d("DEBUG", "Value of contributorGreatest: " + contributorGreatest);

                // Handle personalized button click
                personalizedButton.setOnClickListener(v -> {
                    // Add logic for filtering based on the greatest contributor
                    if ("DrivePersonalVehicleDailyCO2".equals(contributorGreatest) ||
                            "TakePublicTransportationDailyCO2".equals(contributorGreatest) ||
                            "Flight (Short-Haul or Long-Haul)DailyCO2".equals(contributorGreatest)) {
                        filteredHabitList.clear();
                        filteredHabitList.addAll(typeFilter.getOrDefault("Transportation", new ArrayList<>()));
                        personalizedButton.setText("Now seeing personalized habit suggestions");
                    } else if ("EnergyBillDailyCO2".equals(contributorGreatest)) {
                        filteredHabitList.clear();
                        filteredHabitList.addAll(typeFilter.getOrDefault("Energy", new ArrayList<>()));
                        personalizedButton.setText("Now seeing personalized habit suggestions");
                    } else if ("MealDailyCO2".equals(contributorGreatest)) {
                        filteredHabitList.clear();
                        filteredHabitList.addAll(typeFilter.getOrDefault("Food", new ArrayList<>()));
                        personalizedButton.setText("Now seeing personalized habit suggestions");
                    } else if ("ElectronicsDailyCO2".equals(contributorGreatest) ||
                            "ClothesDailyCO2".equals(contributorGreatest) ||
                            "OtherPurchasesDailyCO2".equals(contributorGreatest)) {
                        filteredHabitList.clear();
                        filteredHabitList.addAll(typeFilter.getOrDefault("Consumption", new ArrayList<>()));
                        personalizedButton.setText("Now seeing personalized habit suggestions");
                    } else {
                        Toast.makeText(HabitTrackerListPage.this, "No specific habits found for the greatest contributor", Toast.LENGTH_SHORT).show();
                    }

                    dialogAdapter.notifyDataSetChanged(); // Refresh the list view
                });
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(HabitTrackerListPage.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

        listView.setOnItemClickListener((parent, view, position, id) -> {
            selectedHabit = dialogAdapter.getItem(position);
            customSpinner.setText(selectedHabit);
            confirmButton.setVisibility(View.VISIBLE);
            cancelButton.setVisibility(View.VISIBLE);
            dialog.dismiss();
        });
    }


    private void applyFilters(String searchText, String selectedType, String selectedImpact) {
        filteredHabitList.clear();

        List<String> typeFiltered;
        if ("All Types".equals(selectedType)) {
            typeFiltered = habitList;
        } else {
            typeFiltered = typeFilter.get(selectedType);
            if (typeFiltered == null) {
                typeFiltered = new ArrayList<>();
            }
        }

        List<String> impactFiltered;
        if ("All Impacts".equals(selectedImpact)) {
            impactFiltered = habitList;
        } else {
            impactFiltered = impactFilter.get(selectedImpact);
            if (impactFiltered == null) {
                impactFiltered = new ArrayList<>();
            }
        }

        for (String habit : habitList) {
            if (typeFiltered.contains(habit) && impactFiltered.contains(habit) &&
                    habit.toLowerCase().contains(searchText.toLowerCase())) {
                filteredHabitList.add(habit);
            }
        }

        // Refresh adapter with updated list
        dialogAdapter.notifyDataSetChanged();
    }

    private void initializeFilters() {
        typeFilter.put("Transportation", new ArrayList<>(List.of(
                "Walk to work/school/destinations",
                "Cycle to work/school/destinations",
                "Carpool to work/school/destinations",
                "Take public transportation to work/school/destinations",
                "Reduce idling time when driving to save fuel",
                "Switch to cycling or walking for short distances",
                "Combine errands into a single trip to reduce driving distances",
                "Eliminate non-essential flights, especially long-haul"
        )));
        typeFilter.put("Consumption", new ArrayList<>(List.of(
                "Buy fewer new clothes and opt for second-hand items",
                "Unplug and recycle old electronics responsibly",
                "Choose energy-efficient appliances",
                "Repair broken items instead of discarding them"
        )));
        typeFilter.put("Food", new ArrayList<>(List.of(
                "Introduce one plant-based meal per day",
                "Compost food scraps instead of discarding them",
                "Reduce portion sizes for meals",
                "Adopt a fully plant-based diet",
                "Eliminate food waste by meal planning and using leftovers",
                "Buy locally-sourced produce to reduce transportation emissions",
                "Buy seasonal produce to reduce energy-intensive farming practices"
        )));
        typeFilter.put("Energy", new ArrayList<>(List.of(
                "Unplug devices when not in use",
                "Switch to LED bulbs",
                "Use cold water for laundry",
                "Switch to renewable energy"
        )));

        impactFilter.put("Low Impact", new ArrayList<>(List.of(
                "Unplug devices when not in use",
                "Switch to LED bulbs",
                "Use cold water for laundry",
                "Reduce idling time when driving to save fuel",
                "Walk to work/school/destinations",
                "Buy fewer new clothes and opt for second-hand items",
                "Carpool to work/school/destinations",
                "Unplug and recycle old electronics responsibly"
        )));

        impactFilter.put("Medium Impact", new ArrayList<>(List.of(
                "Introduce one plant-based meal per day",
                "Use public transportation to work/school/destinations",
                "Switch to cycling or walking for short distances",
                "Combine errands into a single trip to reduce driving distances",
                "Buy locally-sourced produce to reduce transportation emissions",
                "Repair broken items instead of discarding them",
                "Compost food scraps instead of discarding them",
                "Reduce portion sizes for meals"
        )));

        impactFilter.put("High Impact", new ArrayList<>(List.of(
                "Adopt a fully plant-based diet",
                "Switch to renewable energy",
                "Eliminate non-essential flights, especially long-haul",
                "Eliminate food waste by meal planning and using leftovers",
                "Buy seasonal produce to reduce energy-intensive farming practices",
                "Choose energy-efficient appliances"
        )));
    }
}
