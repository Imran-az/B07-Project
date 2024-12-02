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

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import net.robertx.planeteze_b07.R;
import net.robertx.planeteze_b07.UserLogin.WelcomePage;

public class ProfileFragment extends Fragment {

    private String firstName, lastName;
    private FirebaseAuth auth = FirebaseAuth.getInstance();

    private FirebaseDatabase database = FirebaseDatabase.getInstance();

    private FirebaseUser currentUser = auth.getCurrentUser();

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView profileFirstName = view.findViewById(R.id.profile_first_name);
        TextView profileLastName = view.findViewById(R.id.profile_last_name);
        TextView profileUserId = view.findViewById(R.id.profile_user_id);
        TextView profileEmail = view.findViewById(R.id.profile_email);

        if (currentUser != null) {
            profileEmail.setText(currentUser.getEmail());
            profileUserId.setText(currentUser.getUid());

            DatabaseReference userDetailsRef = database.getReference("Users").child(currentUser.getUid());
            userDetailsRef.get().addOnCompleteListener( result -> {
                DataSnapshot resp = result.getResult();
                if (resp != null ) {
                    firstName = resp.child("firstName").getValue(String.class);
                    lastName = resp.child("lastName").getValue(String.class);
                }
                profileFirstName.setText(firstName);
                profileLastName.setText(lastName);

            });

        }

        Button logoutButton = view.findViewById(R.id.button_sign_out);
        logoutButton.setEnabled(currentUser != null);
        logoutButton.setOnClickListener(v -> {
            auth.signOut();
            Intent intent = new Intent(getActivity(), WelcomePage.class);
            startActivity(intent);
            requireActivity().finish();
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        return view;
    }
}