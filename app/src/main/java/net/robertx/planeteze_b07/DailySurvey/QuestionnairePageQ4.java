package net.robertx.planeteze_b07.DailySurvey;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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

public class QuestionnairePageQ4 extends AppCompatActivity {
    public Button submitButton, backButton;
    public EditText question2_answer, question3_answer;
    public TextView question2, question3;
    public static Map<String, Object> data4 = new HashMap<>();

    String currentDate;
    FirebaseDatabase database;
    DatabaseReference dailySurveyReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_questionnaire_page_q4);

        question2 = findViewById(R.id.question2Text_Q4);
        question2_answer = findViewById(R.id.answer2_input_Q4);

        question3 = findViewById(R.id.question3_text_view_Q4);
        question3_answer = findViewById(R.id.answer3_input_Q4);

        //next button
        submitButton = findViewById(R.id.submit_button_Q4);
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
        submitButton.setOnClickListener(v -> {
            if (!TextUtils.isEmpty(question2_answer.getText().toString())) {
                Intent intent = new Intent(QuestionnairePageQ4.this, DailySurveyHomePage.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

                data4.put("Number of flights taken today", question2_answer.getText().toString());
                int distance_travelled = Integer.parseInt(question3_answer.getText().toString());

                String answer;

                if (distance_travelled >= 1500){
                    answer  = "Long-Haul";
                }
                else{
                    answer = "Short-Haul";
                }
                data4.put("Distance traveled", answer);

                dailySurveyReference.updateChildren(data4).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Success message (optional)
                        CO2EmissionUpdater.fetchDataAndRecalculate(userID, currentDate);
                        Toast.makeText(QuestionnairePageQ4.this, "Data saved successfully!", Toast.LENGTH_SHORT).show();
                    } else {
                        // Error message
                        Toast.makeText(QuestionnairePageQ4.this, "Failed to save data: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            }
            else {
                Toast.makeText(QuestionnairePageQ4.this, "Please fill out the required fields", Toast.LENGTH_SHORT).show();
            }
        });
        backButton = findViewById(R.id.back_button_Q4);
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(QuestionnairePageQ4.this, DailySurveyHomePage.class);
            startActivity(intent);
        });


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}