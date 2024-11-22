package net.robertx.planeteze_b07;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        System.out.println("Hello, World!");

       HousingCO2DataRetriever housingCO2DataRetriever = new HousingCO2DataRetriever();
         housingCO2DataRetriever.getSpecificCO2Value("Detached", "Under-1000-sqft", "Under-50-Dollars", "1-Occupant", "Wood")
                .thenAccept(co2Value -> {
                     // Handle the retrieved CO2 value
                    System.out.println("CO2 Value: " + co2Value);
                })
                .exceptionally(throwable -> {
                     // Handle the exception
                     return null;
                });

    }

}