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

public class QuestionnairePageQ1 extends AppCompatActivity {
    private Button nxtbtn, yesbtn, nobtn;
    private EditText question2_answer, question3_answer;
    private TextView question2, question3;
    public static Map<String, Object> data = new HashMap<>();
    public static Map<String, Object> data1 = new HashMap<>();

    FirebaseDatabase database;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_questionnaire_page_q1);
        helperMethods helper = new helperMethods();

        //yesbtn
        yesbtn = findViewById(R.id.yes_button_Q1);
        question2 = findViewById(R.id.question2Text_Q1);
        question2_answer = findViewById(R.id.answer2_input_Q1);
        question3 = findViewById(R.id.question3_text_Q1);
        question3_answer = findViewById(R.id.answer3_input_Q1);
        yesbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                helperMethods.setVisibility1(question2, question3, question2_answer, question3_answer);
            }
        });
        //next button
        nxtbtn = findViewById(R.id.next_button_Q1);
        database = FirebaseDatabase.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        String userID = currentUser.getUid();
        databaseReference = database.getReference("Users").child("userID");
        nxtbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(question2_answer.getText().toString()) || !TextUtils.isEmpty(question3_answer.getText().toString())) {
                    Intent intent = new Intent(QuestionnairePageQ1.this, QuestionnairePageQ2.class);
                    startActivity(intent);

                    data.put("Drive Personal Vehicle", "Yes");
                    data.put("Distance Driven", question2_answer.getText().toString());
                    data.put("Change vehicle type", question3_answer.getText().toString());

                }
                else {
                    Toast.makeText(QuestionnairePageQ1.this, "Please fill out the required fields", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //nobtn
        nobtn = findViewById(R.id.no_button_Q1);
        nobtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(QuestionnairePageQ1.this, QuestionnairePageQ2.class);
                startActivity(intent);


                data.put("Drive Personal Vehicle", "No");
                data.put("Distance Driven", "0");
                data.put("Change vehicle type", "No");

            }
        });


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}