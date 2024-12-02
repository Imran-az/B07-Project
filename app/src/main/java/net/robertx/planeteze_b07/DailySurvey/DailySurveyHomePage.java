package net.robertx.planeteze_b07.DailySurvey;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.appbar.MaterialToolbar;

import net.robertx.planeteze_b07.MainActivity;
import net.robertx.planeteze_b07.R;

public class DailySurveyHomePage extends AppCompatActivity {
    Button q1, q2, q3, q4, q5, q6, q7, q8, q9;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_daily_survey_home_page);

        q1 = findViewById(R.id.Question1_DailySurvey);
        q2 = findViewById(R.id.Question2_DailySurvey);
        q3 = findViewById(R.id.Question3_DailySurvey);
        q4 = findViewById(R.id.Question4_DailySurvey);
        q5 = findViewById(R.id.Question5_DailySurvey);
        q6 = findViewById(R.id.Question6_DailySurvey);
        q7 = findViewById(R.id.Question7_DailySurvey);
        q8 = findViewById(R.id.Question8_DailySurvey);
        q9 = findViewById(R.id.Question9_DailySurvey);

        MaterialToolbar toolbar = findViewById(R.id.toolbar_question_list);
        toolbar.setNavigationOnClickListener(v -> {
            // Create an Intent to go to the other Activity
            Intent intent = new Intent(DailySurveyHomePage.this, MainActivity.class);
            startActivity(intent);  // Start the new Activity

            // Optionally finish the current Activity
            finish();
        });

        q1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DailySurveyHomePage.this, QuestionnairePageQ1.class);
                startActivity(intent);
            }
        });
        q2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DailySurveyHomePage.this, QuestionnairePageQ2.class);
                startActivity(intent);
            }
        });
        q3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DailySurveyHomePage.this, QuestionnairePageQ3.class);
                startActivity(intent);
            }
        });
        q4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DailySurveyHomePage.this, QuestionnairePageQ4.class);
                startActivity(intent);
            }
        });
        q5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DailySurveyHomePage.this, QuestionnairePageQ5.class);
                startActivity(intent);
            }
        });
        q6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DailySurveyHomePage.this, QuestionnairePageQ6.class);
                startActivity(intent);
            }
        });
        q7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DailySurveyHomePage.this, QuestionnairePageQ7.class);
                startActivity(intent);
            }
        });
        q8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DailySurveyHomePage.this, QuestionnairePageQ8.class);
                startActivity(intent);
            }
        });
        q9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DailySurveyHomePage.this, QuestionnairePageQ9.class);
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