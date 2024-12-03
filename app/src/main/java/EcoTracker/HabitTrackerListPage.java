package EcoTracker;

import android.os.Bundle;
import android.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import net.robertx.planeteze_b07.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class HabitTrackerListPage extends AppCompatActivity {

    private Button confirmButton, cancelButton;
    private List<String> habitList, filteredHabitList;
    private ArrayAdapter<String> dialogAdapter;
    private String selectedHabit = "";
    TextView customSpinner, resultText;
    FirebaseDatabase logDatabase;
    DatabaseReference habitLogReference;
    private DatabaseReference habitLogsRef, dailyHabitTrackerRef;
    private String userID;
    public static Map<String, Object> logData = new HashMap<>();
    public static Map<String, ArrayList<String>> typeFilter = new HashMap<>();
    public static Map<String, ArrayList<String>> impactFilter = new HashMap<>();
    int counterFull, counterLogged;

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
        dailyHabitTrackerRef = logDatabase.getReference("DailyHabitTracker").child(userID); // Updated reference
        habitLogReference = dailyHabitTrackerRef.child("SelectedHabit"); // Updated to use DailyHabitTracker
        DatabaseReference habitLogsRef = dailyHabitTrackerRef.child("HabitLogs"); // Updated reference to store logs

        customSpinner = findViewById(R.id.custom_spinner);
        resultText = findViewById(R.id.result_text);
        confirmButton = findViewById(R.id.confirm_button);
        cancelButton = findViewById(R.id.cancel_button);

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
        habitList.add("Switch to an electric vehicle (EV)");
        habitList.add("Eliminate non-essential flights, especially long-haul");
        habitList.add("Adopt a fully plant-based diet");
        habitList.add("Buy seasonal produce to reduce energy-intensive farming practices");
        habitList.add("Buy fewer new clothes and opt for second-hand items");
        habitList.add("Unplug and recycle old electronics responsibly");
        habitList.add("Choose energy-efficient appliances");
        habitList.add("Reduce idling time when driving to save fuel");
        habitList.add("Use fuel-efficient or hybrid vehicles");
        habitList.add("Switch to renewable energy");
        habitList.add("Eliminate food waste by meal planning and using leftovers");
        habitList.add("Buy locally-sourced produce to reduce transportation emissions");

        // Initialize filteredHabitList as a copy of habitList
        filteredHabitList = new ArrayList<>(habitList);

        // Initialize filters
        initializeFilters();

        customSpinner.setOnClickListener(v -> showSearchableDialog());

        confirmButton.setOnClickListener(v -> {
            if (!selectedHabit.isEmpty()) {
                // Clear the existing habit logs for the selected habit
                habitLogsRef.removeValue() // This removes all logs under HabitLogs
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                // Now set the new habit and log for today
                                habitLogReference.setValue(selectedHabit);

                                counterFull = 1;

                                // Save the current date under HabitLogs node with "Not Completed"
                                habitLogsRef.child(currentDate).setValue("Not Completed")
                                        .addOnCompleteListener(task1 -> {
                                            if (task1.isSuccessful()) {
                                                Toast.makeText(HabitTrackerListPage.this, "Habit confirmed and logged for today", Toast.LENGTH_SHORT).show();
                                                resultText.setText("Selected Habit: " + selectedHabit);
                                                resultText.setVisibility(View.VISIBLE);
                                                confirmButton.setVisibility(View.GONE);
                                                cancelButton.setVisibility(View.GONE);
                                            } else {
                                                Toast.makeText(HabitTrackerListPage.this, "Failed to log habit: " + task1.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
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
    }

    private void showSearchableDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View customView = getLayoutInflater().inflate(R.layout.dialog_searchable_spinner, null);
        builder.setView(customView);

        AlertDialog dialog = builder.create();
        dialog.show();

        EditText searchBox = customView.findViewById(R.id.search_box);
        Spinner typeSpinner = customView.findViewById(R.id.type_spinner);
        Spinner impactSpinner = customView.findViewById(R.id.impact_spinner);
        ListView listView = customView.findViewById(R.id.list_view);

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

        // Apply type filter
        List<String> typeFiltered = "All Types".equals(selectedType)
                ? habitList
                : typeFilter.getOrDefault(selectedType, new ArrayList<>());

        // Apply impact filter
        List<String> impactFiltered = "All Impacts".equals(selectedImpact)
                ? habitList
                : impactFilter.getOrDefault(selectedImpact, new ArrayList<>());

        // Combine filters and apply search text
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
                "Use fuel-efficient or hybrid vehicles",
                "Switch to cycling or walking for short distances",
                "Combine errands into a single trip to reduce driving distances",
                "Switch to an electric vehicle (EV)",
                "Eliminate non-essential flights, especially long-haul"
        )));
        typeFilter.put("Consumption", new ArrayList<>(List.of(
                "Buy fewer new clothes and opt for second-hand items",
                "Unplug and recycle old electronics responsibly",
                "Choose energy-efficient appliances"
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
                "Buy fewer new clothes and opt for second-hand items"
        )));
        impactFilter.put("Medium Impact", new ArrayList<>(List.of(
                "Introduce one plant-based meal per day",
                "Use public transportation to work/school/destinations",
                "Switch to cycling or walking for short distances",
                "Combine errands into a single trip to reduce driving distances",
                "Buy locally-sourced produce to reduce transportation emissions"
        )));
        impactFilter.put("High Impact", new ArrayList<>(List.of(
                "Adopt a fully plant-based diet",
                "Switch to renewable energy",
                "Eliminate non-essential flights, especially long-haul",
                "Switch to an electric vehicle (EV)",
                "Eliminate food waste by meal planning and using leftovers",
                "Buy seasonal produce to reduce energy-intensive farming practices"
        )));
    }
}
