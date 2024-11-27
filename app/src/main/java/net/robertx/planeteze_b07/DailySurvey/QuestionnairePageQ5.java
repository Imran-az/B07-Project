package net.robertx.planeteze_b07.DailySurvey;

import android.annotation.SuppressLint;
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

public class QuestionnairePageQ5 extends AppCompatActivity {

    Button yes_btn, no_btn, previous_btn, next_btn;
    EditText q1_ans, q2_ans, q3_ans;
    TextView q1_que, q2_que, q3_que;
    Map<String, Object> q5_data = new HashMap<>();
    FirebaseDatabase database;
    DatabaseReference databaseReference;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_questionnaire_page_q5);

        yes_btn = findViewById(R.id.yes_button);
        q2_que = findViewById(R.id.question2_text_view);
        q2_ans = findViewById(R.id.answer2_input);
        q3_que = findViewById(R.id.question3_text_view);
        q3_ans = findViewById(R.id.answer3_input);

        yes_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                helperMethods.setVisibility1(q2_que, q3_que, q2_ans, q3_ans);
            }
        });

        String q1, q2, q3;
        q1 = String.valueOf(q1_que.getText());
        q2 = String.valueOf(q2_que.getText());
        q3 = String.valueOf(q3_ans.getText());

        next_btn = findViewById(R.id.next_button);
        previous_btn = findViewById(R.id.previous_button);
        database = FirebaseDatabase.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        String userID = currentUser.getUid();
        databaseReference = database.getReference("Users").child(userID);

        next_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(String.valueOf(q2_ans.getText()))){
                    Intent intent = new Intent(QuestionnairePageQ5.this, QuestionnairePageQ6.class);
                    startActivity(intent);

                    String answer1, answer2, answer3;
                    answer1 = "Yes";
                    answer2 = String.valueOf(q2_ans.getText());
                    answer3 = String.valueOf(q3_ans.getText());

                    q5_data.put(q1, answer1);
                    q5_data.put(q2, answer2);
                    q5_data.put(q3, answer3);
                }
                else{
                    Toast.makeText(QuestionnairePageQ5.this, "Please fill out the required fields", Toast.LENGTH_SHORT).show();
                }
            }
        });

        previous_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(QuestionnairePageQ5.this, QuestionnairePageQ4.class);
                startActivity(intent);
            }
        });

        no_btn = findViewById(R.id.no_button);
        no_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(QuestionnairePageQ5.this, QuestionnairePageQ6.class);
                startActivity(intent);

                String answer1, answer2, answer3;
                answer1 = "No";
                answer2 = String.valueOf(0);
                answer3 = String.valueOf(0);

                q5_data.put(q1, answer1);
                q5_data.put(q2, answer2);
                q5_data.put(q3, answer3);
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}