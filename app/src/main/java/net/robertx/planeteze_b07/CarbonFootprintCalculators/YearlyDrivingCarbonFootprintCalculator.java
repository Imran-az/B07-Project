package net.robertx.planeteze_b07.CarbonFootprintCalculators;
import java.util.HashMap;

public class YearlyDrivingCarbonFootprintCalculator implements CalculateYearlyCarbonFootPrint {
    public String carType; // Gasoline, Diesel, Hybrid, Electric
    public double distanceDriven; // Distance driven in kilometers

    private double parseDistance(String distanceStr) {
        if (distanceStr == null || distanceStr.isEmpty()) {
            return 0;
        }

        if (distanceStr.equalsIgnoreCase("Up to 5,000 km (3,000 miles)")) {
            return 5000;
        } else if (distanceStr.equalsIgnoreCase("5,000–10,000 km (3,000–6,000 miles)")) {
            return 10000;
        } else if (distanceStr.equalsIgnoreCase("10,000–15,000 km (6,000–9,000 miles)")) {
            return 15000;
        } else if (distanceStr.equalsIgnoreCase("15,000–20,000 km (9,000–12,000 miles)")) {
            return 20000;
        } else if (distanceStr.equalsIgnoreCase("20,000–25,000 km (12,000–15,000 miles)")) {
            return 25000;
        } else if (distanceStr.equalsIgnoreCase("More than 25,000 km (15,000 miles)")) {
            return 35000;
        } else {
            return 0; // Default for invalid input
        }
    }

    @Override
    public double calculateYearlyFootprint(HashMap<String, String> responses) {

        if (responses.containsKey("Do you own or regularly use a car?") &&
                responses.get("Do you own or regularly use a car?").equalsIgnoreCase("No")) {
            return 0;
        }

        if (responses.containsKey("Do you own or regularly use a car?") &&
                responses.get("Do you own or regularly use a car?").equalsIgnoreCase("Yes")) {
            this.carType = responses.get("What type of car do you drive?");
            String distanceStr = responses.get("How many kilometers/miles do you drive per year?");

            // Convert distance string to numeric value (default to 0 if missing or invalid)
            this.distanceDriven = parseDistance(distanceStr);

            double emissionFactor = getEmissionFactor();

            // Calculate and return carbon footprint
            return emissionFactor * distanceDriven;
        }

        // Default return in case the key doesn't exist or has unexpected values
        return 0;
    }

    private double getEmissionFactor() {
        double emissionFactor = 0.1;

        // Get emission factor based on car type
        if (carType.equalsIgnoreCase("gasoline")) {
            emissionFactor = 0.24; // kg CO2 per km
        } else if (carType.equalsIgnoreCase("diesel")) {
            emissionFactor = 0.27; // kg CO2 per km
        } else if (carType.equalsIgnoreCase("hybrid")) {
            emissionFactor = 0.16; // kg CO2 per km
        } else if (carType.equalsIgnoreCase("electric")) {
            emissionFactor = 0.05; // kg CO2 per km
        }
        return emissionFactor;
    }
}
