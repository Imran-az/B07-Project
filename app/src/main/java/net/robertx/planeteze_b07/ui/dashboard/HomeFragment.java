package net.robertx.planeteze_b07.ui.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import net.robertx.planeteze_b07.R;
import net.robertx.planeteze_b07.annualCarbonTracker.ResultsActivity;
import net.robertx.planeteze_b07.annual_carbon_footprint.AnnualCarbonFootprintSurvey;

public class HomeFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        view.findViewById(R.id.cardView).setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AnnualCarbonFootprintSurvey.class);
            startActivity(intent);
        });

        view.findViewById(R.id.cardViewResults).setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ResultsActivity.class);
            startActivity(intent);
        });

        return view;
    }
}
