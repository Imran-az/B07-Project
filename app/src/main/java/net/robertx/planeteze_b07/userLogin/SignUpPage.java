package net.robertx.planeteze_b07.userLogin;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import net.robertx.planeteze_b07.R;

/**
 * SignUpPage Activity allows users to register for a new account.
 * Users provide their first name, last name, email, and password to create an account.
 * The account is stored in Firebase Authentication and Realtime Database.
 */
public class SignUpPage extends AppCompatActivity {

    /**
     * EditText for the user's email input.
     */
    private EditText emailInput;

    /**
     * EditText for the user's password input.
     */
    private EditText passwordInput;

    /**
     * EditText for the user's first name input.
     */
    private EditText firstNameInput;

    /**
     * EditText for the user's last name input.
     */
    private EditText lastNameInput;

    /**
     * ProgressBar to indicate the registration process.
     */
    private ProgressBar registrationProgressBar;

    /**
     * Firebase Authentication instance.
     */
    private FirebaseAuth firebaseAuth;

    /**
     * Firebase Database instance.
     */
    private FirebaseDatabase firebaseDatabase;

    /**
     * Database reference for the users node.
     */
    private DatabaseReference usersDatabaseReference;

    /**
     * Called when the activity is first created. Initializes the UI components, Firebase, and sets up event listeners.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down,
     *                           this Bundle contains the data it most recently supplied in onSaveInstanceState.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up_page);

        firebaseAuth = FirebaseAuth.getInstance();

        emailInput = findViewById(R.id.emailbox_signup2);
        passwordInput = findViewById(R.id.password_signup2);
        firstNameInput = findViewById(R.id.firstname2);
        lastNameInput = findViewById(R.id.lastname2);
        Button registerButton = findViewById(R.id.signup_confirm2);
        registrationProgressBar = findViewById(R.id.progressbar_signup);
        Button loginButton = findViewById(R.id.loginbutton_signup2);
        Button backButton = findViewById(R.id.returnhomescreen_signup2);

        loginButton.setOnClickListener(v -> {
            Intent intent = new Intent(SignUpPage.this, LoginPageView.class);
            startActivity(intent);
            finish();
        });

        registerButton.setOnClickListener(v -> {
            registrationProgressBar.setVisibility(View.VISIBLE);
            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();

            if (TextUtils.isEmpty(email)) {
                Toast.makeText(SignUpPage.this, "Enter email", Toast.LENGTH_SHORT).show();
                registrationProgressBar.setVisibility(View.GONE);
                return;
            }
            if (TextUtils.isEmpty(password)) {
                Toast.makeText(SignUpPage.this, "Enter password", Toast.LENGTH_SHORT).show();
                registrationProgressBar.setVisibility(View.GONE);
                return;
            }

            firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        registrationProgressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                            if (currentUser != null) {
                                currentUser.sendEmailVerification().addOnCompleteListener(emailTask -> {
                                    if (emailTask.isSuccessful()) {
                                        String firstName = firstNameInput.getText().toString().trim();
                                        String lastName = lastNameInput.getText().toString().trim();
                                        String userId = currentUser.getUid();

                                        if (!TextUtils.isEmpty(firstName) && !TextUtils.isEmpty(lastName) && !TextUtils.isEmpty(password)) {
                                            User newUser = new User(userId, firstName, lastName, email, password);
                                            firebaseDatabase = FirebaseDatabase.getInstance();
                                            usersDatabaseReference = firebaseDatabase.getReference("Users");
                                            usersDatabaseReference.child(userId).setValue(newUser)
                                                    .addOnCompleteListener(databaseTask -> {
                                                        if (databaseTask.isSuccessful()) {
                                                            // Clear input fields after successful registration
                                                            firstNameInput.setText("");
                                                            lastNameInput.setText("");
                                                            emailInput.setText("");
                                                            passwordInput.setText("");
                                                        }
                                                    });
                                        }

                                        Toast.makeText(SignUpPage.this, "User registered successfully. Please verify your email.",
                                                Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(SignUpPage.this, LoginPageView.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        Toast.makeText(SignUpPage.this, "Failed to send email verification.",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } else {
                                Toast.makeText(SignUpPage.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(SignUpPage.this, "Registration failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(SignUpPage.this, WelcomePage.class);
            startActivity(intent);
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}
