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

public class QuestionnairePageQ3 extends AppCompatActivity {

    private Button nxtbtn, option1btn, option2btn, prevbtn;
    private EditText question2_answer;
    private TextView question2;

    String buttonClicked;
    FirebaseDatabase database;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_questionnaire_page_q3);

        //options btn
        option1btn = findViewById(R.id.Cycling_button_Q3);
        question2 = findViewById(R.id.question2Text_Q3);
        question2_answer = findViewById(R.id.answer2_input_Q3);
        option2btn = findViewById(R.id.Walking_button_Q3);

        option1btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                question2.setVisibility(View.VISIBLE);
                question2_answer.setVisibility(View.VISIBLE);
                buttonClicked = "Cycling";
            }
        });
        option2btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                question2.setVisibility(View.VISIBLE);
                question2_answer.setVisibility(View.VISIBLE);
                buttonClicked = "Walking";
            }
        });

        //next button
        nxtbtn = findViewById(R.id.next_button_Q3);
        database = FirebaseDatabase.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        String userID = currentUser.getUid();
        databaseReference = database.getReference("Users").child("W35Qr6MzplfED39mMHhiYRLKMYO2");
        nxtbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(question2_answer.getText().toString())) {
                    Intent intent = new Intent(QuestionnairePageQ3.this, QuestionnairePageQ4.class);
                    startActivity(intent);

                    Map<String, Object> newField = new HashMap<>();
                    newField.put("Cycling or Walking", buttonClicked);
                    newField.put("Distance cycled or walked", question2_answer.getText().toString());

                    databaseReference.updateChildren(newField).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Success message (optional)
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
            }
        });

        prevbtn = findViewById(R.id.previous_button_Q3);
        prevbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(QuestionnairePageQ3.this, QuestionnairePageQ2.class);
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