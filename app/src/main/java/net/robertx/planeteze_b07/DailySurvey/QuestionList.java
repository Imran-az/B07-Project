package net.robertx.planeteze_b07.DailySurvey;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import net.robertx.planeteze_b07.R;

import java.text.MessageFormat;
import java.util.ArrayList;

public class QuestionList extends AppCompatActivity {
    RecyclerView recyclerView;
    static ArrayList<MainModel> list;
    DatabaseReference dailySurveyDbRef;
    MainAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.question_answer_recyclerview);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        MaterialToolbar toolbar = findViewById(R.id.toolbar_question_list);
        toolbar.setNavigationOnClickListener(v -> finish());

        TextView PickedDate = findViewById(R.id.ChosenDate);
        PickedDate.setText(MessageFormat.format("Answers for {0}/{1}/{2}",CalendarPage.ChosenDay, CalendarPage.ChosenMonth, CalendarPage.ChosenYear));


        recyclerView = findViewById(R.id.recyclerView_widget);
        String date = CalendarPage.SelectedDate;
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        String userID = currentUser.getUid();
        dailySurveyDbRef = FirebaseDatabase.getInstance().getReference("DailySurvey").child(userID).child(date);
        DatabaseReference dailySurveyCO2 = FirebaseDatabase.getInstance().getReference("DailySurveyCO2").child(userID).child(date);
        list = new ArrayList<>();


        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MainAdapter(this, list);
        recyclerView.setAdapter(adapter);
        recyclerView.setNestedScrollingEnabled(false);
        Log.d("RecyclerView", "Adapter attached with initial list size: " + adapter.getItemCount());
        //Log.d("data", "value" + databaseReference);

        dailySurveyDbRef.addValueEventListener(new ValueEventListener() {
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

        dailySurveyCO2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String question = dataSnapshot.getKey();
                    Object value = dataSnapshot.getValue();

                    if (value != null) {
                        if (question != null) {
                            question = question.replace("CO2", "");
                            for (int i = 0; i < list.size(); i++) {
                                if (list.get(i).getQuestion().equals(question)) {
                                    list.get(i).setCO2_answer(question);
                                }
                            }
                        }
                    }
                }
                adapter.notifyDataSetChanged();
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}