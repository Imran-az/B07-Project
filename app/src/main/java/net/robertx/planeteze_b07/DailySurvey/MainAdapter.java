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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import net.robertx.planeteze_b07.EcoTracker.CO2EmissionUpdater;
import net.robertx.planeteze_b07.R;

import java.util.List;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.MainViewHolder> {

    private final List<MainModel> mainModelList;
    private final Context context;

    public MainAdapter(Context context, List<MainModel> mainModelList) {
        this.mainModelList = mainModelList;
        this.context = context;
    }

    @NonNull
    @Override
    public MainViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.activity_cardview, parent, false);
        return new MainViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MainViewHolder holder, int position) {
        MainModel model = mainModelList.get(position);
        holder.questionTextView.setText(model.getQuestion());
        holder.answerTextView.setText(model.getAnswer());
        holder.editbtn.setOnClickListener(v -> showEditAnswerDialog(model, position));
    }

    @Override
    public int getItemCount() {
        return mainModelList.size();
    }

    public static class MainViewHolder extends RecyclerView.ViewHolder {
        final TextView questionTextView;
        final TextView answerTextView;
        final Button editbtn;

        public MainViewHolder(@NonNull View itemView) {
            super(itemView);
            questionTextView = itemView.findViewById(R.id.Question);
            answerTextView = itemView.findViewById(R.id.Answer);
            editbtn = itemView.findViewById(R.id.Edit);
        }
    }

    private void updateAnswerInDatabase(String itemId, String newAnswer) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            Log.d("MainAdapter", "User not authenticated.");
            return;
        }

        String userID = currentUser.getUid();
        String date = CalendarPage.SelectedDate;
        if (itemId == null || itemId.isEmpty() || date == null || date.isEmpty()) {
            return;
        }

        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("DailySurvey")
                .child(userID).child(date);

        databaseRef.child(itemId).setValue(newAnswer)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("MainAdapter", "Answer updated successfully in database.");
                        CO2EmissionUpdater.fetchDataAndRecalculate(userID, date);
                    } else {
                        Log.d("MainAdapter", "Failed to update answer in database: " + task.getException());
                    }
                });
    }

    private void showEditAnswerDialog(MainModel model, int position) {
        EditText editText = new EditText(context);
        editText.setText(model.getAnswer());

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
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
