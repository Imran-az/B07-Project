package net.robertx.planeteze_b07.dataRetrievers;

import android.content.Context;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;

/**
 * Utility class for retrieving emissions data from a JSON file.
 *
 * This class reads global emissions data from a JSON file stored in the app's assets
 * and provides methods to retrieve per-capita emissions data for specific countries.
 * The class must be initialized at app startup to load the data.
 */
public class EmissionsDataRetriever {

    /**
     * The root {@link JsonNode} representing the emissions data loaded from the JSON file.
     * This data contains per-capita emissions information for various countries and
     * is used to retrieve specific emission values.
     */
    private static JsonNode emissionsData;

    /**
     * Initializes the emissions data from a JSON file.
     *
     * This method reads the "Global_Averages.json" file from the app's assets folder
     * and loads it into a {@link JsonNode} for future use. It must be called before
     * using any methods in this class that depend on the emissions data.
     *
     * @param context the {@link Context} used to access the app's assets folder.
     * @throws IOException if an error occurs while reading the JSON file.
     */
    public static void initialize(Context context) throws IOException {
        if (emissionsData == null) {
            ObjectMapper mapper = new ObjectMapper();
            InputStream inputStream = context.getAssets().open("Global_Averages.json");
            emissionsData = mapper.readTree(inputStream);
        }
    }

    /**
     * Constructs an instance of {@code EmissionsDataRetriever}.
     *
     * This constructor verifies that the emissions data has been initialized.
     * If the data is not initialized, it throws an {@link IllegalStateException}.
     *
     * @throws IllegalStateException if the emissions data has not been initialized using {@link #initialize(Context)}.
     */
    public EmissionsDataRetriever() {
        if (emissionsData == null) {
            throw new IllegalStateException("EmissionsDataRetriever is not initialized. Call initialize(Context) before using this class.");
        }
    }

    /**
     * Retrieves the per-capita emissions value for a specified country.
     *
     * This method fetches the emissions data for the given country from the loaded JSON data.
     * If the data is not initialized or the country is invalid, an appropriate exception
     * is thrown or a default value is returned.
     *
     * @param country the name of the country whose emissions data is to be retrieved.
     * @return the per-capita emissions value in the form of a {@code double}, or 0.0 if an error occurs.
     * @throws IllegalStateException if the emissions data has not been initialized using {@link #initialize(Context)}.
     * @throws IllegalArgumentException if the specified country name is invalid or if a non-numeric value is encountered.
     */
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
