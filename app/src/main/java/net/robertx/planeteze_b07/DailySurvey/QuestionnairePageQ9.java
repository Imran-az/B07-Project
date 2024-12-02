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

import EcoTracker.CO2EmissionUpdater;

public class QuestionnairePageQ9 extends AppCompatActivity {

    private Button submitbtn, backbtn;
    EditText q1_ans, q2_ans, q3_ans, q4_ans;
    TextView q1_que, q2_que, q3_que, q4_que;
    Map<String, Object> data9 = new HashMap<>();
    FirebaseDatabase database;
    DatabaseReference dailySurveyReference;
    boolean e = false;
    boolean g = false;
    boolean w = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        // Start the CO2EmissionUpdater for all users
        CO2EmissionUpdater updater = new CO2EmissionUpdater();
        updater.startListeningForAllUsers();

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
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        dailySurveyReference = database.getReference("DailySurvey").child(userID).child(currentDate);

        electric.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                e = !e;
                if (e){
                    helperMethods.setVisibility2(q2_que, q2_ans);
                }
                else{
                    helperMethods.setVisibility4(q2_que, q2_ans);
                }
            }
        });


        database = FirebaseDatabase.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        String userID = currentUser.getUid();
        dailySurveyReference = database.getReference("DailySurvey");
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        dailySurveyReference = database.getReference("DailySurvey").child(userID).child(currentDate);

        submitbtn = findViewById(R.id.submit_button_Q9);
        //QuestionnairePageQ1 prev_data = new QuestionnairePageQ1();

        submitbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(QuestionnairePageQ9.this,DailySurveyHomePage.class);
                startActivity(intent);

                String answer1, answer2, answer3, answer4, answer5, answer6;
                answer1 = "Electricity bill not paid";
                answer2 = "Gas bill not paid";
                answer3 = "Water bill not paid";
                answer4 = "0";
                answer5 = "0";
                answer6 = "0";

                if(e && !TextUtils.isEmpty(String.valueOf(q2_ans.getText()))){
                    answer1 = "Electricity bill paid";
                    answer4 = String.valueOf(q2_ans.getText());
                }
                if(g && !TextUtils.isEmpty(String.valueOf(q3_ans.getText()))){
                    answer2 = "Gas bill paid";
                    answer5 = String.valueOf(q3_ans.getText());
                }
                if(w && !TextUtils.isEmpty(String.valueOf(q4_ans.getText()))){
                    answer3 = "Water bill paid";
                    answer6 = String.valueOf(q4_ans.getText());
                }

                if(!q4.equals("0")){
                    answer3 = q4;
                }

                //Log.d("HashMapData", "Current data: " + QuestionnairePageQ1.data.toString());
                QuestionnairePageQ1.data.put(q1, answer1 + ", " + answer2 + ", " + answer3);
                QuestionnairePageQ1.data.put("Electricity Paid", answer4);
                QuestionnairePageQ1.data.put(q2, answer2);
                QuestionnairePageQ1.data.put("Gas Paid", answer5);
                QuestionnairePageQ1.data.put(q3, answer3);
                QuestionnairePageQ1.data.put("Water Paid", answer6);
                //Log.d("HashMapData", "Current data: " + QuestionnairePageQ1.data.toString());

                dailySurveyReference.updateChildren(QuestionnairePageQ1.data).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Success message (optional)
                        Toast.makeText(QuestionnairePageQ9.this, "Data saved successfully!", Toast.LENGTH_SHORT).show();
                    } else {
                        // Error message
                        Toast.makeText(QuestionnairePageQ9.this, "Failed to save data: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        backbtn = findViewById(R.id.back_button_Q9);
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(QuestionnairePageQ9.this, QuestionnairePageQ8.class);
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