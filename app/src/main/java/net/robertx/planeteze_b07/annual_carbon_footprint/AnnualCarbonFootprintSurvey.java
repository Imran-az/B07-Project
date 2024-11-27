package net.robertx.planeteze_b07.annual_carbon_footprint;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import net.robertx.planeteze_b07.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AnnualCarbonFootprintSurvey extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_annual_carbon_footprint_survey);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.survey_pages_main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        MaterialToolbar toolbar = findViewById(R.id.materialToolbar);
        toolbar.setNavigationOnClickListener(v -> NavUtils.navigateUpFromSameTask(AnnualCarbonFootprintSurvey.this));

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.survey_pages_main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();


        ViewPager2 viewPager = findViewById(R.id.surveyPages);

        HashMap<String, String> answers = new HashMap<>();

        List<SurveyQuestionFragment> fragments = initSurveyQuestions();


        ViewPagerAdapter adapter = new ViewPagerAdapter(this, fragments);
        viewPager.setAdapter(adapter);

        Button nextButton = findViewById(R.id.next_button);
        Button previousButton = findViewById(R.id.previous_button);

        nextButton.setEnabled(viewPager.getCurrentItem() < viewPager.getAdapter().getItemCount() - 1);
        nextButton.setOnClickListener(v -> {
            int currentItem = viewPager.getCurrentItem();
            SurveyQuestionFragment currentFragment = fragments.get(currentItem);

            if (currentItem == 0) {
                // if they responded no to owning a car, skip the car questions
                if (currentFragment.getSelectedOption().equals("No")) {
                    viewPager.setCurrentItem(3, true);
                    nextButton.setEnabled(viewPager.getCurrentItem() < viewPager.getAdapter().getItemCount() - 1);
                    previousButton.setEnabled(viewPager.getCurrentItem() > 0);
                    return;
                }
            }

            answers.put(currentFragment.getQuestion(), currentFragment.getSelectedOption());
            viewPager.setCurrentItem(currentItem + 1, true);
            nextButton.setEnabled(viewPager.getCurrentItem() < viewPager.getAdapter().getItemCount() - 1);
            previousButton.setEnabled(viewPager.getCurrentItem() > 0);

            // if the current item is 1 from the last item, show the submit button
            if (viewPager.getCurrentItem() == viewPager.getAdapter().getItemCount() - 1) {
                nextButton.setEnabled(true);
                nextButton.setText("Submit");
                nextButton.setOnClickListener(v1 -> {
                    answers.put(fragments.get(fragments.size() - 1).getQuestion(), fragments.get(fragments.size() - 1).getSelectedOption());
                    nextButton.setEnabled(false);
                    previousButton.setEnabled(false);
                    saveSurveyResults(answers);
                    finish();
                });
            }

        });


        previousButton.setEnabled(viewPager.getCurrentItem() > 0);
        previousButton.setOnClickListener(v -> {
            int currentItem = viewPager.getCurrentItem();
            SurveyQuestionFragment currentFragment = fragments.get(currentItem);

            if (currentItem == 3) {
                // if they responded no to owning a car, skip the car questions
                if (fragments.get(0).getSelectedOption().equals("No")) {
                    viewPager.setCurrentItem(0, true);
                    nextButton.setEnabled(viewPager.getCurrentItem() < viewPager.getAdapter().getItemCount() - 1);
                    previousButton.setEnabled(viewPager.getCurrentItem() > 0);
                    return;
                }
            }

            answers.put(currentFragment.getQuestion(), currentFragment.getSelectedOption());
            viewPager.setCurrentItem(currentItem - 1, true);
            nextButton.setEnabled(viewPager.getCurrentItem() < viewPager.getAdapter().getItemCount() - 1);
            previousButton.setEnabled(viewPager.getCurrentItem() > 0);
        });
    }

    private ArrayList<SurveyQuestionFragment> initSurveyQuestions() {
        ArrayList<SurveyQuestionFragment> fragments = new ArrayList<>();
        fragments.add(SurveyQuestionFragment.newInstance("Do you own or regularly use a car?", new String[]{"Yes", "No"}));
        fragments.add(SurveyQuestionFragment.newInstance("What type of car do you drive?", new String[]{"Gasoline", "Diesel", "Hybrid", "Electric", "I don't know"}));
        fragments.add(SurveyQuestionFragment.newInstance("How many kilometers/miles do you drive per year?", new String[]{"Up to 5,000 km (3,000 miles)", "5,000–10,000 km (3,000–6,000 miles)", "10,000–15,000 km (6,000–9,000 miles)", "15,000–20,000 km (9,000–12,000 miles)", "20,000–25,000 km (12,000–15,000 miles)", "More than 25,000 km (15,000 miles)"}));
        fragments.add(SurveyQuestionFragment.newInstance("How often do you use public transportation (bus, train, subway)?", new String[]{"Never", "Occasionally (1-2 times/week)", "Frequently (3-4 times/week)", "Always (5+ times/week)"}));
        fragments.add(SurveyQuestionFragment.newInstance("How much time do you spend on public transport per week (bus, train, subway)?", new String[]{"Under 1 hour", "1-3 hours", "3-5 hours", "5-10 hours", "More than 10 hours"}));
        fragments.add(SurveyQuestionFragment.newInstance("How many short-haul flights (less than 1,500 km / 932 miles) have you taken in the past year?", new String[]{"None", "1-2 flights", "3-5 flights", "6-10 flights", "More than 10 flights"}));
        fragments.add(SurveyQuestionFragment.newInstance("How many long-haul flights (more than 1,500 km / 932 miles) have you taken in the past year?", new String[]{"None", "1-2 flights", "3-5 flights", "6-10 flights", "More than 10 flights"}));
        fragments.add(SurveyQuestionFragment.newInstance("What best describes your diet?", new String[]{"Vegetarian", "Vegan", "Pescatarian (fish/seafood)", "Meat-based (eat all types of animal products)"}));
        fragments.add(SurveyQuestionFragment.newInstance("How often do you eat the following animal-based products? Beef:", new String[]{"Daily", "Frequently (3-5 times/week)", "Occasionally (1-2 times/week)", "Never"}));
        fragments.add(SurveyQuestionFragment.newInstance("How often do you eat the following animal-based products? Pork:", new String[]{"Daily", "Frequently (3-5 times/week)", "Occasionally (1-2 times/week)", "Never"}));
        fragments.add(SurveyQuestionFragment.newInstance("How often do you eat the following animal-based products? Chicken:", new String[]{"Daily", "Frequently (3-5 times/week)", "Occasionally (1-2 times/week)", "Never"}));
        fragments.add(SurveyQuestionFragment.newInstance("How often do you eat the following animal-based products? Fish/Seafood:", new String[]{"Daily", "Frequently (3-5 times/week)", "Occasionally (1-2 times/week)", "Never"}));
        fragments.add(SurveyQuestionFragment.newInstance("How often do you waste food or throw away uneaten leftovers?", new String[]{"Never", "Rarely", "Occasionally", "Frequently"}));
        fragments.add(SurveyQuestionFragment.newInstance("What type of home do you live in?", new String[]{"Detached house", "Semi-detached house", "Townhouse", "Condo/Apartment", "Other"}));
        fragments.add(SurveyQuestionFragment.newInstance("How many people live in your household?", new String[]{"1", "2", "3-4", "5 or more"}));
        fragments.add(SurveyQuestionFragment.newInstance("What is the size of your home?", new String[]{"Under 1000 sq. ft.", "1000-2000 sq. ft.", "Over 2000 sq. ft."}));
        fragments.add(SurveyQuestionFragment.newInstance("What type of energy do you use to heat your home?", new String[]{"Natural Gas", "Electricity", "Oil", "Propane", "Wood", "Other"}));
        fragments.add(SurveyQuestionFragment.newInstance("What is your average monthly electricity bill?", new String[]{"Under $50", "$50-$100", "$100-$150", "$150-$200", "Over $200"}));
        fragments.add(SurveyQuestionFragment.newInstance("What type of energy do you use to heat water in your home?", new String[]{"Natural Gas", "Electricity", "Oil", "Propane", "Solar", "Other"}));
        fragments.add(SurveyQuestionFragment.newInstance("Do you use any renewable energy sources for electricity or heating (e.g., solar, wind)?", new String[]{"Yes, primarily (more than 50% of energy use)", "Yes, partially (less than 50% of energy use)", "No"}));
        fragments.add(SurveyQuestionFragment.newInstance("How often do you buy new clothes?", new String[]{"Monthly", "Quarterly", "Annually", "Rarely"}));
        fragments.add(SurveyQuestionFragment.newInstance("Do you buy second-hand or eco-friendly products?", new String[]{"Yes, regularly", "Yes, occasionally", "No"}));
        fragments.add(SurveyQuestionFragment.newInstance("How many electronic devices (phones, laptops, etc.) have you purchased in the past year?", new String[]{"None", "1", "2", "3 or more"}));
        fragments.add(SurveyQuestionFragment.newInstance("How often do you recycle?", new String[]{"Never", "Occasionally", "Frequently", "Always"}));
        return fragments;
    }

    private void saveSurveyResults(HashMap<String, String> answers) {
        String userId = firebaseAuth.getCurrentUser().getUid();
        firestore.collection("AnnualCarbonFootprintSurveyData").document(userId).set(answers).addOnSuccessListener(aVoid -> Toast.makeText(this, "Survey submitted successfully!", Toast.LENGTH_SHORT).show()).addOnFailureListener(e -> Toast.makeText(this, "Error submitting survey: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}