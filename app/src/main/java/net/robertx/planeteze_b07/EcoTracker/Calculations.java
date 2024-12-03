package net.robertx.planeteze_b07.EcoTracker;

import java.util.HashMap;
import java.util.Map;

public class Calculations {

    public static boolean validResponses(Map<String, String> responses, String[] requiredKeys) {
        if (responses == null || responses.isEmpty()) {
            return false;
        }

        for (String key : requiredKeys) {
            if (!responses.containsKey(key) || responses.get(key) == null) {
                return false;
            }
        }
        return true;
    }

    public static double personalVehicle(String type, double distance){
        String type_fixed = type.replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
        if (type_fixed.contains("gas")){
            return (0.24 * distance);
        }
        if (type_fixed.contains("electric")){
            return 0.05 * distance;
        }
        if(type_fixed.contains("hybrid")){
            return 0.16 * distance;
        }
        if(type_fixed.contains("diesel")){
            return 0.27 * distance;
        }
        return 0.0;
    }

    public static double personalVehicleCO2(HashMap<String, String> responses){
        String[] requiredKeys = {"Drive Personal Vehicle", "Distance Driven", "Change vehicle type"};
        if (!validResponses(responses, requiredKeys)){
            return 0.0;
        }

        String ownVehicle = responses.get("Drive Personal Vehicle");
        if (!ownVehicle.equals("Yes")){
            return 0.0;
        }

        String distance = responses.get("Distance Driven");
        String carType = responses.get("Change vehicle type");
        double newDistance = Double.parseDouble(distance);

        return personalVehicle(carType, newDistance);
    }

    public static double publicTransport(double hours){
        if(hours == 1){
            return 0.67;
        }
        if(1 < hours && hours <= 3){
            return 2.24;
        }
        if(3 < hours && hours <= 5){
            return 4.49;
        }
        if(5 < hours && hours <= 10){
            return 8.41;
        }
        if(hours > 10){
            return 11.22;
        }
        return 0.0;
    }

    public static double publicTransportCO2(HashMap<String, String> responses){
        String[] requiredKeys = {"Take public transportation", "Type of public transportation", "Time spent on public transport"};
        if (!validResponses(responses, requiredKeys)){
            return 0.0;
        }

        String taken = responses.get("Take public transportation");
        if (!taken.equals("Yes")){
            return 0.0;
        }

        String time = responses.get("Time spent on public transport");
        double newTime = Double.parseDouble(time);

        return publicTransport(newTime);
    }

    public static double shortHaulFlight(int number){
        if(1 <= number && number <= 2){
            return 225;
        }
        if(3 <= number && number <= 5){
            return 600;
        }
        if(6 <= number && number <= 10){
            return 1200;
        }
        if(number >= 10){
            return 1800;
        }
        return 0;
    }

    public static double longHaulFlight(int number){
        if(1 <= number && number <= 2){
            return 825;
        }
        if(3 <= number && number <= 5){
            return 2200;
        }
        if(6 <= number && number <= 10){
            return 4400;
        }
        if(number >= 10){
            return 6600;
        }
        return 0;
    }

    public static double flightCO2(HashMap<String, String> responses){
        String[] requiredKeys = {"Distance traveled", "Number of flights taken today"};
        if (!validResponses(responses, requiredKeys)){
            return 0.0;
        }

        String time = responses.get("Number of flights taken today");
        int num = Integer.parseInt(time);

        String taken = responses.get("Distance traveled");
        String newTaken = taken.replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
        if (newTaken.equals("longhaul")){
            return longHaulFlight(num);
        }
        return shortHaulFlight(num);
    }

    public static double meat(String type, double servings){
        type = type.replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
        if (type.contains("beef")){
            return 3.43 * servings;
        }
        if (type.contains("pork")){
            return 1.99 * servings;
        }
        if (type.contains("chicken")){
            return 1.3 * servings;
        }
        if (type.contains("fish")){
            return 1.2 * servings;
        }
        if (type.contains("plantbased")){
            return 1.37 * servings;
        }
        else{
            return 1.29 * servings;
        }
    }

    public static double meatCO2(HashMap<String, String> responses){
        String[] requiredKeys = {"Meal", "What type of protein did you eat?", "How many servings did you eat?"};
        if (!validResponses(responses, requiredKeys)){
            return 0.0;
        }

        String taken = responses.get("Meal");
        if (!taken.equals("Yes")){
            return 0.0;
        }

        String protein = responses.get("What type of protein did you eat?");
        if (taken.equals("")){
            return 0.0;
        }

        String servings = responses.get("How many servings did you eat?");
        double newServings = Double.parseDouble(servings);

        return meat(protein, newServings);
    }

    public static double clothes(int num){
        return (0.5 * num) * 27.88;
    }

    public static double clothesCO2(HashMap<String, String> responses){
        String[] requiredKeys = {"Buy New Clothes", "How many clothing items did you purchase?"};
        if (!validResponses(responses, requiredKeys)){
            return 0.0;
        }

        String taken = responses.get("Buy New Clothes");
        if (!taken.equals("Yes")){
            return 0.0;
        }

        String num = responses.get("How many clothing items did you purchase?");
        int newNum = Integer.parseInt(num);

        return clothes(newNum);
    }

    public static double electronics(int num){
        if (num == 1){
            return 300;
        }
        if (num == 2){
            return 600;
        }
        if (num == 3){
            return 900;
        }
        if (num >= 4){
            return 1200;
        }
        return 0.0;
    }

    public static double electronicsCO2(HashMap<String, String> responses){
        String[] requiredKeys = {"Buy Electronics", "What types of electronics did you buy?", "How many electronics did you buy?"};
        if (!validResponses(responses, requiredKeys)){
            return 0.0;
        }

        String taken = responses.get("Buy Electronics");
        if (!taken.equals("Yes")){
            return 0.0;
        }

        String type = responses.get("What types of electronics did you buy?");
        if (!type.isEmpty()){
            return 0.0;
        }

        String num = responses.get("How many electronics did you buy?");
        int newNum = Integer.parseInt(num);

        return electronics(newNum);
    }

    public static double gasBill(double paid){
        if (paid < 50){
            return 7.78;
        }
        if (50 <= paid && paid < 100){
            return 8.08;
        }
        if (100 <= paid && paid < 150){
            return 10.93;
        }
        if (150 <= paid && paid < 200){
            return 14.28;
        }
        return 18.78;
    }

    public static double gasBillCO2(HashMap<String, String> responses){
        String[] requiredKeys = {"Gas Paid"};
        if (!validResponses(responses, requiredKeys)){
            return 0.0;
        }

        String paid = responses.get("Gas Paid");
        if (paid.equals("0")){
            return 0.0;
        }

        double newPaid = Double.parseDouble(paid);
        return gasBill(newPaid);
    }

    public static double elecBill(double paid){
        if (paid < 50){
            return 1.15;
        }
        if (50 <= paid && paid < 100){
            return 2.28;
        }
        if (100 <= paid && paid < 150){
            return 4.3;
        }
        if (150 <= paid && paid < 200){
            return 5.96;
        }
        return 6.96;
    }

    public static double elecBillCO2(HashMap<String, String> responses){
        String[] requiredKeys = {"Electricity Paid"};
        if (!validResponses(responses, requiredKeys)){
            return 0.0;
        }

        String paid = responses.get("Electricity Paid");
        if (paid.equals("0")){
            return 0.0;
        }

        double newPaid = Double.parseDouble(paid);
        return elecBill(newPaid);
    }

    public static double waterBill(double paid){
        if (paid < 50){
            return 13.7;
        }
        if (50 <= paid && paid < 100){
            return 20.5;
        }
        if (100 <= paid && paid < 150){
            return 34.2;
        }
        if (150 <= paid && paid < 200){
            return 47.9;
        }
        return 61.6;
    }

    public static double waterBillCO2(HashMap<String, String> responses){
        String[] requiredKeys = {"Water Paid"};
        if (!validResponses(responses, requiredKeys)){
            return 0.0;
        }

        String paid = responses.get("Water Paid");
        if (paid.equals("0")){
            return 0.0;
        }

        double newPaid = Double.parseDouble(paid);
        return waterBill(newPaid);
    }

    public static double otherPurchases(int num){
        return num * 300;
    }

    public static double otherPurchasesCO2(HashMap<String, String> responses){
        String[] requiredKeys = {"Other Purchases", "How many clothing items did you purchase?"};
        if (!validResponses(responses, requiredKeys)){
            return 0.0;
        }

        String taken = responses.get("Other Purchases");
        if (!taken.equals("Yes")){
            return 0.0;
        }

        String num = responses.get("How many things have you purchased?");
        int newNum = Integer.parseInt(num);

        return otherPurchases(newNum);
    }
}