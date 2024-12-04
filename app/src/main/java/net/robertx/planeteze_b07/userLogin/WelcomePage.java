package net.robertx.planeteze_b07.userLogin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import net.robertx.planeteze_b07.Dashboard;
import net.robertx.planeteze_b07.R;

/**
 * The WelcomePage activity is the entry point of the application.
 * It provides options for the user to either log in or sign up.
 * If the user is already authenticated, they are redirected to the Dashboard.
 */
public class WelcomePage extends AppCompatActivity {
    /**
     * Button to navigate to the login page.
     */
    Button logininbutton;

    /**
     * Button to navigate to the sign-up page.
     */
    Button signinbutton;

    /**
     * Called when the activity is first created. Initializes the UI components and Firebase.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down,
     *                           this Bundle contains the data it most recently supplied in onSaveInstanceState.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.welcome_page);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser != null) {
            Intent intent = new Intent(WelcomePage.this, Dashboard.class);
            startActivity(intent);
            finish();
            return;
        }

        signinbutton= findViewById(R.id.signupbutton);
        signinbutton.setOnClickListener(v -> {
            Intent intent= new Intent(WelcomePage.this, SignUpPage.class);
            startActivity(intent);
        });

        logininbutton = findViewById(R.id.loginbutton);
        logininbutton.setOnClickListener(v -> {
            Intent intent = new Intent(WelcomePage.this, LoginPageView.class);
            startActivity(intent);
        });
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.welcomepage), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}