package net.robertx.planeteze_b07;
import androidx.annotation.NonNull;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.concurrent.CompletableFuture;

public class HousingCO2DataRetriever {
    private static final String TAG = "HousingCO2Retriever";
    private final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

    public CompletableFuture<Object> getSpecificCO2Value(String houseType, String sizeCategory, String billRange, String occupants, String energyType) {
        // Create a CompletableFuture to hold the result
        CompletableFuture<Object> resultFuture = new CompletableFuture<>();

        DatabaseReference ref = mDatabase.child("HousingCO2Data")
                .child(houseType)
                .child(sizeCategory)
                .child(billRange)
                .child(occupants)
                .child(energyType);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Double co2Value = snapshot.getValue(Double.class);
                    resultFuture.complete(co2Value); // Set the retrieved value in the CompletableFuture
                } else {
                    resultFuture.completeExceptionally(new Exception("No data found at the specified path."));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                resultFuture.completeExceptionally(error.toException());
            }
        });



        return resultFuture;
    }


}

