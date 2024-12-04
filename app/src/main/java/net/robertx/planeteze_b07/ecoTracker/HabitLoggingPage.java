package net.robertx.planeteze_b07.ecoTracker;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.*;

import net.robertx.planeteze_b07.R;

/**
 * Activity for logging and tracking a user's daily habit progress.
 *
 * This class sets up the user interface, integrates with Firebase for habit tracking,
 * and allows users to log their habits, view progress, and manage habit data.
 */
public class HabitLoggingPage extends AppCompatActivity {

    /** Firebase reference to the user's selected habit. */
    private DatabaseReference habitRef;

    /** Firebase reference to the user's habit logs. */
    private DatabaseReference habitLogsRef;

    /** TextView to display the selected habit name. */
    private TextView trackingHabitText;

    /** Button to mark the habit as completed. */
    private Button completeHabitButton;

    /** Flag to determine whether a habit is selected. */
    private boolean isHabitSelected = false;

    /**
     * Initializes the Habit Logging Page activity.
     *
     * This method sets up the user interface, initializes Firebase references,
     * and configures event listeners for logging habits, checking progress, and
     * navigating to the habit selection page.
     *
     * @param savedInstanceState a {@link Bundle} containing the activity's previously saved state, or null if this is a new instance.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_habit_logging_page);

        MaterialToolbar toolbar = findViewById(R.id.toolbar_question_list);
        toolbar.setNavigationOnClickListener(v -> finish());

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        String userID = currentUser != null ? currentUser.getUid() : null;
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        habitRef = database.getReference("DailyHabitTracker").child(userID).child("SelectedHabit");
        habitLogsRef = database.getReference("DailyHabitTracker").child(userID).child("HabitLogs");

        trackingHabitText = findViewById(R.id.tracking_habit_text);
        completeHabitButton = findViewById(R.id.complete_habit_button);
        Button changeButton = findViewById(R.id.change_habit);

        habitRef.child("habitName").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String habitName = dataSnapshot.getValue(String.class);
                    trackingHabitText.setText("Habit: " + habitName);
                    isHabitSelected = true;
                } else {
                    trackingHabitText.setText("Habit: None Selected");
                    isHabitSelected = false;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                trackingHabitText.setText("Habit: Error Loading");
                isHabitSelected = false;
                Toast.makeText(HabitLoggingPage.this, "Failed to load habit: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        habitLogsRef.child(currentDate).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && "Completed".equals(dataSnapshot.getValue(String.class))) {
                    completeHabitButton.setText("Completed for the Day");
                    completeHabitButton.setEnabled(false);
                } else {
                    completeHabitButton.setText("Mark as Completed");
                    completeHabitButton.setEnabled(true);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(HabitLoggingPage.this, "Failed to check completion status: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        changeButton.setOnClickListener(view -> {
            Intent intent = new Intent(HabitLoggingPage.this, HabitTrackerListPage.class);
            startActivity(intent);
        });

        completeHabitButton.setOnClickListener(v -> {
            if (!isHabitSelected) {
                Toast.makeText(HabitLoggingPage.this, "No habit selected. Please select a habit first.", Toast.LENGTH_SHORT).show();
                return;
            }

            habitLogsRef.child(currentDate).setValue("Completed").addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    completeHabitButton.setText("Completed for the Day");
                    completeHabitButton.setEnabled(false);
                    Toast.makeText(HabitLoggingPage.this, "Habit marked as completed!", Toast.LENGTH_SHORT).show();

                    updateProgressBar();
                } else {
                    Toast.makeText(HabitLoggingPage.this, "Failed to update habit status.", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    /**
     * Updates the progress bar based on the user's habit logging history.
     *
     * This method calculates the percentage of days the habit has been logged
     * since the start date and updates the progress bar and progress text accordingly.
     * It retrieves data from Firebase to determine the start date and the number
     * of completed habit logs.
     */
    private void updateProgressBar() {
        habitRef.child("startDate").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot startSnapshot) {
                if (startSnapshot.exists()) {
                    String startDate = startSnapshot.getValue(String.class);
                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                        Date start = sdf.parse(startDate);
                        Date current = new Date();

                        if (start != null) {
                            long totalDays = (current.getTime() - start.getTime()) / (1000 * 60 * 60 * 24) + 1;

                            if (totalDays <= 0) {
                                Toast.makeText(HabitLoggingPage.this, "Invalid total days calculation.", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            habitLogsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot logSnapshot) {
                                    int completedDays = 0;

                                    for (DataSnapshot log : logSnapshot.getChildren()) {
                                        if ("Completed".equals(log.getValue(String.class))) {
                                            completedDays++;
                                        }
                                    }

                                    float progress = (completedDays / (float) totalDays) * 100;

                                    progress = Math.min(progress, 100);

                                    ProgressBar progressBar = findViewById(R.id.habit_progress_bar);
                                    TextView progressText = findViewById(R.id.progress_text);

                                    progressBar.setProgress((int) progress);
                                    progressBar.setVisibility(View.VISIBLE);

                                    progressText.setText(String.format(Locale.getDefault(), "You’ve logged your habit %.2f%% of the time!", progress));
                                    progressText.setVisibility(View.VISIBLE);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    Toast.makeText(HabitLoggingPage.this, "Failed to load logs: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(HabitLoggingPage.this, "Error parsing start date.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(HabitLoggingPage.this, "Start date not found in the database.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(HabitLoggingPage.this, "Failed to load start date: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}