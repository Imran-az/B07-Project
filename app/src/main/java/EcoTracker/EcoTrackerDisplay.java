package EcoTracker;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import net.robertx.planeteze_b07.DailySurvey.QuestionnairePageQ1;
import net.robertx.planeteze_b07.DailySurvey.QuestionnairePageQ9;
import net.robertx.planeteze_b07.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import net.robertx.planeteze_b07.R;

public class EcoTrackerDisplay extends AppCompatActivity {

    FirebaseDatabase database, newDatabase;
    DatabaseReference dailySurveyReference, dailyEmissionReference;

    public void updateDailyEmissions(HashMap<String, Double> dataCO2) {
        // The updateChildren method requires a Map<String, Object>
        Map<String, Object> dataToUpdate = new HashMap<>();
        for (Map.Entry<String, Double> entry : dataCO2.entrySet()) {
            dataToUpdate.put(entry.getKey(), entry.getValue());
        }

        // Now update Firebase with the correct data
        dailyEmissionReference.updateChildren(dataToUpdate).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Success message (optional)
                Toast.makeText(EcoTrackerDisplay.this, "Data saved successfully!", Toast.LENGTH_SHORT).show();
            } else {
                // Error message
                Toast.makeText(EcoTrackerDisplay.this, "Failed to save data: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_eco_tracker_display);

        database = FirebaseDatabase.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        String userID = currentUser.getUid();
        dailySurveyReference = database.getReference("DailySurvey");
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        dailySurveyReference = database.getReference("DailySurvey").child(userID).child(currentDate);

        newDatabase = FirebaseDatabase.getInstance();
        dailyEmissionReference = newDatabase.getReference("DailySurveyCO2");
        dailyEmissionReference = newDatabase.getReference("DailySurveyCO2").child(userID).child(currentDate);

        HashMap<String, String> data = new HashMap<>();

        // Fetches data from the DailySurvey Root
        dailySurveyReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    data.clear();

                    for (DataSnapshot dateSnapshot : dataSnapshot.getChildren()) {
                        // Get the date and CO2 emission value from each snapshot
                        String question = dateSnapshot.getKey();
                        String answer = (String) dateSnapshot.getValue();

                        // Add the data to the HashMap
                        if (question != null && answer != null) {
                            data.put(question, answer);
                        }
                    }
                    DailyCalculation currentEmissions = new DailyCalculation(data);
                    HashMap<String, Double> dataCO2 = currentEmissions.toHashMap();

                    updateDailyEmissions(dataCO2);
                }
                else{
                    Toast.makeText(EcoTrackerDisplay.this, "No data found under specified date", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(EcoTrackerDisplay.this, "Data not saved", Toast.LENGTH_SHORT).show();
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}