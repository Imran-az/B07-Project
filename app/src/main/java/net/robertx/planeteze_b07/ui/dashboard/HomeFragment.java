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

import net.robertx.planeteze_b07.R;
import net.robertx.planeteze_b07.annualCarbonTracker.ResultsActivity;
import net.robertx.planeteze_b07.annual_carbon_footprint.AnnualCarbonFootprintSurvey;

public class HomeFragment extends Fragment {

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    FirebaseAuth auth = FirebaseAuth.getInstance();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        view.findViewById(R.id.cardView).setOnClickListener(v -> {
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

        return view;
    }
}
