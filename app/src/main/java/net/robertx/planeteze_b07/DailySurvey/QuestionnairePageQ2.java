package net.robertx.planeteze_b07.DailySurvey;

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
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import net.robertx.planeteze_b07.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class QuestionnairePageQ2 extends AppCompatActivity {
    private Button submitbtn, backbtn;
    private EditText question2_answer, question3_answer;
    private TextView question2, question3;
    public static Map<String, Object> data2 = new HashMap<>();

    FirebaseDatabase database;
    DatabaseReference dailySurveyReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_questionnaire_page_q2);

        question2 = findViewById(R.id.question2Text_Q2);
        question2_answer = findViewById(R.id.answer2_input_Q2);
        question3 = findViewById(R.id.answer3_input_Q2);
        question3_answer = findViewById(R.id.answer3_input_Q2);

        String q1, q2, q3;
        q1 = "Take public transportation";
        q2 = "Type of public transportation";
        q3 = "Time spent on public transport";

        //next button
        submitbtn = findViewById(R.id.submit_button_Q2);
        database = FirebaseDatabase.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        String userID = currentUser.getUid();
        database.getReference("Users").child("W35Qr6MzplfED39mMHhiYRLKMYO2");
        dailySurveyReference = database.getReference("DailySurvey");
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        if (currentDate != QuestionnairePageQ1.ChangedDate && QuestionnairePageQ1.ChangedDate != ""){
            currentDate = QuestionnairePageQ1.ChangedDate;
        }
        dailySurveyReference = database.getReference("DailySurvey").child(userID).child(currentDate);
        submitbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(question2_answer.getText().toString()) || !TextUtils.isEmpty(question3_answer.getText().toString())) {
                    Intent intent = new Intent(QuestionnairePageQ2.this, DailySurveyHomePage.class);
                    startActivity(intent);

                    String ans1, ans2, ans3;
                    ans1 = "Yes";
                    ans2 = question2_answer.getText().toString();
                    ans3 = question3_answer.getText().toString();


                    //QuestionnairePageQ1 prev_data = new QuestionnairePageQ1();

                    data2.put(q1, ans1);
                    data2.put(q2, ans2);
                    data2.put(q3, ans3);

                    dailySurveyReference.updateChildren(data2).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Success message (optional)
                            Toast.makeText(QuestionnairePageQ2.this, "Data saved successfully!", Toast.LENGTH_SHORT).show();
                        } else {
                            // Error message
                            Toast.makeText(QuestionnairePageQ2.this, "Failed to save data: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(QuestionnairePageQ2.this, "Please fill out the required fields", Toast.LENGTH_SHORT).show();
                }
            }
        });

        backbtn = findViewById(R.id.back_button_Q2);
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(QuestionnairePageQ2.this, DailySurveyHomePage.class);
                startActivity(intent);
            }
        });
    }
}