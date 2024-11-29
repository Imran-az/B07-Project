package net.robertx.planeteze_b07.CarbonFootprintCalculators;

import java.util.HashMap;

public class YearlyFoodCarbonFootprintCalculator implements CalculateYearlyCarbonFootPrint {
    private String dietType; // e.g., "Vegetarian", "Vegan", "Pescatarian", "Meat-based"
    private String beefConsumption; // "Daily", "Frequently (3-5 times/week)", "Occasionally (1-2 times/week)", "Never"
    private String porkConsumption; // "Daily", "Frequently (3-5 times/week)", "Occasionally (1-2 times/week)", "Never"
    private String chickenConsumption; // "Daily", "Frequently (3-5 times/week)", "Occasionally (1-2 times/week)", "Never"
    private String fishConsumption; //"Daily", "Frequently (3-5 times/week)", "Occasionally (1-2 times/week)", "Never"
    private String foodWaste; // "Daily", "Frequently (3-5 times/week)", "Occasionally (1-2 times/week)", "Never"
    double dietFootprint;

    @Override
    public double calculateYearlyFootprint(HashMap<String, String> responses) {
        // Reset dietFootprint at the start of each calculation
        dietFootprint = 0;

        // Extract values from the HashMap
        this.dietType = responses.get("What best describes your diet?");

        if (dietType.equalsIgnoreCase("Meat-based (eat all types of animal products)")) {
            this.beefConsumption = responses.get("How often do you eat the following animal-based products? Beef:");
            this.porkConsumption = responses.get("How often do you eat the following animal-based products? Pork:");
            this.chickenConsumption = responses.get("How often do you eat the following animal-based products? Chicken:");
            this.fishConsumption = responses.get("How often do you eat the following animal-based products? Fish/Seafood:");
        }

        this.foodWaste = responses.get("How often do you waste food or throw away uneaten leftovers?");

        // If diet is meat-based, add footprints for meat consumption
        if (dietType.equalsIgnoreCase("Meat-based (eat all types of animal products)")) {
            dietFootprint += calculateMeatConsumptionFootprint(beefConsumption, porkConsumption, chickenConsumption, fishConsumption);
        } else {
            dietFootprint = calculateDietFootprint(dietType);
        }

        // Add footprint for food waste
        dietFootprint += calculateFoodWasteFootprint(this.foodWaste);

        System.out.println(" in Function  diet" + dietFootprint);
        return dietFootprint;
    }


    private double calculateDietFootprint(String dietType) {
        if (dietType.equalsIgnoreCase("Vegetarian")) {
            return 1000;
        } else if (dietType.equalsIgnoreCase("Vegan")) {
            return 500;
        } else if (dietType.equalsIgnoreCase("Pescatarian (fish/seafood)")) {
            return 1500;
        }
        throw new IllegalArgumentException("Invalid diet type: " + dietType);
    }

    private double calculateMeatConsumptionFootprint(String beef, String pork, String chicken, String fish) {
        double totalFootprint = 0;

        // Add footprint for beef consumption
        if (beef != null) {
            if (beef.equalsIgnoreCase("Daily")) {
                totalFootprint += 2500;
            } else if (beef.equalsIgnoreCase("Frequently (3-5 times/week)")) {
                totalFootprint += 1900;
            } else if (beef.equalsIgnoreCase("Occasionally (1-2 times/week)")) {
                totalFootprint += 1300;
            }
        }

        // Add footprint for pork consumption
        if (pork != null) {
            if (pork.equalsIgnoreCase("Daily")) {
                totalFootprint += 1450;
            } else if (pork.equalsIgnoreCase("Frequently (3-5 times/week)")) {
                totalFootprint += 860;
            } else if (pork.equalsIgnoreCase("Occasionally (1-2 times/week)")) {
                totalFootprint += 450;
            }
        }

        // Add footprint for chicken consumption
        if (chicken != null) {
            if (chicken.equalsIgnoreCase("Daily")) {
                totalFootprint += 950;
            } else if (chicken.equalsIgnoreCase("Frequently (3-5 times/week)")) {
                totalFootprint += 600;
            } else if (chicken.equalsIgnoreCase("Occasionally (1-2 times/week)")) {
                totalFootprint += 200;
            }
        }

        // Add footprint for fish consumption
        if (fish != null) {
            if (fish.equalsIgnoreCase("Daily")) {
                totalFootprint += 800;
            } else if (fish.equalsIgnoreCase("Frequently (3-5 times/week)")) {
                totalFootprint += 500;
            } else if (fish.equalsIgnoreCase("Occasionally (1-2 times/week)")) {
                totalFootprint += 150;
            }
        }
        return totalFootprint;
    }

    private double calculateFoodWasteFootprint(String waste) {
        if (waste.equalsIgnoreCase("Never")) {
            return 0;
        } else if (waste.equalsIgnoreCase("Rarely")) {
            return 23.4;
        } else if (waste.equalsIgnoreCase("Occasionally")) {
            return 70.2;
        } else if (waste.equalsIgnoreCase("Frequently")) {
            return 140.4;
        }
        throw new IllegalArgumentException("Invalid food waste frequency: " + waste);
    }
}