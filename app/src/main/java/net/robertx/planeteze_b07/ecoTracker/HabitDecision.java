package net.robertx.planeteze_b07.ecoTracker;

import static net.robertx.planeteze_b07.R.*;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.appbar.MaterialToolbar;

import net.robertx.planeteze_b07.R;

/**
 * Activity for the Habit Decision screen.
 *
 * This class provides options for users to log their current habit or select a new habit to track.
 * It sets up navigation to the habit logging page and the habit tracker list page.
 */
public class HabitDecision extends AppCompatActivity {

    /** Button for navigating to the habit logging page. */
    public Button button1;

    /** Button for navigating to the habit tracker list page to select a new habit. */
    public Button button2;

    /**
     * Initializes the Habit Decision activity.
     * Sets up buttons for navigating to other activities and applies edge-to-edge window insets.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down, this Bundle contains the most recent data supplied; otherwise, it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_habit_decision);

        button1 = findViewById(R.id.oldhabit);
        button2 = findViewById(id.newhabit);

        MaterialToolbar toolbar = findViewById(R.id.toolbar_habit_decision);
        toolbar.setNavigationOnClickListener(v -> finish());

        button1.setOnClickListener(view -> {
            Intent intent = new Intent(HabitDecision.this, HabitLoggingPage.class);
            startActivity(intent);
        });

        button2.setOnClickListener(view -> {
            Intent intent = new Intent(HabitDecision.this, HabitTrackerListPage.class);
            startActivity(intent);
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}