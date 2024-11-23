package net.robertx.planeteze_b07;


import java.util.Map;

public class YearlyHousingCarbonFootprintCalculator {
    private final HousingCO2DataRetriever dataRetriever;

    // Zero-argument constructor
    public YearlyHousingCarbonFootprintCalculator() {
        this.dataRetriever = new HousingCO2DataRetriever();
    }

    public double calculateYearlyFootprint(Map<String, String> responses) {
        String typeOfHome = responses.get("What type of home do you live in?");
        String sizeOfHome = responses.get("What is the size of your home?");
        String electricityBill = responses.get("What is your average monthly electricity bill?");
        String numOccupants = responses.get("How many people live in your household?");
        String homeHeatingType = responses.get("What type of energy do you use to heat your home?");
        String waterHeatingType = responses.get("What type of energy do you use to heat water?");
        String useRenewableEnergy = responses.get("Do you use any renewable energy sources for electricity or heating?");

        double heatingCO2 = dataRetriever.getSpecificCO2Value(typeOfHome, sizeOfHome, electricityBill, numOccupants, homeHeatingType);
        double waterHeatingCO2 = dataRetriever.getSpecificCO2Value(typeOfHome, sizeOfHome, electricityBill, numOccupants, waterHeatingType);

        double additionalCO2 = !homeHeatingType.equals(waterHeatingType) ? 233.0 : 0.0;
        double renewableAdjustment = adjustForRenewableEnergy(useRenewableEnergy);

        return heatingCO2 + waterHeatingCO2 - additionalCO2 + renewableAdjustment;
    }

    private double adjustForRenewableEnergy(String renewableEnergy) {
        switch (renewableEnergy) {
            case "Yes, primarily":
                return -6000.0;
            case "Yes, partially":
                return -4000.0;
            default:
                return 0.0;
        }
    }
}
