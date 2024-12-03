package net.robertx.planeteze_b07.DailySurvey;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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

public class QuestionnairePageQ9 extends AppCompatActivity {

    EditText q2_ans;
    EditText q3_ans;
    EditText q4_ans;
    TextView q2_que;
    TextView q3_que;
    TextView q4_que;
    final Map<String, Object> data9 = new HashMap<>();
    FirebaseDatabase database;

    String currentDate;
    DatabaseReference dailySurveyReference;
    boolean e = false;
    boolean g = false;
    boolean w = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_questionnaire_page_q9);

        q2_que = findViewById(R.id.question2_text_view);
        q2_ans = findViewById(R.id.answer2_input);
        q3_que = findViewById(R.id.question3_text_view);
        q3_ans = findViewById(R.id.answer3_input);
        q4_que = findViewById(R.id.question4_text_view);
        q4_ans = findViewById(R.id.answer4_input);



        database = FirebaseDatabase.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        String userID = currentUser.getUid();
        dailySurveyReference = database.getReference("DailySurvey");
        currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        dailySurveyReference = database.getReference("DailySurvey").child("TestUser").child(currentDate);

        Button submitButton = findViewById(R.id.submit_button_Q9);
        //QuestionnairePageQ1 prev_data = new QuestionnairePageQ1();

        submitButton.setOnClickListener(v -> {
            Intent intent = new Intent(QuestionnairePageQ9.this, DailySurveyHomePage.class);
            startActivity(intent);

            String q2, q3, q4;
            q2 = q2_ans.getText().toString();
            q3 = q3_ans.getText().toString();
            q4 = q4_ans.getText().toString();

            String answer1, answer2, answer3;
            answer1 = "0";
            answer2 = "0";
            answer3 = "0";

            if(!q2.equals("0")){
                answer1 = q2;
            }

            if(!q3.equals("0")){
                answer2 = q3;
            }

            if(!q4.equals("0")){
                answer3 = q4;
            }

            //Log.d("HashMapData", "Current data: " + QuestionnairePageQ1.data.toString());
            data9.put("Electricity Paid", answer1);
            data9.put("Gas Paid", answer2);
            data9.put("Water Paid", answer3);
            //Log.d("HashMapData", "Current data: " + QuestionnairePageQ1.data.toString());

            dailySurveyReference.updateChildren(data9).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    // Success message (optional)
                    CO2EmissionUpdater.fetchDataAndRecalculate(userID, currentDate);
                    Toast.makeText(QuestionnairePageQ9.this, "Data saved successfully!", Toast.LENGTH_SHORT).show();
                } else {
                    // Error message
                    Toast.makeText(QuestionnairePageQ9.this, "Failed to save data: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        Button backButton = findViewById(R.id.back_button_Q9);
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(QuestionnairePageQ9.this, QuestionnairePageQ8.class);
            startActivity(intent);
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}