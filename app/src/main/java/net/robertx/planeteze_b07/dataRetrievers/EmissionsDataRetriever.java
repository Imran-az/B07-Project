package net.robertx.planeteze_b07.dataRetrievers;

import android.content.Context;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;

public class EmissionsDataRetriever {
    private static JsonNode emissionsData;

    // Static method to initialize the JSON file at app startup
    public static void initialize(Context context) throws IOException {
        if (emissionsData == null) {
            ObjectMapper mapper = new ObjectMapper();
            InputStream inputStream = context.getAssets().open("Global_Averages.json");
            emissionsData = mapper.readTree(inputStream);
        }
    }

    public EmissionsDataRetriever() {
        if (emissionsData == null) {
            throw new IllegalStateException("EmissionsDataRetriever is not initialized. Call initialize(Context) before using this class.");
        }
    }

    public double getEmissionValue(String country) {
        if (emissionsData == null) {
            throw new IllegalStateException("EmissionsDataRetriever is not initialized.");
        }

        try {
            JsonNode valueNode = emissionsData.path(country).path("Emissions (per capita)");

            if (valueNode.isMissingNode() || !valueNode.isNumber()) {
                throw new IllegalArgumentException("Invalid country name or non-numeric value encountered.");
            }

            return valueNode.asDouble();
        } catch (Exception e) {
            e.printStackTrace();
            return 0.0;
        }
    }
}
