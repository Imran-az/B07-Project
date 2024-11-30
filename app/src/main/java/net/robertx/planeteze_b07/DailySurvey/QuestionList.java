package net.robertx.planeteze_b07.DailySurvey;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import net.robertx.planeteze_b07.R;

import java.util.ArrayList;

public class QuestionList extends AppCompatActivity {
    RecyclerView recyclerView;
    ArrayList<MainModel> list;
    DatabaseReference databaseReference;
    MainAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.question_list);

        recyclerView = findViewById(R.id.recyclerView_widget);
        String date = CalendarPage.SelectedDate;
        databaseReference = FirebaseDatabase.getInstance().getReference("DailySurvey").child("W35Qr6MzplfED39mMHhiYRLKMYO2").child(date);
        list = new ArrayList<>();




        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MainAdapter(this, list);
        recyclerView.setAdapter(adapter);
        Log.d("RecyclerView", "Adapter attached with initial list size: " + adapter.getItemCount());

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String question = dataSnapshot.getKey();
                    Object value = dataSnapshot.getValue();

                    if (value != null) {
                        String answer;
                        if (value instanceof Long) {
                            answer = String.valueOf(value); // Convert Long to String
                        } else if (value instanceof String) {
                            answer = (String) value; // Use the String directly
                        } else {
                            Log.d("FirebaseData", "Unexpected value type: " + value.getClass().getSimpleName());
                            continue; // Skip unexpected types
                        }

                        if (question != null) {
                            list.add(new MainModel(question, answer));
                        }
                    }
                }
                adapter.notifyDataSetChanged();
                Log.d("FirebaseData", "Items retrieved: " + list.size());
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseError", "Data retrieval cancelled: " + error.getMessage());
            }
        });



        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}