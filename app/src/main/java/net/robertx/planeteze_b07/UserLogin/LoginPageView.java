package net.robertx.planeteze_b07.UserLogin;

import android.content.Intent;
import android.os.Bundle;
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

import net.robertx.planeteze_b07.Dashboard;
import net.robertx.planeteze_b07.R;

import com.google.firebase.auth.FirebaseAuth;

/**
 * This class represents the login page view and implements the LoginContract.View.
 * It handles user interactions and delegates tasks to the presenter.
 */
public class LoginPageView extends AppCompatActivity implements LoginContract.View {
    private EditText emailaddress_login, password_login;
    private ProgressBar progressBar;

    private LoginContract.Presenter presenter;

    /**
     * Called when the activity is first created. Initializes the UI components, Firebase, and the presenter.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down,
     *                           this Bundle contains the data it most recently supplied in onSaveInstanceState.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login_page);

        // Adjust layout for system window insets (edge-to-edge experience)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize Firebase, Model, and Presenter
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        LoginModel model = new LoginModel(mAuth);
        presenter = new LoginPresenter(this, model);

        // Initialize UI components
        emailaddress_login = findViewById(R.id.emailaddress_login2);
        password_login = findViewById(R.id.emailaddress_forgotpassword);
        Button loginbutton = findViewById(R.id.resetPasswordButton);
        Button forgotPasswordButton = findViewById(R.id.resetPasswordbutton2);
        Button signupForAccountButton = findViewById(R.id.signupbutton_login2);
        progressBar = findViewById(R.id.progressbar_login);
        Button backbutton2 = findViewById(R.id.backbutton_login2);

        // Set button listeners
        backbutton2.setOnClickListener(v -> presenter.onBackButtonClicked());
        signupForAccountButton.setOnClickListener(v -> presenter.onSignUpButtonClicked());
        forgotPasswordButton.setOnClickListener(v -> presenter.onForgotPasswordButtonClicked());
        loginbutton.setOnClickListener(v -> {
            String email = emailaddress_login.getText().toString();
            String password = password_login.getText().toString();
            presenter.onLoginButtonClicked(email, password);
        });
    }

    /**
     * Shows the progress bar to indicate a loading state.
     */
    @Override
    public void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
    }

    /**
     * Hides the progress bar to indicate the end of a loading state.
     */
    @Override
    public void hideProgress() {
        progressBar.setVisibility(View.GONE);
    }

    /**
     * Displays a toast message to the user.
     *
     * @param message The message to display.
     */
    @Override
    public void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Navigates the user to the main page of the app.
     */
    @Override
    public void navigateToMainPage() {
        Intent intent = new Intent(this, Dashboard.class);
        startActivity(intent);
        finish();
    }

    /**
     * Navigates the user to the sign-up page.
     */
    @Override
    public void navigateToSignUpPage() {
        Intent intent = new Intent(this, SignUpPage.class);
        startActivity(intent);
    }

    /**
     * Navigates the user to the forgot password page.
     */
    @Override
    public void navigateToForgotPasswordPage() {
        Intent intent = new Intent(this, ForgotPasswordPage.class);
        startActivity(intent);
    }

    /**
     * Navigates the user to the welcome page.
     */
    @Override
    public void navigateToWelcomePage() {
        Intent intent = new Intent(this, WelcomePage.class);
        startActivity(intent);
    }
}
