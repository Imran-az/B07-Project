package net.robertx.planeteze_b07.DailySurvey;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class helperMethods {

    public static void setVisibility1(TextView question1, TextView question2, EditText question1_answer, EditText question2_answer){
        question1.setVisibility(View.VISIBLE);
        question1_answer.setVisibility(View.VISIBLE);

        question2.setVisibility(View.VISIBLE);
        question2_answer.setVisibility(View.VISIBLE);
    }
    public static <T> void nextPage(EditText question2_answer, EditText question3_answer, Context currentClass, Class<T> destinationClass){
        if (!TextUtils.isEmpty(question2_answer.getText().toString()) || !TextUtils.isEmpty(question3_answer.getText().toString())) {
            Intent intent = new Intent(currentClass, destinationClass);
            currentClass.startActivity(intent);
        }
        else {
            Toast.makeText(currentClass, "Please fill out the required fields", Toast.LENGTH_SHORT).show();
        }
    }

    public static <T> void nextPageq3(EditText question2_answer, Context currentClass, Class<T> destinationClass){
        if (!TextUtils.isEmpty(question2_answer.getText().toString())) {
            Intent intent = new Intent(currentClass, destinationClass);
            currentClass.startActivity(intent);
        }
        else {
            Toast.makeText(currentClass, "Please fill out the required fields", Toast.LENGTH_SHORT).show();
        }
    }
}
