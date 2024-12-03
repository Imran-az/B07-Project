package net.robertx.planeteze_b07.UserLogin;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import net.robertx.planeteze_b07.R;

/**
 * ForgotPasswordPage Activity allows users to reset their passwords via email.
 * It provides an interface for users to enter their email and request a password reset link.
 */
public class ForgotPasswordPage extends AppCompatActivity {

    // UI components for email input and buttons
    public EditText emailAddressInput;
    public Button resetPasswordButton;
    public Button backButton, gotoLoginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgot_password_page);

        // Adjust the layout for system window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize UI components
        emailAddressInput = findViewById(R.id.emailaddress_forgotpassword);
        resetPasswordButton = findViewById(R.id.resetPasswordButton);
        backButton = findViewById(R.id.backbutton_forgotpassword);
        gotoLoginButton = findViewById(R.id.resetPasswordLogin);

        gotoLoginButton.setOnClickListener(v -> {
            // Navigate to the LoginPage activity
            Intent intent = new Intent(ForgotPasswordPage.this, LoginPageView.class);
            startActivity(intent);
            finish();
        });

        // Set up the Reset Password button functionality
        resetPasswordButton.setOnClickListener(v -> {
            // Get the entered email address
            String email = emailAddressInput.getText().toString().trim();

            // Validate email input
            if (TextUtils.isEmpty(email)) {
                Toast.makeText(ForgotPasswordPage.this, "Enter an email",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            // Send a password reset email using FirebaseAuth
            FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(ForgotPasswordPage.this,
                                    "Password reset email sent", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ForgotPasswordPage.this,
                                    "Failed to send password reset email", Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        // Set up the Back button functionality
        backButton.setOnClickListener(v -> {
            // Navigate back to the WelcomePage activity
            Intent intent = new Intent(ForgotPasswordPage.this, WelcomePage.class);
            startActivity(intent);
            finish();
        });
        // Adjust layout padding for system window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}
