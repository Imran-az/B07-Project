package net.robertx.planeteze_b07;

import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

public class YearlyHousingCarbonFootprintCalculator {
    private String typeOfHome;
    private String numOccupants;
    private String sizeOfHome;
    private String homeHeatingType;
    private String electricityBill;
    private String waterHeatingType;
    private String useRenewableEnergy;
    private final HousingCO2DataRetriever housingCO2DataRetriever = new HousingCO2DataRetriever();

    public double calculateYearlyFootprint(HashMap<String, String> responses) {
        this.typeOfHome = responses.get("What type of home do you live in?");
        this.numOccupants = responses.get("How many people live in your household?");
        this.sizeOfHome = responses.get("What is the size of your home?");
        this.homeHeatingType = responses.get("What type of energy do you use to heat your home?");
        this.electricityBill = responses.get("What is your average monthly electricity bill?");
        this.waterHeatingType = responses.get("What type of energy do you use to heat water?");
        this.useRenewableEnergy = responses.get("Do you use any renewable energy sources for electricity or heating?");

        // Fetch CO2 values asynchronously
       double heatingCO2 = housingCO2DataRetriever.fetchCO2ValueAndHandle();
    }

    private double adjustForRenewableEnergy(String renewableEnergy) {
        if ("Yes, primarily (more than 50% of energy use)".equals(renewableEnergy)) {
            return -6000.0; // Significant reduction
        } else if ("Yes, partially (less than 50% of energy use)".equals(renewableEnergy)) {
            return -4000.0; // Partial reduction
        } else {
            return 0.0; // No adjustment
        }
    }
}

