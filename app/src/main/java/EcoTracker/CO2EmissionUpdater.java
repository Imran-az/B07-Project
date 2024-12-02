package EcoTracker;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class CO2EmissionUpdater {
    private final DatabaseReference dailySurveyRootRef;

    public String TAG = "CO2EmissionUpdater";

    public CO2EmissionUpdater() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        this.dailySurveyRootRef = database.getReference("DailySurvey"); // Root node for all users
    }

    /**
     * Start listening for changes across all users and dates.
     */
    public void startListeningForAllUsers() {
        dailySurveyRootRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Log.d(TAG, "User data added for userId: " + snapshot.getKey());
                startListeningForUser(snapshot.getKey());
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                startListeningForUser(snapshot.getKey());
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                String userId = snapshot.getKey();
                if (userId != null) {
                    Log.d(TAG, "User data removed for userId: " + userId);
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "Error listening to user-level changes: " + error.getMessage());
            }
        });
    }

    /**
     * Start listening for changes for a specific user's dates.
     */
    private void startListeningForUser(String userId) {
        if (userId == null) return;

        DatabaseReference userSurveyRef = dailySurveyRootRef.child(userId);
        userSurveyRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Log.d(TAG, "Date data added for userId: " + userId + ", date: " + snapshot.getKey());
                handleDateChange(userId, snapshot);
            }
            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Log.d(TAG, "Date data changed for userId: " + userId + ", date: " + snapshot.getKey());
                handleDateChange(userId, snapshot);
            }
            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
            }
            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "Error listening to date-level changes for userId: " + userId + ": " + error.getMessage());
            }
        });
    }

    /**
     * Handle changes for a specific user's date.
     */
    private void handleDateChange(String userId, @NonNull DataSnapshot dateSnapshot) {
        String date = dateSnapshot.getKey();
        if (date == null){
            return;
        }

        // Convert all responses to strings safely
        HashMap<String, String> responses = new HashMap<>();
        for (DataSnapshot responseSnapshot : dateSnapshot.getChildren()) {
            Object value = responseSnapshot.getValue();
            responses.put(responseSnapshot.getKey(), value.toString());
        }
        // Recalculate and update CO2 emissions for the user and date
        recalculateAndUpload(userId, date, responses);
    }

    /**
     * Recalculate CO2 emissions and upload results for a specific user and date.
     */
    private void recalculateAndUpload(String userId, String date, HashMap<String, String> responses) {
        try {
            // Perform CO2 calculations
            DailyCalculation calculation = new DailyCalculation(responses);
            HashMap<String, Double> co2Data = calculation.toHashMap();

            // Reference to Firebase node
            DatabaseReference co2EmissionsRef = FirebaseDatabase.getInstance()
                    .getReference("DailySurveyCO2").child(userId).child(date);

            // Upload the calculated data to Firebase
            co2EmissionsRef.setValue(co2Data)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // Write was successful
                            Log.d("CO2EmissionUpdater", "CO2 emissions updated successfully for user: " + userId + ", date: " + date);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Write failed
                            Log.e("CO2EmissionUpdater", "Failed to update CO2 emissions for user: " + userId + ", date: " + date, e);
                        }
                    });

        } catch (Exception e) {
            // Handle any unexpected exceptions
            Log.e("CO2EmissionUpdater", "Error recalculating CO2 emissions for user: " + userId + ", date: " + date, e);
        }
    }


}

