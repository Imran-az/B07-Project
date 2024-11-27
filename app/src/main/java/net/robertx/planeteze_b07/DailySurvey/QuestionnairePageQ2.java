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

import java.util.HashMap;
import java.util.Map;

public class QuestionnairePageQ2 extends AppCompatActivity {
    private Button yesbtn, nextbtn, prevbtn, nobtn;
    private EditText question2_answer, question3_answer;
    private TextView question2, question3;

    FirebaseDatabase database;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_questionnaire_page_q2);

        //yesbtn
        yesbtn = findViewById(R.id.yes_button_Q2);
        question2 = findViewById(R.id.question2Text_Q2);
        question2_answer = findViewById(R.id.answer2_input_Q2);

        question3 = findViewById(R.id.question3_text_view_Q2);
        question3_answer = findViewById(R.id.answer3_input_Q2);

        yesbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                helperMethods.setVisibility1(question2, question3, question2_answer, question3_answer);
            }
        });

        String q1, q2, q3;
        q1 = "Take public transportation";
        q2 = "Type of public transportation";
        q3 = "Time spent on public transport";

        //next button
        nextbtn = findViewById(R.id.next_button_Q2);
        database = FirebaseDatabase.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        String userID = currentUser.getUid();
        nextbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(question2_answer.getText().toString()) || !TextUtils.isEmpty(question3_answer.getText().toString())) {
                    Intent intent = new Intent(QuestionnairePageQ2.this, QuestionnairePageQ3.class);
                    startActivity(intent);

                    String ans1, ans2, ans3;
                    ans1 = "Yes";
                    ans2 = question2_answer.getText().toString();
                    ans3 = question3_answer.getText().toString();


                    //QuestionnairePageQ1 prev_data = new QuestionnairePageQ1();

                    QuestionnairePageQ1.data.put(q1, ans1);
                    QuestionnairePageQ1.data.put(q2, ans2);
                    QuestionnairePageQ1.data.put(q3, ans3);

//                    Map<String, Object> newField = new HashMap<>();
//                    newField.put("Take public transportation", "Yes");
//                    newField.put("Type of public transportation", question2_answer.getText().toString());
//                    newField.put("Time spent on public transport", question3_answer.getText().toString());
//
//                    databaseReference.updateChildren(newField).addOnCompleteListener(task -> {
//                        if (task.isSuccessful()) {
//                            // Success message (optional)
//                            Toast.makeText(QuestionnairePageQ2.this, "Data saved successfully!", Toast.LENGTH_SHORT).show();
//                        } else {
//                            // Error message
//                            Toast.makeText(QuestionnairePageQ2.this, "Failed to save data: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
//                        }
//                    });
                } else {
                    Toast.makeText(QuestionnairePageQ2.this, "Please fill out the required fields", Toast.LENGTH_SHORT).show();
                }
            }
        });
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //No button
        nobtn = findViewById(R.id.no_button_Q2);
        nobtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(QuestionnairePageQ2.this, QuestionnairePageQ3.class);
                startActivity(intent);

                String ans1;
                Object ans2, ans3;
                ans1 = "No";
                ans2 = 0;
                ans3 = 0;

                //QuestionnairePageQ1 prev_data = new QuestionnairePageQ1();

                QuestionnairePageQ1.data.put(q1, ans1);
                QuestionnairePageQ1.data.put(q2, ans2);
                QuestionnairePageQ1.data.put(q3, ans3);

//                Map<String, Object> newField = new HashMap<>();
//                newField.put("Take public transportation", "No");
//                newField.put("Type of public transportation", 0);
//                newField.put("Time spent on public transport", 0);
//
//                databaseReference.updateChildren(newField).addOnCompleteListener(task -> {
//                    if (task.isSuccessful()) {
//                        // Success message (optional)
//                        Toast.makeText(QuestionnairePageQ2.this, "Data saved successfully!", Toast.LENGTH_SHORT).show();
//                    } else {
//                        // Error message
//                        Toast.makeText(QuestionnairePageQ2.this, "Failed to save data: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//                });
            }
        });
        prevbtn = findViewById(R.id.previous_button_Q2);
        prevbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(QuestionnairePageQ2.this, QuestionnairePageQ1.class);
                startActivity(intent);
            }
        });
    }
}