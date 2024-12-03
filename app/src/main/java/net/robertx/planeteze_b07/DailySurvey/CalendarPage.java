package net.robertx.planeteze_b07.DailySurvey;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import net.robertx.planeteze_b07.R;

import java.text.MessageFormat;
import java.util.Calendar;
import java.util.List;

public class CalendarPage extends AppCompatActivity {

    FirebaseDatabase database;
    DatabaseReference databaseReference;
    public static String SelectedDate;

    public static String datedisplay;

    public static String ChosenYear, ChosenDay, ChosenMonth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_calendar_page);

        Button button = findViewById(R.id.datePicker);
        TextView displayDate = findViewById(R.id.displayDate);
        SelectedDate = "";


        displayDate.setText("Calendar");

//        recyclerView = findViewById(R.id.recyclerView);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//
//
//        FirebaseRecyclerOptions<MainModel> options = new FirebaseRecyclerOptions.Builder<MainModel>()
//                .setQuery(FirebaseDatabase.getInstance().getReference().child("DailySurvey").child("W35Qr6MzplfED39mMHhiYRLKMYO2").child("2024-11-27"), MainModel.class).build();
//
//        mainAdapter = new MainAdapter(options);
//        recyclerView.setAdapter(mainAdapter);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                FirebaseAuth auth = FirebaseAuth.getInstance();
                FirebaseUser currentUser = auth.getCurrentUser();
                String userID = currentUser.getUid();


                DatePickerDialog dialog = new DatePickerDialog(CalendarPage.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        displayDate.setText(MessageFormat.format("{0}/{1}/{2}", String.valueOf(dayOfMonth), String.valueOf(month + 1), String.valueOf(year)));
                        SelectedDate =  String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth);
                        datedisplay = String.format("%02d-%02d-%04d", dayOfMonth, month + 1, year);
                        ChosenDay = String.valueOf(dayOfMonth);
                        ChosenYear = String.valueOf(year);
                        ChosenMonth = String.valueOf(month + 1);

                        databaseReference = database.getInstance().getReference("DailySurvey").child(userID).child(SelectedDate);

                        databaseReference.get().addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                DataSnapshot snapshot = task.getResult();
                                if (snapshot.exists()) {
                                    Intent intent = new Intent(CalendarPage.this, QuestionList.class);
                                    startActivity(intent);
                                } else {
                                    QuestionnairePageQ1.ChangedDate = SelectedDate;
                                    System.out.print(QuestionnairePageQ1.ChangedDate);
                                    Intent intent = new Intent(CalendarPage.this, DailySurveyHomePage.class);
                                    startActivity(intent);
                                }
                            } else {
                                System.out.println("Error checking node: " + task.getException().getMessage());
                            }
                        });
                    }
                }, year, month, day);
                dialog.show();
            }
        });



        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}