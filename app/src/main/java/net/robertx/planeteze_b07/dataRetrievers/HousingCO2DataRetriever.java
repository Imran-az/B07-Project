package net.robertx.planeteze_b07.dataRetrievers;

import android.content.Context;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;

/**
 * Utility class for retrieving CO2 emissions data related to housing.
 *
 * This class reads housing-related CO2 emissions data from a JSON file stored in the app's assets
 * and provides methods to retrieve specific emissions values based on housing characteristics.
 * The class must be initialized at app startup to load the data.
 */
public class HousingCO2DataRetriever {

    /**
     * The root {@link JsonNode} representing the CO2 emissions data loaded from the JSON file.
     * This data contains housing-related CO2 emissions information and is used to retrieve
     * specific emission values based on housing characteristics.
     */
    private static JsonNode co2Data;

    /**
     * Initializes the housing CO2 emissions data from a JSON file.
     *
     * This method reads the "housingCo2Data.json" file from the app's assets folder
     * and loads it into a {@link JsonNode} for future use. It must be called before
     * using any methods in this class that depend on the CO2 data.
     *
     * @param context the {@link Context} used to access the app's assets folder.
     * @throws IOException if an error occurs while reading the JSON file.
     */
    public static void initialize(Context context) throws IOException {
        if (co2Data == null) {
            ObjectMapper mapper = new ObjectMapper();
            InputStream inputStream = context.getAssets().open("housingCo2Data.json");
            co2Data = mapper.readTree(inputStream);
        }
    }

    /**
     * Constructs an instance of {@code HousingCO2DataRetriever}.
     *
     * This constructor verifies that the CO2 emissions data has been initialized.
     * If the data is not initialized, it throws an {@link IllegalStateException}.
     *
     * @throws IllegalStateException if the CO2 emissions data has not been initialized using {@link #initialize(Context)}.
     */
    public HousingCO2DataRetriever() {
        if (co2Data == null) {
            throw new IllegalStateException("HousingCO2DataRetriever is not initialized. Call initialize(Context) before using this class.");
        }
    }

    /**
     * Retrieves the CO2 emissions value for specific housing characteristics.
     *
     * This method fetches the emissions data based on the provided housing attributes,
     * including house type, size category, bill range, number of occupants, and energy type.
     * The data is retrieved from the loaded JSON file.
     *
     * @param houseType the type of house (e.g., "Apartment", "Detached").
     * @param sizeCategory the size category of the house (e.g., "Small", "Large").
     * @param billRange the range of utility bills (e.g., "Low", "High").
     * @param occupants the number of occupants in the house.
     * @param energyType the type of energy used in the house (e.g., "Electricity", "Gas").
     * @return the CO2 emissions value in the form of a {@code double}, or 0.0 if an error occurs.
     * @throws IllegalStateException if the CO2 emissions data has not been initialized using {@link #initialize(Context)}.
     * @throws IllegalArgumentException if the specified attributes result in an invalid path or non-numeric value.
     */
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
