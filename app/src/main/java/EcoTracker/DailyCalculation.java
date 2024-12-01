package EcoTracker;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import net.robertx.planeteze_b07.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class DailyCalculation extends Calculations{
    public final double personalVehicleCalculation;
    public final double publicTransportCalculation;
    public final double flightCalculation;
    public final double meatCalculation;
    public final double clothesCalculation;
    public final double electronicsCalculation;
    public final double gasCalculator;
    public final double electricityCalculator;
    public final double waterCalculator;
    public final double dailyTotal;

    public DailyCalculation(HashMap<String, String> responses) {
        this.personalVehicleCalculation = personalVehicleCO2(responses);
        this.publicTransportCalculation = publicTransportCO2(responses);
        this.flightCalculation = flightCO2(responses);
        this.meatCalculation = meatCO2(responses);
        this.clothesCalculation = clothesCO2(responses);
        this.electronicsCalculation = electronicsCO2(responses);
        this.gasCalculator = gasBillCO2(responses);
        this.electricityCalculator = elecBillCO2(responses);
        this.waterCalculator = waterBillCO2(responses);
        this.dailyTotal = personalVehicleCalculation + publicTransportCalculation
                        + flightCalculation + meatCalculation
                        + clothesCalculation + electronicsCalculation + gasCalculator
                        + electricityCalculator + waterCalculator;
    }

    public HashMap<String, Double> toHashMap() {
        HashMap<String, Double> dataMap = new HashMap<>();

        // Add the calculations to the HashMap with appropriate keys
        dataMap.put("personalVehicle", personalVehicleCalculation);
        dataMap.put("publicTransport", publicTransportCalculation);
        dataMap.put("flight", flightCalculation);
        dataMap.put("meat", meatCalculation);
        dataMap.put("clothes", clothesCalculation);
        dataMap.put("electronics", electronicsCalculation);
        dataMap.put("gas", gasCalculator);
        dataMap.put("electricity", electricityCalculator);
        dataMap.put("water", waterCalculator);
        dataMap.put("dailyTotal", dailyTotal);

        return dataMap;
    }
}
