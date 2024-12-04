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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import net.robertx.planeteze_b07.R;
import net.robertx.planeteze_b07.userLogin.WelcomePage;

/**
 * Fragment for displaying and managing the user's profile.
 *
 * This class displays user information such as first name, last name, email, and user ID.
 * It also provides functionality for the user to sign out and navigate back to the welcome page.
 * User data is fetched from Firebase Authentication and Firebase Realtime Database.
 */
public class ProfileFragment extends Fragment {

    /**
     * The first name of the currently authenticated user, fetched from the database.
     */
    private String firstName;

    /**
     * The last name of the currently authenticated user, fetched from the database.
     */
    private String lastName;

    /**
     * Instance of Firebase Authentication used to manage authentication
     * and retrieve the currently authenticated user.
     */
    private final FirebaseAuth auth = FirebaseAuth.getInstance();

    /**
     * Instance of Firebase Realtime Database used to fetch user-specific data.
     */
    private final FirebaseDatabase database = FirebaseDatabase.getInstance();

    /**
     * The currently authenticated user, retrieved from Firebase Authentication.
     */
    private final FirebaseUser currentUser = auth.getCurrentUser();

    /**
     * Sets up the user's profile view.
     *
     * This method populates the user's profile with data fetched from Firebase Authentication
     * and Firebase Realtime Database, including their first name, last name, email, and user ID.
     * It also sets up a logout button that allows the user to sign out and navigate back to the welcome page.
     *
     * @param view the root {@link View} for the fragment's UI.
     * @param savedInstanceState a {@link Bundle} containing the fragment's previously saved state, or null if none.
     */
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

    /**
     * Initializes the view for the Profile fragment.
     *
     * This method inflates the layout for the fragment, setting up the user interface
     * for displaying the user's profile information and logout functionality.
     *
     * @param inflater the {@link LayoutInflater} used to inflate the fragment's layout.
     * @param container the parent {@link ViewGroup} that the fragment's UI will be attached to, or null.
     * @param savedInstanceState a {@link Bundle} containing the fragment's previously saved state, or null if none.
     * @return the root {@link View} for the fragment's UI.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        return view;
    }
}