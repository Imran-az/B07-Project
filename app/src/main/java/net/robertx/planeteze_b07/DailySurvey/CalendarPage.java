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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import net.robertx.planeteze_b07.R;

import org.w3c.dom.Text;

import java.text.MessageFormat;
import java.util.Calendar;
import java.util.List;

public class CalendarPage extends AppCompatActivity {

    FirebaseDatabase database;
    DatabaseReference databaseReference;
    RecyclerView recyclerView;
    MainAdapter mainAdapter;

    List<MainModel> activityList;
    static String SelectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_calendar_page);

        Button button = findViewById(R.id.datePicker);
        TextView displayDate = findViewById(R.id.displayDate);
        SelectedDate = "";

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


                DatePickerDialog dialog = new DatePickerDialog(CalendarPage.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        displayDate.setText(MessageFormat.format("{0}/{1}/{2}", String.valueOf(dayOfMonth), String.valueOf(month + 1), String.valueOf(year)));
                        SelectedDate =  String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth);
                        Intent intent = new Intent(CalendarPage.this, QuestionList.class);
                        startActivity(intent);
                        finish();
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