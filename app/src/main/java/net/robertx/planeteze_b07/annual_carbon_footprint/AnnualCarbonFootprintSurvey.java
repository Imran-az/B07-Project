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

import net.robertx.planeteze_b07.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AnnualCarbonFootprintSurvey extends AppCompatActivity {

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
        toolbar.setNavigationOnClickListener(v -> {
            NavUtils.navigateUpFromSameTask(AnnualCarbonFootprintSurvey.this);
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.survey_pages_main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ViewPager2 viewPager = findViewById(R.id.surveyPages);

        HashMap<String, String> answers = new HashMap<>();

        List<SurveyQuestionFragment> fragments = new ArrayList<>();
        fragments.add(SurveyQuestionFragment.newInstance("Do you own or regularly use a car?", new String[]{"Yes", "No"}));
        // car questions should not be displayed unless they answer yes
        fragments.add(SurveyQuestionFragment.newInstance("What type of car do you drive?", new String[]{"Gasoline", "Diesel", "Hybrid", "Electric", "I don't know"}));
        fragments.add(SurveyQuestionFragment.newInstance("How many kilometers/miles do you drive per year?", new String[]{"Up to 5,000 km (3,000 miles)", "5,000-10,000 km (3,000 - 6,000 miles)", "10,000 - 15,000 km (6,000 - 9,000) miles", "15,000 - 20,000 km (9,000 - 12,000 mile)", "20,000 - 25,000 (12,000 - 15,000 miles)", "More than 25,000 km (15,000 miles)"}));

        fragments.add(SurveyQuestionFragment.newInstance("How often do you use public transportation (bus, train, subway)?", new String[] {"Never", "Occasionally (1-2 times/week)", "Frequently (3-4 times/week)", "Always (5+ times/week)"}));




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
}