package net.robertx.planeteze_b07.questionaire;

import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import net.robertx.planeteze_b07.R;

import java.util.ArrayList;

public class Questionnaire extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_questionnaire);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        TextView question = findViewById(R.id.question);

        Button backButton = findViewById(R.id.backButton);
        backButton.setEnabled(false);


        question.setText(getIntent().getStringExtra("question"));

        ArrayList<String> answerOptions = getIntent().getStringArrayListExtra("options");
        if (answerOptions == null) {
            answerOptions = new ArrayList<>();
            // TODO: handle this case
            answerOptions.add("Oops you didn't add shit");
        }

        for (String option: answerOptions) {
            // add a button to the linearLayout answerButtons
            ToggleButton button = new ToggleButton(this);
            button.setText(option);
            button.setTextOn(option);
            button.setTextOff(option);
            button.setBackgroundResource(R.drawable.toggle_button_background);


//            button.setOnClickListener(v -> {
//
//            });
            LinearLayout answerButtons = findViewById(R.id.answerButtons);
            answerButtons.addView(button);
        }




    }
}