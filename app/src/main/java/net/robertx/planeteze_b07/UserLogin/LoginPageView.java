package net.robertx.planeteze_b07.UserLogin;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

import net.robertx.planeteze_b07.MainActivity;
import net.robertx.planeteze_b07.R;

import com.google.firebase.auth.FirebaseAuth;

public class LoginPageView extends AppCompatActivity implements LoginContract.View {
    private EditText emailaddress_login, password_login;
    private Button loginbutton, forgotPasswordButton, signupForAccountButton, backbutton2;
    private ProgressBar progressBar;

    private LoginContract.Presenter presenter;

    private static final String TAG = "LoginPageViewLoginPageView";

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
        loginbutton = findViewById(R.id.resetPasswordButton);
        forgotPasswordButton = findViewById(R.id.resetPasswordbutton2);
        signupForAccountButton = findViewById(R.id.signupbutton_login2);
        progressBar = findViewById(R.id.progressbar_login);
        backbutton2 = findViewById(R.id.backbutton_login2);

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

    @Override
    public void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void navigateToMainPage() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void navigateToSignUpPage() {
        Intent intent = new Intent(this, SignUpPage.class);
        startActivity(intent);
    }

    @Override
    public void navigateToForgotPasswordPage() {
        Intent intent = new Intent(this, ForgotPasswordPage.class);
        startActivity(intent);
    }

    @Override
    public void navigateToWelcomePage() {
        Intent intent = new Intent(this, WelcomePage.class);
        startActivity(intent);
    }
}
