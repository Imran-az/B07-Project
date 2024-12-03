package net.robertx.planeteze_b07.DailySurvey;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import net.robertx.planeteze_b07.EcoTracker.CO2EmissionUpdater;
import net.robertx.planeteze_b07.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class QuestionnairePageQ3 extends AppCompatActivity {

    private EditText question2_answer;

    String currentDate;
    public static Map<String, Object> data3 = new HashMap<>();

    FirebaseDatabase database;
    DatabaseReference dailySurveyReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_questionnaire_page_q3);

        question2_answer = findViewById(R.id.answer2_input_Q3);



        //next button
        Button submitbtn = findViewById(R.id.submit_button_Q3);
        database = FirebaseDatabase.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        String userID = currentUser.getUid();
        database.getReference("Users").child("W35Qr6MzplfED39mMHhiYRLKMYO2");
        dailySurveyReference = database.getReference("DailySurvey");
        currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        if (currentDate != QuestionnairePageQ1.ChangedDate && QuestionnairePageQ1.ChangedDate != ""){
            currentDate = QuestionnairePageQ1.ChangedDate;
        }
        dailySurveyReference = database.getReference("DailySurvey").child(userID).child(currentDate);
        submitbtn.setOnClickListener(v -> {
            if (!TextUtils.isEmpty(question2_answer.getText().toString())) {
                Intent intent = new Intent(QuestionnairePageQ3.this, DailySurveyHomePage.class);
                startActivity(intent);

                //QuestionnairePageQ1 prev_data = new QuestionnairePageQ1();
                data3.put("Distance cycled or walked", question2_answer.getText().toString());

                dailySurveyReference.updateChildren(data3).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Success message (optional)
                        CO2EmissionUpdater.fetchDataAndRecalculate(userID, currentDate);
                        Toast.makeText(QuestionnairePageQ3.this, "Data saved successfully!", Toast.LENGTH_SHORT).show();
                    } else {
                        // Error message
                        Toast.makeText(QuestionnairePageQ3.this, "Failed to save data: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
            else {
                Toast.makeText(QuestionnairePageQ3.this, "Please fill out the required fields", Toast.LENGTH_SHORT).show();
            }
        });

        Button backbtn = findViewById(R.id.back_button_Q3);
        backbtn.setOnClickListener(v -> {
            Intent intent = new Intent(QuestionnairePageQ3.this, DailySurveyHomePage.class);
            startActivity(intent);
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}