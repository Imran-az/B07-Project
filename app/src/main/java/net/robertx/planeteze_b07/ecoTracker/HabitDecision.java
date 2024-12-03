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

public class HabitDecision extends AppCompatActivity {

    public Button button1, button2;

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