package net.robertx.planeteze_b07.UserLogin;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
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

    // UI Components for user inputs and buttons
    private EditText emailInput, passwordInput, firstNameInput, lastNameInput;
    private ProgressBar registrationProgressBar;

    // Firebase Authentication and Realtime Database
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference usersDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up_page);

        // Initialize Firebase Authentication
        firebaseAuth = FirebaseAuth.getInstance();

        // Initialize UI components
        emailInput = findViewById(R.id.emailbox_signup2);
        passwordInput = findViewById(R.id.password_signup2);
        firstNameInput = findViewById(R.id.firstname2);
        lastNameInput = findViewById(R.id.lastname2);
        Button registerButton = findViewById(R.id.signup_confirm2);
        registrationProgressBar = findViewById(R.id.progressbar_signup);
        Button loginButton = findViewById(R.id.loginbutton_signup2);
        Button backButton = findViewById(R.id.returnhomescreen_signup2);

        // Redirect to the login page when the login button is clicked
        loginButton.setOnClickListener(v -> {
            Intent intent = new Intent(SignUpPage.this, LoginPageView.class);
            startActivity(intent);
            finish();
        });

        // Handle user registration when the register button is clicked
        registerButton.setOnClickListener(v -> {
            registrationProgressBar.setVisibility(View.VISIBLE);
            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();

            // Validate user input for email and password
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

            // Register the user with Firebase Authentication
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        registrationProgressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            // Get the current authenticated user
                            FirebaseUser currentUser = firebaseAuth.getCurrentUser();

                            if (currentUser != null) {
                                // Send email verification
                                currentUser.sendEmailVerification().addOnCompleteListener(emailTask -> {
                                    if (emailTask.isSuccessful()) {
                                        String firstName = firstNameInput.getText().toString().trim();
                                        String lastName = lastNameInput.getText().toString().trim();
                                        String userId = currentUser.getUid();

                                        // Store user details in Realtime Database
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

                                        // Notify the user and navigate to the login page
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

        // Redirect to the welcome screen when the back button is clicked
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(SignUpPage.this, WelcomePage.class);
            startActivity(intent);
        });

        // Adjust layout padding for system window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}
