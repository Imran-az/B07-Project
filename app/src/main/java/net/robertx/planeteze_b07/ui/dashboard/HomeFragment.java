package net.robertx.planeteze_b07.ui.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import net.robertx.planeteze_b07.dailySurvey.DailySurveyHomePage;
import net.robertx.planeteze_b07.ecoGauge.EcoGaugeActivity;
import net.robertx.planeteze_b07.ecoTracker.HabitDecision;
import net.robertx.planeteze_b07.R;
import net.robertx.planeteze_b07.annualCarbonTracker.ResultsActivity;
import net.robertx.planeteze_b07.annual_carbon_footprint.AnnualCarbonFootprintSurvey;

/**
 * Fragment for the Home screen.
 *
 * This class provides navigation to various features of the application,
 * including the annual carbon footprint survey, survey results, eco tracker,
 * eco gauge, and habit tracker. It also integrates with Firebase Firestore
 * and Firebase Authentication to manage user-specific data.
 */
public class HomeFragment extends Fragment {

    /**
     * Instance of Firebase Firestore used for accessing and managing
     * Firestore database collections and documents.
     */
    final FirebaseFirestore db = FirebaseFirestore.getInstance();

    /**
     * Instance of Firebase Authentication used for managing user authentication
     * and retrieving the current authenticated user.
     */
    final FirebaseAuth auth = FirebaseAuth.getInstance();

    /**
     * Initializes the Home screen's user interface.
     *
     * This method inflates the fragment's layout, sets up click listeners for navigation
     * to different features of the application, and manages user-specific survey data
     * using Firebase Firestore and Firebase Authentication.
     *
     * @param inflater the {@link LayoutInflater} used to inflate the fragment's layout.
     * @param container the parent {@link ViewGroup} that the fragment's UI will be attached to, or null.
     * @param savedInstanceState a {@link Bundle} containing the fragment's previously saved state, or null if none.
     * @return the root {@link View} for the fragment's UI.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        view.findViewById(R.id.card_annual_footprint).setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AnnualCarbonFootprintSurvey.class);
            startActivity(intent);
        });

        view.findViewById(R.id.cardViewSurveyResults).setEnabled(false);
        view.findViewById(R.id.viewSurveyResultsCardIcon).setEnabled(false);

        if (auth.getCurrentUser() != null) {
            CollectionReference collection = db.collection("AnnualCarbonFootprintSurveyData");
            // check if document with path auth.getCurrentUser().getUid() exists
            collection.document(auth.getCurrentUser().getUid()).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    if (task.getResult().exists()) {
                        view.findViewById(R.id.textAlreadyTakenSurvey).setVisibility(View.VISIBLE);
                        view.findViewById(R.id.textCantViewResultsWithoutTakingSurvey).setVisibility(View.INVISIBLE);
                        view.findViewById(R.id.cardViewSurveyResults).setEnabled(true);
                        view.findViewById(R.id.viewSurveyResultsCardIcon).setEnabled(true);
                    }
                }
            });
        }


        view.findViewById(R.id.cardViewSurveyResults).setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ResultsActivity.class);
            startActivity(intent);
        });

        view.findViewById(R.id.card_eco_tracker).setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), DailySurveyHomePage.class);
            startActivity(intent);
        });

        view.findViewById(R.id.card_eco_gauge).setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), EcoGaugeActivity.class);
            startActivity(intent);
        });

        view.findViewById(R.id.card_habit_tracker).setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), HabitDecision.class);
            startActivity(intent);
        });

        return view;
    }
}
