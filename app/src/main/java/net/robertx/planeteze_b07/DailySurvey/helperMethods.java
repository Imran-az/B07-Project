package net.robertx.planeteze_b07.DailySurvey;

import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class helperMethods {

    public static void setVisibility1(TextView question1, TextView question2, EditText question1_answer, EditText question2_answer){
        question1.setVisibility(View.VISIBLE);
        question1_answer.setVisibility(View.VISIBLE);

        question2.setVisibility(View.VISIBLE);
        question2_answer.setVisibility(View.VISIBLE);
    }

    public static void setVisibility2(TextView question1, EditText question1_answer){
        question1.setVisibility(View.VISIBLE);
        question1_answer.setVisibility(View.VISIBLE);
    }

    public static void setVisibility3(TextView question1, TextView question2, EditText question1_answer, EditText question2_answer){
        question1.setVisibility(View.INVISIBLE);
        question1_answer.setVisibility(View.INVISIBLE);

        question2.setVisibility(View.INVISIBLE);
        question2_answer.setVisibility(View.INVISIBLE);
    }

    public static void setVisibility4(TextView question1, EditText question1_answer){
        question1.setVisibility(View.INVISIBLE);
        question1_answer.setVisibility(View.INVISIBLE);
    }
}