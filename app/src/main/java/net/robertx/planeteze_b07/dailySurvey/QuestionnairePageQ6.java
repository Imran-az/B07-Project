package net.robertx.planeteze_b07.dailySurvey;

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

import net.robertx.planeteze_b07.ecoTracker.CO2EmissionUpdater;
import net.robertx.planeteze_b07.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class QuestionnairePageQ6 extends AppCompatActivity {

    EditText q2_ans;
    TextView q2_que;
    final Map<String, Object> data6 = new HashMap<>();
    FirebaseDatabase database;

    String currentDate;
    DatabaseReference dailySurveyReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_questionnaire_page_q6);

        Button submitButton = findViewById(R.id.submit_button_Q6);
        q2_que = findViewById(R.id.question2_text_view);
        q2_ans = findViewById(R.id.answer2_input);

        submitButton.setOnClickListener(v -> helperMethods.setVisibility2(q2_que, q2_ans));

        String q1, q2;
        q1 = "Buy New Clothes";
        q2 = String.valueOf(q2_que.getText());

//        next_btn = findViewById(R.id.next_button);
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
            if (!TextUtils.isEmpty(String.valueOf(q2_ans.getText()))){
                String answer1, answer2;
                answer1 = "Yes";
                answer2 = String.valueOf(q2_ans.getText());

                data6.put(q1, answer1);
                data6.put(q2, answer2);

                dailySurveyReference.updateChildren(data6).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Success message (optional)
                        CO2EmissionUpdater.fetchDataAndRecalculate(userID, currentDate);
                        Toast.makeText(QuestionnairePageQ6.this, "Data saved successfully!", Toast.LENGTH_SHORT).show();
                    } else {
                        // Error message
                        Toast.makeText(QuestionnairePageQ6.this, "Failed to save data: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
            else{
                Toast.makeText(QuestionnairePageQ6.this, "Please fill out the required fields", Toast.LENGTH_SHORT).show();
            }
            finish();
        });

        Button backButton = findViewById(R.id.back_button_Q6);
        backButton.setOnClickListener(v -> finish());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}