package net.robertx.planeteze_b07;

import java.util.HashMap;

public class YearlyHousingCarbonFootprintCalculator {
    String typeOfHome;
    String numOccupants;
    String sizeOfHome;
    String heatingType;
    String avgElectricityBill;
    String waterHeatingType;

    public YearlyHousingCarbonFootprintCalculator(HashMap<String, String> responses) {
        this.typeOfHome = responses.get("What type of home do you live in?");
        this.numOccupants = responses.get("How many people live in your home?");
        this.sizeOfHome = responses.get("What is the size of your home?");
        this.heatingType = responses.get("What type of heating do you use?");
        this.avgElectricityBill = responses.get("What is your average monthly electricity bill?");
        this.waterHeatingType = responses.get("What type of water heating do you use?");
    }
}
