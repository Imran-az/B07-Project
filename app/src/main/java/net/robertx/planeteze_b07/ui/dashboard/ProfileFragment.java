package net.robertx.planeteze_b07.ui.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import net.robertx.planeteze_b07.R;
import net.robertx.planeteze_b07.UserLogin.WelcomePage;

import java.util.Objects;

public class ProfileFragment extends Fragment {

    private TextView profileName, profileEmail;
    private FirebaseAuth auth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        profileName = view.findViewById(R.id.profile_name);
        profileEmail = view.findViewById(R.id.profile_email);

        auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser != null) {
            profileEmail.setText(currentUser.getEmail());
        }

        Button logoutButton = view.findViewById(R.id.button_sign_out);
        logoutButton.setOnClickListener(v -> {
            auth.signOut();
            Intent intent = new Intent(getActivity(), WelcomePage.class);
            startActivity(intent);
            requireActivity().finish();
        });

        return view;
    }
}