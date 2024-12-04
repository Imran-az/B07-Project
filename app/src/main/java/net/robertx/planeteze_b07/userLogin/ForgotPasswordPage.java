package net.robertx.planeteze_b07.userLogin;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;

import net.robertx.planeteze_b07.R;

/**
 * ForgotPasswordPage Activity allows users to reset their passwords via email.
 * It provides an interface for users to enter their email and request a password reset link.
 */
public class ForgotPasswordPage extends AppCompatActivity {

    /**
     * Input field for the user's email address.
     */
    public EditText emailAddressInput;

    /**
     * Button to initiate the password reset process.
     */
    public Button resetPasswordButton;

    /**
     * Button to navigate back to the previous page.
     */
    public Button backButton;

    /**
     * Button to navigate to the login page.
     */
    public Button gotoLoginButton;

    /**
     * Initializes the ForgotPasswordPage activity.
     * Sets up the UI, handles user interactions, and integrates Firebase authentication for password reset functionality.
     *<br>
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down,
     *                           this Bundle contains the data it most recently supplied in
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgot_password_page);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        emailAddressInput = findViewById(R.id.emailaddress_forgotpassword);
        resetPasswordButton = findViewById(R.id.resetPasswordButton);
        backButton = findViewById(R.id.backbutton_forgotpassword);
        gotoLoginButton = findViewById(R.id.resetPasswordLogin);

        gotoLoginButton.setOnClickListener(v -> {
            Intent intent = new Intent(ForgotPasswordPage.this, LoginPageView.class);
            startActivity(intent);
            finish();
        });

        resetPasswordButton.setOnClickListener(v -> {
            String email = emailAddressInput.getText().toString().trim();

            if (TextUtils.isEmpty(email)) {
                Toast.makeText(ForgotPasswordPage.this, "Enter an email",
                        Toast.LENGTH_SHORT).show();
                return;
            }

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

        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(ForgotPasswordPage.this, WelcomePage.class);
            startActivity(intent);
            finish();
        });
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}
