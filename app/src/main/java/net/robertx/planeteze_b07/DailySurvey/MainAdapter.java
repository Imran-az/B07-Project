package net.robertx.planeteze_b07.DailySurvey;

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

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import net.robertx.planeteze_b07.R;
import java.util.List;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.MainViewHolder> {

     List<MainModel> mainModelList;
     Context context;
    DatabaseReference databaseReference;

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

        Log.d("MainAdapter", "Binding Question: " + model.getQuestion() + ", Answer: " + model.getAnswer());

        // Set the data to the TextViews
        holder.questionTextView.setText(model.getQuestion());
        holder.answerTextView.setText(model.getAnswer());

        holder.editbtn.setOnClickListener(v -> {
            showEditAnswerDialog(model, position);

        });
    }

    @Override
    public int getItemCount() {
        return mainModelList.size();
    }

    // ViewHolder class to hold references to views for performance
    public static class MainViewHolder extends RecyclerView.ViewHolder {
        TextView questionTextView, answerTextView;
        Button editbtn, deletebtn;

        public MainViewHolder(@NonNull View itemView) {
            super(itemView);
            // Find the views from the item layout
            questionTextView = itemView.findViewById(R.id.Question);
            answerTextView = itemView.findViewById(R.id.Answer);
            editbtn = itemView.findViewById(R.id.Edit);
            deletebtn = itemView.findViewById(R.id.Delete);


        }
    }
    private void updateAnswerInDatabase(String itemId, String newAnswer) {
        // Ensure that itemId is not null
        if (itemId == null || itemId.isEmpty()) {
            Log.d("MainAdapter", "Error: itemId is null or empty.");
            return;  // Exit if the ID is not valid
        }

        // Get a reference to the database
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("DailySurvey");

        // Update the answer for the item with the given ID
        databaseRef.child(itemId).child("answer").setValue(newAnswer)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("MainAdapter", "Answer updated successfully in database.");
                    } else {
                        Log.d("MainAdapter", "Failed to update answer in database: " + task.getException());
                    }
                });
    }


    private void showEditAnswerDialog(MainModel model, int position) {
        // Create an EditText to get the new answer from the user
        EditText editText = new EditText(context);
        editText.setText(model.getAnswer());  // Pre-fill with the current answer
        editText.setSelection(editText.getText().length()); // Move cursor to the end

        // Show the dialog
        new AlertDialog.Builder(context)
                .setTitle("Edit Answer")
                .setMessage("Enter the new answer:")
                .setView(editText)
                .setPositiveButton("OK", (dialog, which) -> {
                    // Get the new answer from the EditText
                    String newAnswer = editText.getText().toString().trim();
                    if (!newAnswer.isEmpty()) {
                        // Update the model's answer in the list
                        model.setAnswer(newAnswer);

                        // Notify the adapter that data has changed
                        notifyItemChanged(position);

                        // Update the answer in Firebase
                        updateAnswerInDatabase(model.getId(), newAnswer);  // Pass the model's ID to update the database
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }


    // Optionally, if you need to update the data in the adapter
    public void updateData(List<MainModel> newData) {
        this.mainModelList = newData;
        notifyDataSetChanged();
    }
}
