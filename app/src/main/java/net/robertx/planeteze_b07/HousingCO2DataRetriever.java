package net.robertx.planeteze_b07;

import android.content.Context;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;

public class HousingCO2DataRetriever {
    private static JsonNode co2Data;

    // Static method to initialize the JSON file at app startup
    public static void initialize(Context context) throws IOException {
        if (co2Data == null) {
            ObjectMapper mapper = new ObjectMapper();
            InputStream inputStream = context.getAssets().open("housingCo2Data.json");
            co2Data = mapper.readTree(inputStream);
        }
    }

    public HousingCO2DataRetriever() {
        if (co2Data == null) {
            throw new IllegalStateException("HousingCO2DataRetriever is not initialized. Call initialize(Context) before using this class.");
        }
    }

    public double getSpecificCO2Value(String houseType, String sizeCategory, String billRange, String occupants, String energyType) {
        if (co2Data == null) {
            throw new IllegalStateException("HousingCO2DataRetriever is not initialized.");
        }

        try {
            JsonNode valueNode = co2Data.path("HousingCO2Data")
                    .path(houseType)
                    .path(sizeCategory)
                    .path(billRange)
                    .path(occupants)
                    .path(energyType);

            if (valueNode.isMissingNode() || !valueNode.isNumber()) {
                throw new IllegalArgumentException("Invalid path or non-numeric value encountered.");
            }

            return valueNode.asDouble();
        } catch (Exception e) {
            e.printStackTrace();
            return 0.0;
        }
    }
}
