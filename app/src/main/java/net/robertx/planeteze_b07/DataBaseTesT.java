package net.robertx.planeteze_b07;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.Map;
import java.util.Objects;

public class DataBaseTesT {
    private static final String TAG = "FirestormHelper";
    public int co2Value;
    private FirebaseFirestore db;

    // Constructor to initialize Firestorm
    public DataBaseTesT(FirebaseFirestore mockDb) {
        db = FirebaseFirestore.getInstance();
    }

    // Method to fetch a specific CO2 value and print directly to the log
    public void fetchAndPrintCO2Value() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();



}
