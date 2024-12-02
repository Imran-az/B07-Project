package net.robertx.planeteze_b07;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;

import net.robertx.planeteze_b07.annualCarbonTracker.ResultsActivity;
import net.robertx.planeteze_b07.annual_carbon_footprint.AnnualCarbonFootprintSurvey;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private Button button, button2;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        button = findViewById(R.id.button22);
        button.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ResultsActivity.class);
            startActivity(intent);

        });

        findViewById(R.id.openDashboardBtn).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, Dashboard.class);
            startActivity(intent);
        });


        TextView usernameDisplay = findViewById(R.id.mainActivityUsernameDisplay);
        if (firebaseAuth.getCurrentUser() != null)
            usernameDisplay.setText("User ID: " + firebaseAuth.getCurrentUser().getUid() + "\n" + "Email: " + firebaseAuth.getCurrentUser().getEmail());


        findViewById(R.id.startAnnualCarbonFootprintSurvey).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AnnualCarbonFootprintSurvey.class);
            startActivity(intent);
        });
    }
}