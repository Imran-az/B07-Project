package net.robertx.planeteze_b07.EcoTracker;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class CO2EmissionUpdater {
    public static final String TAG = "CO2EmissionUpdater";

    /**
     * General function to fetch data from Firebase for a user and date,
     * then recalculate and upload CO2 emissions.
     *
     * @param userId The user's ID.
     * @param date   The date for which data needs to be fetched.
     */
    public static void fetchDataAndRecalculate(String userId, String date) {
        if (userId == null || userId.isEmpty() || date == null || date.isEmpty()) {
            //Log.e(TAG, "Invalid userId or date. Cannot fetch data.");
            return;
        }

        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("DailySurvey")
                .child(userId).child(date);

        databaseRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                HashMap<String, String> responses = new HashMap<>();
                for (DataSnapshot snapshot : task.getResult().getChildren()) {
                    String key = snapshot.getKey();
                    Object value = snapshot.getValue();
                    if (key != null && value != null) {
                        responses.put(key, value.toString());
                    }
                }
                recalculateAndUpload(userId, date, responses);
            } else {
                Log.e(TAG, "Failed to fetch data for recalculation: " + task.getException());
            }
        });
    }

    /**
     * Recalculate CO2 emissions and upload results for a specific user and date.
     */
    private static void recalculateAndUpload(String userId, String date, HashMap<String, String> responses) {
        try {
            // Perform CO2 calculations
            DailyCalculation calculation = new DailyCalculation(responses);
            HashMap<String, Double> co2Data = calculation.toHashMap();

            // Reference to Firebase node
            DatabaseReference co2EmissionsRef = FirebaseDatabase.getInstance()
                    .getReference("DailySurveyCO2").child(userId).child(date);

            co2EmissionsRef.setValue(co2Data)
                    .addOnSuccessListener(aVoid ->
                            Log.d(TAG, "CO2 emissions updated successfully for user: " + userId + ", date: " + date))
                    .addOnFailureListener(e ->
                            Log.e(TAG, "Failed to update CO2 emissions for user: " + userId + ", date: " + date, e));

        } catch (Exception e) {
            Log.e(TAG, "Error recalculating CO2 emissions for user: " + userId + ", date: " + date, e);
        }
    }
}

