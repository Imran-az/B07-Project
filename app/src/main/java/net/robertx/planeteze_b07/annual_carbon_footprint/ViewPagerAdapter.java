package net.robertx.planeteze_b07.annual_carbon_footprint;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.List;

/**
 * Adapter for managing survey question fragments in a {@link ViewPager2}.
 *
 * This class extends {@link FragmentStateAdapter} to handle the display and
 * navigation of {@link SurveyQuestionFragment} instances within a ViewPager.
 * It provides methods to create and retrieve fragments for the survey.
 */
public class ViewPagerAdapter extends FragmentStateAdapter {

    /**
     * A list of {@link SurveyQuestionFragment} instances representing the survey questions.
     *
     * Each fragment corresponds to a single question in the survey and is managed by the adapter
     * for navigation and display in the {@link ViewPager2}.
     */
    private final List<SurveyQuestionFragment> fragments;

    /**
     * Constructs a {@code ViewPagerAdapter} with the specified activity and survey fragments.
     *
     * This constructor initializes the adapter with a list of {@link SurveyQuestionFragment}
     * instances and associates it with the provided {@link FragmentActivity}.
     *
     * @param activity the {@link FragmentActivity} that hosts the ViewPager.
     * @param fragments a {@link List} of {@link SurveyQuestionFragment} instances representing the survey questions.
     */
    public ViewPagerAdapter(FragmentActivity activity, List<SurveyQuestionFragment> fragments) {
        super(activity);
        this.fragments = fragments;
    }

    /**
     * Creates and returns the fragment for the specified position.
     *
     * This method retrieves the {@link SurveyQuestionFragment} at the given position
     * from the list of fragments managed by the adapter.
     *
     * @param position the index of the fragment to be created.
     * @return the {@link Fragment} at the specified position.
     */
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return fragments.get(position);
    }

    /**
     * Returns the total number of fragments managed by the adapter.
     *
     * This method provides the number of {@link SurveyQuestionFragment} instances
     * available for navigation in the {@link ViewPager2}.
     *
     * @return the total number of fragments.
     */
    @Override
    public int getItemCount() {
        return fragments.size();
    }
}