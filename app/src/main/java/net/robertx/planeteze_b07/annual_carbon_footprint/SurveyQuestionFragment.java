package net.robertx.planeteze_b07.annual_carbon_footprint;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import net.robertx.planeteze_b07.R;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

public class SurveyQuestionFragment extends Fragment {
    private static final String ARG_QUESTION = "question";
    private static final String ARG_OPTIONS = "options";

    private String question;
    private String[] options;
    private ViewPager2 viewPager;


    public static SurveyQuestionFragment newInstance(String question, String[] options) {
        SurveyQuestionFragment fragment = new SurveyQuestionFragment();
        Bundle args = new Bundle();
        args.putString(ARG_QUESTION, question);
        args.putStringArray(ARG_OPTIONS, options);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof AnnualCarbonFootprintSurvey) {
            viewPager = ((AnnualCarbonFootprintSurvey) context).findViewById(R.id.surveyPages);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            question = getArguments().getString(ARG_QUESTION);
            options = getArguments().getStringArray(ARG_OPTIONS);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_survey_question, container, false);

        if (getArguments() != null) {
            question = getArguments().getString(ARG_QUESTION);
            options = getArguments().getStringArray(ARG_OPTIONS);
        }

        TextView questionTextView = view.findViewById(R.id.question_text_view);
        RadioButtonSystem optionsGroup = view.findViewById(R.id.options_radio_button_system);

        questionTextView.setText(question);

        for (String option : options) {
            optionsGroup.addOption(option);
        }


        return view;
    }
}
