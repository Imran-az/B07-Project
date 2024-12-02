package net.robertx.planeteze_b07.DailySurvey;

import static net.robertx.planeteze_b07.DailySurvey.QuestionList.list;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import net.robertx.planeteze_b07.R;

import java.util.Calendar;
import java.util.List;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.MainViewHolder> {

     List<MainModel> mainModelList;
     Context context;
    DatabaseReference databaseReference;

    FirebaseDatabase database;

    // Constructor that accepts the list of MainModel
    public MainAdapter(Context context,List<MainModel> mainModelList) {
        this.mainModelList = mainModelList;
        this.context = context;
    }

    @NonNull
    @Override
    public MainViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the item layout (change R.layout.activity_item_adapter to your actual layout)
        View view = LayoutInflater.from(context).inflate(R.layout.activity_cardview, parent, false);
        return new MainViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MainViewHolder holder, int position) {
        MainModel model = mainModelList.get(position);

        // Set the data to the TextViews
        holder.questionTextView.setText(model.getQuestion());
        holder.answerTextView.setText(model.getAnswer());
        holder.deletebtn.setOnClickListener(v -> {
            ShowDeleteActivity(model, position);
        });


        holder.editbtn.setOnClickListener(v -> {
            showEditAnswerDialog(model, position);
        });
    }

    @Override
    public int getItemCount() {
        return mainModelList.size();
    }

    public static class MainViewHolder extends RecyclerView.ViewHolder {
        TextView questionTextView, answerTextView, CO2Emission_Answer, CO2_Emission;
        Button editbtn, deletebtn;

        public MainViewHolder(@NonNull View itemView) {
            super(itemView);

            questionTextView = itemView.findViewById(R.id.Question);
            answerTextView = itemView.findViewById(R.id.Answer);
            editbtn = itemView.findViewById(R.id.Edit);
            deletebtn = itemView.findViewById(R.id.Delete);
            //CO2Emission_Answer = itemView.findViewById(R.id.CO2Emission_Answer);
            //CO2_Emission = itemView.findViewById(R.id.CO2_Emission);


        }
    }
    private void updateAnswerInDatabase(String itemId, String newAnswer) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        String userID = currentUser.getUid();
        String date = CalendarPage.SelectedDate;
        Log.d("date", "current: " + date);

        if (itemId == null || itemId.isEmpty()) {
            return;
        }
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("DailySurvey")
                .child(userID).child(date);


        databaseRef.child(itemId).setValue(newAnswer)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("MainAdapter", "Answer updated successfully in database.");
                    } else {
                        Log.d("MainAdapter", "Failed to update answer in database: " + task.getException());
                    }
                });
    }


    private void showEditAnswerDialog(MainModel model, int position) {
        EditText editText = new EditText(context);
        editText.setText(model.getAnswer());
        editText.setSelection(editText.getText().length());

        // Show the dialog
        new AlertDialog.Builder(context)
                .setTitle("Edit Answer")
                .setMessage("Enter the new answer:")
                .setView(editText)
                .setPositiveButton("OK", (dialog, which) -> {

                    String newAnswer = editText.getText().toString().trim();
                    if (!newAnswer.isEmpty()) {

                        model.setAnswer(newAnswer);

                        notifyItemChanged(position);

                        updateAnswerInDatabase(model.getQuestion(), newAnswer);
                        Log.d("tester", "id: " + model.getQuestion());
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
    private void ShowDeleteActivity(MainModel model, int position) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        String userID = currentUser.getUid();
        String date = CalendarPage.SelectedDate;

        // Show the dialog
        new AlertDialog.Builder(context)
                .setTitle("Are you Sure you Want to Delete Activity?")
                .setMessage("This action cannot be undone.")
                .setPositiveButton("Yes", (dialog, which) -> {
                    // Step 1: Remove from Firebase
                    DatabaseReference databaseRef = FirebaseDatabase.getInstance()
                            .getReference("DailySurvey")
                            .child(userID).child(date).child(model.getQuestion()); // Replace "DailySurvey" with the correct root

                    databaseRef.removeValue()
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    // Step 2: Remove from local list
                                    list.remove(position);

                                    // Step 3: Notify adapter about removal
                                    notifyItemRemoved(position);

                                    // Log success
                                    Log.d("DeleteActivity", "Activity deleted successfully.");
                                } else {
                                    // Handle failure
                                    Log.e("DeleteActivity", "Failed to delete activity: " + task.getException());
                                }
                            });
                })
                .setNegativeButton("No", null)
                .show();
    }

}
