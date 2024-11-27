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

public class QuestionnairePageQ9 extends AppCompatActivity {

    Button electric, gas, water, none_btn, previous_btn, submit_btn;
    EditText q1_ans, q2_ans, q3_ans, q4_ans;
    TextView q1_que, q2_que, q3_que, q4_que;
    Map<String, Object> q9_data = new HashMap<>();
    FirebaseDatabase database;
    DatabaseReference databaseReference;
    boolean e = false;
    boolean g = false;
    boolean w = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_questionnaire_page_q9);

        electric = findViewById(R.id.electricity);
        gas = findViewById(R.id.gas);
        water = findViewById(R.id.water);
        none_btn = findViewById(R.id.None);
        submit_btn = findViewById(R.id.submit_button);

        q1_que = findViewById(R.id.question1_text_view);
        q2_que = findViewById(R.id.question2_text_view);
        q2_ans = findViewById(R.id.answer2_input);
        q3_que = findViewById(R.id.question3_text_view);
        q3_ans = findViewById(R.id.answer3_input);
        q4_que = findViewById(R.id.question4_text_view);
        q4_ans = findViewById(R.id.answer4_input);

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

        gas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                g = !g;
                if (g){
                    helperMethods.setVisibility2(q3_que, q3_ans);
                }
                else{
                    helperMethods.setVisibility4(q3_que, q3_ans);
                }
            }
        });

        water.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                w = !w;
                if (w){
                    helperMethods.setVisibility2(q4_que, q4_ans);
                }
                else{
                    helperMethods.setVisibility4(q4_que, q4_ans);
                }
            }
        });

        String q1, q2, q3, q4;
        q1 = String.valueOf(q1_que.getText());
        q2 = String.valueOf(q2_que.getText());
        q3 = String.valueOf(q3_que.getText());
        q4 = String.valueOf(q4_que.getText());

        submit_btn = findViewById(R.id.submit_button);
        //QuestionnairePageQ1 prev_data = new QuestionnairePageQ1();

        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(QuestionnairePageQ9.this, QuestionnairePageQ9.class);
                startActivity(intent);

                String answer1, answer2, answer3, answer4, answer5, answer6;
                answer1 = "Electricity bill not paid";
                answer2 = "Gas bill not paid";
                answer3 = "Water bill not paid";
                answer4 = "";
                answer5 = "";
                answer6 = "";

                if(e && !TextUtils.isEmpty(String.valueOf(q2_ans.getText()))){
                    answer1 = "Electricity bill paid, Amount Paid: ";
                    answer4 = String.valueOf(q2_ans.getText());
                }
                if(g && !TextUtils.isEmpty(String.valueOf(q3_ans.getText()))){
                    answer2 = "Gas bill paid, Amount Paid: ";
                    answer5 = String.valueOf(q3_ans.getText());
                }
                if(w && !TextUtils.isEmpty(String.valueOf(q4_ans.getText()))){
                    answer3 = "Water bill paid, Amount Paid: ";
                    answer6 = String.valueOf(q4_ans.getText());
                }



                QuestionnairePageQ1.data.put(q1, answer1);
                QuestionnairePageQ1.data.put("Electricity Paid", answer4);
                QuestionnairePageQ1.data.put(q2, answer2);
                QuestionnairePageQ1.data.put("Gas Paid", answer5);
                QuestionnairePageQ1.data.put(q3, answer3);
                QuestionnairePageQ1.data.put("Water Bill", answer6);
            }
        });

        previous_btn = findViewById(R.id.previous_button);
        previous_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(QuestionnairePageQ9.this, QuestionnairePageQ8.class);
                startActivity(intent);
            }
        });
        database = FirebaseDatabase.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        String userID = currentUser.getUid();
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
//        databaseReference = database.getReference("Users").child("Daily Survey").child(currentDate).child("W35Qr6MzplfED39mMHhiYRLKMYO2");
//
//        Log.d("FirebaseData", "Saving data: " + QuestionnairePageQ1.data);
//
//        databaseReference.updateChildren(QuestionnairePageQ1.data).addOnCompleteListener(task -> {
//                    if (task.isSuccessful()) {
//                        // Success message (optional)
//                        Toast.makeText(QuestionnairePageQ9.this, "Data saved successfully!", Toast.LENGTH_SHORT).show();
//                    } else {
//                        // Error message
//                        Toast.makeText(QuestionnairePageQ9.this, "Failed to save data: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//                });


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}