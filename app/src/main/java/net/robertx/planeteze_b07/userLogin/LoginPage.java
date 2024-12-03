package net.robertx.planeteze_b07.userLogin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.text.TextUtils;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import net.robertx.planeteze_b07.dailySurvey.QuestionList;
import net.robertx.planeteze_b07.Dashboard;
import net.robertx.planeteze_b07.R;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * LoginPage Activity provides an interface for users to log in to their account.
 * Users can enter their credentials, navigate to the signup or forgot password pages,
 * and log in if their email is verified.
 */
public class LoginPage extends AppCompatActivity {

    // UI Components for login and navigation
    private EditText emailaddress_login, password_login;
    private ProgressBar progressBar;

    // Firebase Authentication
    private FirebaseAuth mAuth;

    //comment out this method for testing login (Until sign out button is implemented)
    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Intent intent = new Intent(getApplicationContext(),
                    Dashboard.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login_page);

        // Initialize Firebase Authentication
        mAuth = FirebaseAuth.getInstance();

        // Initialize UI components
        emailaddress_login = findViewById(R.id.emailaddress_login2);
        password_login = findViewById(R.id.emailaddress_forgotpassword);
        Button loginbutton = findViewById(R.id.resetPasswordButton);
        Button forgotPasswordButton = findViewById(R.id.resetPasswordbutton2);
        Button signupForAccountButton = findViewById(R.id.signupbutton_login2);
        progressBar = findViewById(R.id.progressbar_login);
        Button backbutton2 = findViewById(R.id.backbutton_login2);

        // Back button: Navigate to the WelcomePage
        backbutton2.setOnClickListener(v -> {
            Intent intent = new Intent(LoginPage.this, QuestionList.class);
            startActivity(intent);
        });

        // Signup button: Navigate to the SignUpPage
        signupForAccountButton.setOnClickListener(v -> {
            Intent intent = new Intent(LoginPage.this, SignUpPage.class);
            startActivity(intent);
        });

        // Login button: Attempt to authenticate with email and password
        loginbutton.setOnClickListener(v -> {
            // Retrieve input from text fields
            String email = String.valueOf(emailaddress_login.getText());
            String password = String.valueOf(password_login.getText());
            progressBar.setVisibility(View.VISIBLE);

            // Validate email input
            if (TextUtils.isEmpty(email)) {
                Toast.makeText(LoginPage.this, "Enter an email", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                return;
            }

            // Validate password input
            if (TextUtils.isEmpty(password)) {
                Toast.makeText(LoginPage.this, "Enter a password", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                return;
            }

            // Attempt login with Firebase Authentication
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null && user.isEmailVerified()) {
                                Toast.makeText(LoginPage.this, "Login Successful", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(), Dashboard.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(LoginPage.this, "Verify your email before you login",
                                        Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(LoginPage.this, "Login failed.", Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        // Forgot Password button: Navigate to the ForgotPasswordPage
        forgotPasswordButton.setOnClickListener(v -> {
            Intent intent = new Intent(LoginPage.this, ForgotPasswordPage.class);
            startActivity(intent);
        });

        // Adjust layout for system window insets (edge-to-edge experience)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}
