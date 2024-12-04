package net.robertx.planeteze_b07.annual_carbon_footprint;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.robertx.planeteze_b07.R;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

/**
 * Fragment representing a single survey question.
 *
 * This class displays a survey question and its corresponding options in a
 * radio button system. It allows users to select an option and provides methods
 * to retrieve the selected answer.
 */
public class SurveyQuestionFragment extends Fragment {
    /**
     * Key for storing the survey question text in the fragment's arguments.
     */
    private static final String ARG_QUESTION = "question";

    /**
     * Key for storing the survey question options in the fragment's arguments.
     */
    private static final String ARG_OPTIONS = "options";

    /**
     * The survey question text to be displayed in the fragment.
     */
    private String question;

    /**
     * The options for the survey question, displayed as a list of choices.
     */
    private String[] options;

    /**
     * Creates a new instance of {@link SurveyQuestionFragment} with the specified question and options.
     *
     * This method initializes the fragment with the provided question and options,
     * bundles them as arguments, and returns the newly created fragment.
     *
     * @param question the text of the survey question to be displayed in the fragment.
     * @param options an array of strings representing the options for the survey question.
     * @return a new instance of {@link SurveyQuestionFragment} with the provided question and options.
     */
    public static SurveyQuestionFragment newInstance(String question, String[] options) {
        SurveyQuestionFragment fragment = new SurveyQuestionFragment();
        Bundle args = new Bundle();
        args.putString(ARG_QUESTION, question);
        args.putStringArray(ARG_OPTIONS, options);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Called when the fragment is first attached to its context.
     *
     * This method is invoked when the fragment is associated with an {@link Activity}.
     * It ensures that the fragment is correctly attached before performing any further setup.
     *
     * @param context the {@link Context} to which the fragment is attached.
     */
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    /**
     * Called when the fragment is created.
     *
     * This method initializes the fragment by retrieving the question and options
     * from the fragment's arguments, which were passed when the fragment was created.
     *
     * @param savedInstanceState a {@link Bundle} containing the fragment's previously saved state,
     *                           or null if this is a new instance.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            question = getArguments().getString(ARG_QUESTION);
            options = getArguments().getStringArray(ARG_OPTIONS);
        }
    }

    /**
     * Called to create the view for the fragment.
     *
     * This method inflates the fragment's layout, retrieves the question and options
     * from the fragment's arguments, and populates the view with the question and
     * corresponding options. It also sets the default option in the {@link RadioButtonSystem}.
     *
     * @param inflater the {@link LayoutInflater} used to inflate the fragment's layout.
     * @param container the parent {@link ViewGroup} that the fragment's UI will be attached to, or null.
     * @param savedInstanceState a {@link Bundle} containing the fragment's previously saved state,
     *                           or null if none.
     * @return the root {@link View} for the fragment's UI.
     */
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

        optionsGroup.setDefaultOption(0);


        return view;
    }

    /**
     * Returns the survey question for this fragment.
     *
     * This method retrieves the question text that was passed to the fragment as an argument.
     *
     * @return the survey question as a {@link String}.
     */
    public String getQuestion() {
        return question;
    }

    /**
     * Retrieves the selected option for the survey question.
     *
     * This method fetches the selected option from the {@link RadioButtonSystem} view,
     * based on the user's choice. It returns the corresponding option from the list of available options.
     *
     * @return the selected option as a {@link String}, or {@code null} if no option is selected.
     */
    public String getSelectedOption() {
        RadioButtonSystem options = this.getView().findViewById(R.id.options_radio_button_system);
        return this.options[options.getSelectedIndex()];
    }
}
