package net.robertx.planeteze_b07.dailySurvey;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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

public class QuestionnairePageQ1 extends AppCompatActivity {
    private EditText question2_answer, question3_answer;
    public static final Map<String, Object> data = new HashMap<>();
    public static final Map<String, Object> data1 = new HashMap<>();

    static String currentDate;

    public static String ChangedDate = "";
    FirebaseDatabase database;
    DatabaseReference dailySurveyReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_questionnaire_page_q1);

        question2_answer = findViewById(R.id.answer2_input_Q1);
        question3_answer = findViewById(R.id.answer3_input_Q1);


        Button submitbtn = findViewById(R.id.submit_button_Q1);
        database = FirebaseDatabase.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        String userID = currentUser.getUid();
        Log.d("TAG", "userID" + userID);
        dailySurveyReference = database.getReference("DailySurvey");
        currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        if (currentDate != QuestionnairePageQ1.ChangedDate && QuestionnairePageQ1.ChangedDate != ""){
            currentDate = QuestionnairePageQ1.ChangedDate;
        }
        dailySurveyReference = database.getReference("DailySurvey").child(userID).child(currentDate);


        submitbtn.setOnClickListener(v -> {
            if (!TextUtils.isEmpty(question2_answer.getText().toString()) || !TextUtils.isEmpty(question3_answer.getText().toString())) {
                Intent intent = new Intent(QuestionnairePageQ1.this, DailySurveyHomePage.class);
                startActivity(intent);


                data1.put("Drive Personal Vehicle", "Yes");
                data1.put("Distance Driven", question2_answer.getText().toString());
                data1.put("Change vehicle type", question3_answer.getText().toString());


                dailySurveyReference.updateChildren(data1).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        CO2EmissionUpdater.fetchDataAndRecalculate(userID, currentDate);
                        // Success message (optional)
                        Toast.makeText(QuestionnairePageQ1.this, "Data saved successfully!", Toast.LENGTH_SHORT).show();
                    } else {
                        // Error message
                        Toast.makeText(QuestionnairePageQ1.this, "Failed to save data: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });


            }
           else {
                Toast.makeText(QuestionnairePageQ1.this, "Please fill out the required fields", Toast.LENGTH_SHORT).show();
            }

        });

        Button backbtn = findViewById(R.id.back_button_Q1);
        backbtn.setOnClickListener(v -> {
            Intent intent = new Intent(QuestionnairePageQ1.this, DailySurveyHomePage.class);
            startActivity(intent);


            data.put("Drive Personal Vehicle", "No");
            data.put("Distance Driven", "0");
            data.put("Change vehicle type", "No");


        });


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}