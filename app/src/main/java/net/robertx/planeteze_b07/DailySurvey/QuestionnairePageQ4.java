package net.robertx.planeteze_b07.DailySurvey;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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

import java.util.HashMap;
import java.util.Map;

public class QuestionnairePageQ4 extends AppCompatActivity {
    private Button nxtbtn, option1btn, option2btn, prevbtn;
    private EditText question2_answer, question3_answer;
    private TextView question2, question3;
    String option;
    FirebaseDatabase database;
    DatabaseReference databaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_questionnaire_page_q4);

        //yesbtn
        option1btn = findViewById(R.id.option1_button_Q4);
        option2btn = findViewById(R.id.option2_button_Q4);
        question2 = findViewById(R.id.question2Text_Q4);
        question2_answer = findViewById(R.id.answer2_input_Q4);

        question3 = findViewById(R.id.question3_text_view_Q4);
        question3_answer = findViewById(R.id.answer3_input_Q4);

        option1btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                helperMethods.setVisibility1(question2, question3, question2_answer, question3_answer);
                option = "Long-haul";
            }
        });
        option2btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                helperMethods.setVisibility1(question2, question3, question2_answer, question3_answer);
                option = "Short-haul";
            }
        });

        //next button
        nxtbtn = findViewById(R.id.next_button_Q4);
        database = FirebaseDatabase.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        String userID = currentUser.getUid();
        databaseReference = database.getReference("Users").child("W35Qr6MzplfED39mMHhiYRLKMYO2");
        nxtbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(question2_answer.getText().toString())) {
                    Intent intent = new Intent(QuestionnairePageQ4.this, QuestionnairePageQ5.class);
                    startActivity(intent);
                    Map<String, Object> newField = new HashMap<>();
                    newField.put("Flight", option);
                    newField.put("Number of flights taken today", question2_answer.getText().toString());
                    newField.put("Distance traveled", question3_answer.getText().toString());

                    databaseReference.updateChildren(newField).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Success message (optional)
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
            }
        });
        prevbtn = findViewById(R.id.previous_button_Q4);
        prevbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(QuestionnairePageQ4.this, QuestionnairePageQ3.class);
                startActivity(intent);
            }
        });


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}